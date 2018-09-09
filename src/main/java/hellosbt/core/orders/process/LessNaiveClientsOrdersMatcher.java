package hellosbt.core.orders.process;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;

import com.google.common.collect.Multimap;
import hellosbt.core.OrdersProcessor;
import hellosbt.data.assets.Asset;
import hellosbt.data.clients.Client;
import hellosbt.data.clients.Clients;
import hellosbt.data.orders.Order;
import hellosbt.data.orders.Orders;
import hellosbt.data.orders.TradeOrder;
import hellosbt.data.orders.TradeOrderSignature;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A less naive implementation of OrdersProcessor.
 *
 * Looks for matching orders from different clients from the input clients data structure
 * and emulates trade deals between clients with matching orders.
 *
 * Changes to the clients' balances and asset quantities are reflected
 * in the resulting clients data structure.
 *
 * This processor expects to work with the clients presented as by-name-maps,
 * and with the orders data presented as two multimaps of orders (for buying and for selling),
 * grouped by signatures (i.e. by sum and asset).
 * (see OrdersBySignatureByType data structure documentation for details).
 *
 * In short the logic of matching is as follows:
 * - Among the multimaps of buying and selling orders, the one with less entries is selected
 * - For each order in this multimap, the counterpart multimap is searched for a matching order
 *   - Only orders with the same signature (i.e. with the same asset and sum) are considered
 *   - Among those, only the orders with the same asset and sum are considered
 *     - We need this check because different orders may still have the same signature
 *   - The orders are matched if their prices and quantities are equal
 *   - If there are several possible matches, the first encountered is selected
 * - The sums and quantities of assets for the clients who make a deal are updated accordingly
 * - The processed match is removed from the counterpart multimap
 * so that it will not be encountered again.
 *
 * As a result, if there are no matches at all, the runtime will be O(L) where L is the size
 * of a lesser multimap (O(n/2) => O(n) in an average case).
 *
 * In a more general case of more or less evenly populated multimaps,
 * the runtime can be approximated as:
 *   O(L) * O(B/A/S), where
 *   - L is the size of a lesser multimap
 *   - B is the size of a bigger counterpart multimap
 *   - A is the number of traded assets
 *   - S is the avg. number of distinct sums of orders for each asset
 *
 * which should approximate to:
 *   O(n/2) * O((n/2) / (A/S)) => O(n) * O(n/A/S) => O(n)
 *
 * This implementation works using a single thread, as it has proved to be fast enough
 * on the sample data, even without paralleling of the processing.
 */

//todo: this matcher can most certainly be decomposed into several components,
//  each doing its part of the processing logic.
//  As of now, this function stretches the limits of the SRP a little too much for my liking

@Service @Profile({DEFAULT, TEST})
@NoArgsConstructor
@Slf4j
public class LessNaiveClientsOrdersMatcher implements OrdersProcessor
    <Map<String, Client>,
        Multimap<TradeOrderSignature, TradeOrder>> {

  @Override
  public Clients<Map<String, Client>> apply(

      Clients<Map<String, Client>> clients,
      Orders<Multimap<TradeOrderSignature, TradeOrder>> orders) {

    log.info("Matching orders between {} clients", clients.getClients().size());

    //todo: move to a validator
    checkArgument(orders.getOrderTypes().stream()
        .allMatch(type -> type == BUY || type == SELL),
        "This order processor works only with the trade orders of types %s and %s, "
            + "but it have found other order types in the supplied input", BUY, SELL);

    return matchOrdersAndUpdateClients(orders, clients);
  }

  private Clients<Map<String, Client>> matchOrdersAndUpdateClients(

      Orders<Multimap<TradeOrderSignature, TradeOrder>> orders,
      Clients<Map<String, Client>> clients) {

    Multimap<TradeOrderSignature, TradeOrder> buyOrders = orders.getOrders(BUY);
    Multimap<TradeOrderSignature, TradeOrder> sellOrders = orders.getOrders(SELL);

    Multimap<TradeOrderSignature, TradeOrder> ordersToIterate =
        buyOrders.size() < sellOrders.size()
            ? buyOrders : sellOrders;

    Multimap<TradeOrderSignature, TradeOrder> ordersToMatch =
        ordersToIterate == buyOrders
            ? sellOrders : buyOrders;

    log.debug("There are {} buying orders and {} selling orders, iterating over the smaller bunch",
        buyOrders.size(), sellOrders.size());

    iterateOrdersFindMatches(
        ordersToIterate, ordersToMatch, clients.getClients());

    log.info("Orders matching completed, there are still {} clients on the floor",
        clients.getClients().size());
    return clients;
  }

  private void iterateOrdersFindMatches(

      Multimap<TradeOrderSignature, TradeOrder> ordersToIterate,
      Multimap<TradeOrderSignature, TradeOrder> ordersToMatch,
      Map<String, Client> clientsByName) {

    //todo: theoretically, here is a potential for parallel processing
    //  we can safely analyze orders with different signatures in parallel,
    //  as their respective matches will never intersect
    //
    //  For the test data of 8k+ orders, however, introducing a level of parallelism here
    //  brought no benefit. It is possible that this will change for larger data sets.

    ordersToIterate.forEach((signature, order) ->
        matchBySignature(order, ordersToMatch, clientsByName));
  }

  private void matchBySignature(

      TradeOrder order,
      Multimap<TradeOrderSignature, TradeOrder> ordersToMatch,
      Map<String, Client> clientsByName) {

    matchOrder(order,
        findMatchCandidatesBySignature(order, ordersToMatch),
        clientsByName);
  }

  private Collection<TradeOrder> findMatchCandidatesBySignature(

      TradeOrder order,
      Multimap<TradeOrderSignature, TradeOrder> ordersToMatch) {

    return ordersToMatch.get(order.getSignature());
  }

  //todo: If we are to process the list of orders for the same asset in parallel
  //  Some synchronization will be needed here, as we are doing a structural modification
  private void matchOrder(

      TradeOrder order,
      Collection<TradeOrder> matchCandidates,
      Map<String, Client> clientsByName) {

    log.trace("Looking for a match for the order {} among {} candidates",
        order, matchCandidates.size());

    Iterator<TradeOrder> candidates = matchCandidates.iterator();
    while (candidates.hasNext()) {
      TradeOrder candidate = candidates.next();

      if (doOrdersMatch(order, candidate)) {
        log.debug("Orders {} and {} match", order, candidate);

        updateAffectedClients(order, candidate, clientsByName);
        candidates.remove();

        log.trace("Order {} has been matched and will no longer be considered", candidate);
      }
    }
    log.trace("No match found for the order {}", order);
  }

  private boolean doOrdersMatch(TradeOrder first, TradeOrder second) {
    sanityCheck(first, second);

    /* It is still necessary to validate that the assets and sums are equal
     * because hash codes of different signatures may be identical */

    return !Objects.equals(first.getClient(), second.getClient())
        && Objects.equals(first.getAsset(), second.getAsset())
        && Objects.equals(first.getSum(), second.getSum())
        && Objects.equals(first.getPrice(), second.getPrice())
        && Objects.equals(first.getQuantity(), second.getQuantity());
  }

  //todo: should probably be extracted from here
  private void sanityCheck(TradeOrder first, TradeOrder second) {

    checkArgument(first.getType() != second.getType(),
        "This order processor expects that input orders "
            + "are divided into two structures, "
            + "with buying orders in one and selling orders in another. "
            + "However, it has encountered orders of identical types "
            + "when comparing two orders from these two structures; "
            + "orders are: %s and %s. "
            + "Most likely, something went horribly wrong "
            + "with reading orders data from its source, "
            + "or the structure has been illegally modified at runtime", first, second);
  }

  private void updateAffectedClients(TradeOrder order, TradeOrder candidate,
                                     Map<String, Client> clientsByName) {

    Client initiator = clientsByName.get(order.getClient());
    Client acceptor = clientsByName.get(candidate.getClient());

    deal(initiator, acceptor, order);
  }

  private void deal(Client initiator, Client acceptor, TradeOrder order) {

    Order.Type orderType = order.getType();
    Asset tradedAsset = order.getAsset();
    int tradedQuantity = order.getQuantity();
    int sum = order.getSum();

    initiator.modifyBalance(orderType == BUY ? -sum : sum);
    acceptor.modifyBalance(orderType == BUY ? sum : -sum);

    initiator.modifyAssetQuantity(tradedAsset,
        orderType == BUY
            ? tradedQuantity
            : -tradedQuantity);

    acceptor.modifyAssetQuantity(tradedAsset,
        orderType == BUY
            ? -tradedQuantity
            : tradedQuantity);

    log.debug("Deal between {} and {}; asset: {}, traded: {}, sum: {}",
        initiator, acceptor, tradedAsset, tradedQuantity, sum);
  }
}