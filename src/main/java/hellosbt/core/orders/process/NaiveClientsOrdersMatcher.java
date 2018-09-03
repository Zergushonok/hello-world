package hellosbt.core.orders.process;

import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.orders.Order.Type.BUY;
import static hellosbt.data.orders.Order.Type.SELL;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.OrdersProcessor;
import hellosbt.data.assets.Asset;
import hellosbt.data.clients.Assets;
import hellosbt.data.clients.AssetsHolder;
import hellosbt.data.clients.Client;
import hellosbt.data.orders.Order;
import hellosbt.data.orders.Orders;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * A naive implementation of OrdersProcessor that looks for matching orders from different clients
 * in the input clients list, emulates a trade between these clients with matching orders,
 * reflecting the changes to clients' balances and asset quantities
 * in the resulting assets instance.
 *
 * This implementation considers the orders to be matching if they:
 * - have the exact same financial parameters (price per asset and the traded quantity)
 * - belong to different clients
 * - have different types (e.g. buy vs sell or vice versa)
 *
 * It does not pay any attention to whether or not a client will be in debt
 * (financially or by one or more of his assets) after any of his transactions.
 * Likewise, it does not prohibit clients that are in debt to make transactions.
 * Basically, every client has an infinite loan on this stock exchange, and no loan sharks, YAY!
 *
 * Orders are processed in the same order they were supplied. Which means that if the client C1
 * places a purchase order, and the clients C3 and C2 place matching sell orders one after another,
 * only the C3's order will be matched as considered to be the first one chronologically.
 *
 * This implementation should not process any order more than once.
 * The runtime will probably be around O(n^2) in the worst case when we have no matching
 * orders at all.
 */

@Service @Profile({DEFAULT, TEST})
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class NaiveClientsOrdersMatcher implements OrdersProcessor {

  @Autowired TransactionProcessor transactionProcessor;

  @Override
  public Assets apply(Assets assets, Orders orders) {

    Map<String, AssetsHolder> assetsByClients = assets.getHoldersByNames();

    Collection<Order> ordersToProcess = orders.getOrders();
    List<Order> processedOrders = new ArrayList<>();

    ordersToProcess.stream()
        .filter(order -> assetsByClients.containsKey(order.getHolder().getName()))
        .forEachOrdered(order ->
            lookForMatchAndProcess(order, ordersToProcess, processedOrders, assetsByClients));


    /* todo: This impl ignores orders from unknown clients,
    allow configuring to handle differently, e.g. by throwing an exception in such a case */
    return assets;
  }

  private void lookForMatchAndProcess(Order order,
                                      Collection<Order> ordersToProcess,
                                      List<Order> processedOrders,
                                      Map<String, AssetsHolder> assetsByClients) {

    if (isNotYetProcessed(order, processedOrders)) {
      ordersToProcess.stream()
          .filter(orderToMatch -> doOrdersMatch(order, orderToMatch, processedOrders))
          .findFirst()
          .ifPresent(matchedOrder ->
              processMatched(order, matchedOrder, processedOrders, assetsByClients));
    }
  }

  private boolean doOrdersMatch(Order order, Order orderToMatch,
                                Collection<Order> processedOrders) {

    return !isSameClient(order, orderToMatch)
        && isNotYetProcessed(orderToMatch, processedOrders)
        && isSameAsset(order, orderToMatch)
        && areTypesComplementary(order, orderToMatch)
        && doOrdersMatch(order, orderToMatch);
  }

  private boolean isSameAsset(Order order, Order orderToMatch) {
    return Objects.equals(orderToMatch.getAsset(), order.getAsset());
  }

  private boolean isNotYetProcessed(Order orderToMatch, Collection<Order> processedOrders) {
    return !processedOrders.contains(orderToMatch);
  }

  private boolean isSameClient(Order order, Order orderToMatch) {
    return Objects.equals(orderToMatch.getHolder(), order.getHolder());
  }

  //todo: will work only when we have two types of orders, buy and sell, should be generalized
  private boolean areTypesComplementary(Order order, Order orderToMatch) {
    return orderToMatch.getType() != order.getType();
  }

  private boolean doOrdersMatch(Order order, Order orderToMatch) {
    return Objects.equals(order.getPrice() * order.getQuantity(),
        orderToMatch.getPrice() * orderToMatch.getQuantity());
  }

  private void processMatched(Order order, Order matchedOrder, List<Order> processedOrders,
                              Map<String, AssetsHolder> assetsByClients) {
    processedOrders.add(order);
    processedOrders.add(matchedOrder);

    String initiator = order.getHolder().getName();
    String follower = matchedOrder.getHolder().getName();

    AssetsHolder initiatorAssets = assetsByClients.get(initiator);
    AssetsHolder followerAssets = assetsByClients.get(follower);

    assetsByClients.replace(initiator, transactionProcessor.apply(initiatorAssets, order));
    assetsByClients.replace(follower, transactionProcessor.apply(followerAssets, matchedOrder));
  }

  //todo: extract functions for re-use, use via lambdas

  /**
   * A function that emulates a sell or a purchase.
   * It constructs a new instance of a Client with an updated balance, depending on the nature
   * of the trade order.
   */
  @Component @Profile({DEFAULT, TEST})
  public static class TransactionProcessor
      implements BiFunction<AssetsHolder, Order, AssetsHolder> {

    @Override
    public AssetsHolder apply(AssetsHolder assetsHolder, Order order) {

      int orderValue = order.getPrice() * order.getQuantity();
      return Client.of(
          assetsHolder.getName(),
          assetsHolder.getWealth()
              + (order.getType() == SELL ? orderValue : -orderValue),
          process(assetsHolder.getAssets(), order));
    }

    private Map<Asset, Integer> process(Map<Asset, Integer> assets, Order order) {
      assets.compute(order.getAsset(),
          (asset, quantity) -> {
            Integer orderedQuantity = order.getQuantity();
            return quantity + (order.getType() == BUY ? orderedQuantity : -orderedQuantity);
          });
      return assets;
    }
  }
}