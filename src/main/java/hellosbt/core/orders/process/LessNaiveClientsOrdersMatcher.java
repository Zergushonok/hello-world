package hellosbt.core.orders.process;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMultimap.of;
import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.TradeOrder.Type.BUY;
import static hellosbt.data.TradeOrder.Type.SELL;

import com.google.common.collect.Multimap;
import hellosbt.core.OrdersProcessor;
import hellosbt.data.Asset;
import hellosbt.data.Client;
import hellosbt.data.Clients;
import hellosbt.data.Order;
import hellosbt.data.Orders;
import hellosbt.data.TradeOrder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A less naive implementation of OrdersProcessor that looks for matching orders from different clients
 * in the input clients list, emulates a trade between these clients with matching orders,
 * reflecting the changes to clients' balances and asset quantities
 * in the resulting clients instance.
 */

//todo: this matcher can most certainly be decomposed into several components,
//  each doing its part of the processing logic.
//  As of now, this function stretches the limits of the SRP a little too much for my liking

@Service @Profile({DEFAULT, TEST})
@NoArgsConstructor
@Slf4j
public class LessNaiveClientsOrdersMatcher implements OrdersProcessor
    <Map<String, Client>,
        Map<Asset, Multimap<Integer, TradeOrder>>> {

  @Override
  public Clients<Map<String, Client>> apply(

      Clients<Map<String, Client>> clients,
      Orders<Map<Asset, Multimap<Integer, TradeOrder>>> orders) {

    log.info("Matching orders between {} clients", clients.getClients().size());

    //todo: move to a validator
    checkArgument(orders.getOrderTypes().stream()
        .allMatch(type -> type == BUY || type == SELL),
        "This order processor works only with the trade orders of types %s and %s, "
            + "but it have found other order types in the supplied input", BUY, SELL);

    return matchOrdersAndUpdateClients(orders, clients);
  }

  private Clients<Map<String, Client>> matchOrdersAndUpdateClients(

      Orders<Map<Asset, Multimap<Integer, TradeOrder>>> orders,
      Clients<Map<String, Client>> clients) {

    Map<Asset, Multimap<Integer, TradeOrder>> buyOrders =
        orders.getOrders(BUY);
    Map<Asset, Multimap<Integer, TradeOrder>> sellOrders =
        orders.getOrders(SELL);

    Map<Asset, Multimap<Integer, TradeOrder>> ordersToIterate =
        buyOrders.size() < sellOrders.size()
            ? buyOrders : sellOrders;

    Map<Asset, Multimap<Integer, TradeOrder>> ordersToMatch =
        ordersToIterate == buyOrders
            ? sellOrders : buyOrders;

    iterateOrdersFindMatches(
        ordersToIterate, ordersToMatch, clients.getClients());

    log.info("Orders matching completed, there are still {} clients on the floor",
        clients.getClients().size());

    return clients;
  }

  private void iterateOrdersFindMatches(

      Map<Asset, Multimap<Integer, TradeOrder>> ordersToIterate,
      Map<Asset, Multimap<Integer, TradeOrder>> ordersToMatch,
      Map<String, Client> clientsByName) {

    //todo: theoretically, here is a potential for parallel processing
    //  we can safely analyze orders for different assets in parallel,
    //  as their respective matches will never intersect
    //
    //  For the test data of 8k+ orders, however, introducing a level of parallelism here
    //  brought no benefit. It is possible that this will change for larger data sets.

    ordersToIterate.forEach((asset, ordersBySum) ->
        matchOrdersForAsset(ordersBySum, ordersToMatch, clientsByName));
  }

  private void matchOrdersForAsset(

      Multimap<Integer, TradeOrder> thisAssetOrdersBySum,
      Map<Asset, Multimap<Integer, TradeOrder>> ordersToMatch,
      Map<String, Client> clientsByName) {

    thisAssetOrdersBySum.entries().forEach(orderWithSum ->
        matchOrder(orderWithSum, ordersToMatch, clientsByName));
  }

  private void matchOrder(

      Entry<Integer, TradeOrder> orderWithSum,
      Map<Asset, Multimap<Integer, TradeOrder>> ordersToMatch,
      Map<String, Client> clientsByName) {

    TradeOrder order = orderWithSum.getValue();
    int sum = orderWithSum.getKey();

    Collection<TradeOrder> candidates = findMatchCandidatesByAssetAndSum(
        order.getAsset(), sum, ordersToMatch);

    matchOrder(order, candidates, clientsByName);
  }

  private Collection<TradeOrder> findMatchCandidatesByAssetAndSum(

      Asset asset, int sum,
      Map<Asset, Multimap<Integer, TradeOrder>> ordersToMatch) {

    return ordersToMatch.getOrDefault(asset, of()).get(sum);
  }

  //todo: If we are to process the list of orders for the same asset in parallel
  //  Some synchronization will be needed here, as we are doing a structural modification
  private void matchOrder(

      TradeOrder order,
      Collection<TradeOrder> matchCandidates,
      Map<String, Client> clientsByName) {

    Iterator<TradeOrder> candidates = matchCandidates.iterator();
    while (candidates.hasNext()) {
      TradeOrder candidate = candidates.next();

      if (doOrdersMatch(order, candidate)) {
        updateAffectedClients(order, candidate, clientsByName);
        candidates.remove();
      }
    }
  }

  private boolean doOrdersMatch(TradeOrder first, TradeOrder second) {
    sanityCheck(first, second);

    return !Objects.equals(second.getClient(), first.getClient())
        && Objects.equals(second.getPrice(), first.getPrice())
        && Objects.equals(second.getQuantity(), first.getQuantity());
  }

  //todo: should probably be extracted from here
  private void sanityCheck(TradeOrder first, TradeOrder second) {

    checkArgument(first.getType() != second.getType(),
        "This order processor expects that input orders "
            + "are divided into two structures, "
            + "with buying orders in one and selling orders in another. "
            + "However, it has encountered orders of identical types "
            + "when comparing two orders from these two structures; orders are: %s and %s. "
            + "Most likely, something went horribly wrong "
            + "with reading orders data from its source", first, second);

    checkArgument(first.getSum() == second.getSum(),
        "This order processor expects to compare "
            + "only orders of the same sum (price * quantity). "
            + "However, it has been asked to compare orders "
            + "of different sums; orders are: %s and %s. "
            + "Most likely, something went horribly wrong "
            + "with reading orders data from its source", first, second);

    checkArgument(Objects.equals(first.getAsset(), second.getAsset()),
        "This order processor expects to compare "
            + "only orders for the same asset. "
            + "However, it has been asked to compare orders "
            + "for different assets; orders are: %s and %s. "
            + "Most likely, something went horribly wrong "
            + "with reading orders data from its source", first, second);
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
  }
}