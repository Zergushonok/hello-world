package hellosbt.core.orders.process;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.TradeOrder.Type.BUY;
import static hellosbt.data.TradeOrder.Type.SELL;
import static lombok.AccessLevel.PRIVATE;

import com.google.common.collect.Multimap;
import hellosbt.core.OrdersProcessor;
import hellosbt.data.Asset;
import hellosbt.data.Client;
import hellosbt.data.Clients;
import hellosbt.data.Order;
import hellosbt.data.Orders;
import hellosbt.data.TradeOrder;
import hellosbt.data.TradeOrder.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A less naive implementation of OrdersProcessor that looks for matching orders from different clients
 * in the input clients list, emulates a trade between these clients with matching orders,
 * reflecting the changes to clients' balances and asset quantities
 * in the resulting clients instance.
 */

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

    //todo: extract into a validator
    checkArgument(orders.getOrderTypes().stream()
        .allMatch(type -> type == BUY || type == SELL),
        "This order processor works only with the trade orders of types %s and %s, "
            + "but it have found other order types in the supplied input", BUY, SELL);

    Map<String, Client> clientsByName = clients.getClients();

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

    ordersToIterate.forEach((asset, ordersBySum) ->
        ordersBySum.entries().forEach(orderWithSum -> {

          TradeOrder order = orderWithSum.getValue();
          Integer sum = orderWithSum.getKey();

          Iterator<TradeOrder> matchCandidates =
              ordersToMatch.get(asset).get(sum).iterator();

          while (matchCandidates.hasNext()) {

            TradeOrder candidate = matchCandidates.next();
            if (Objects.equals(candidate.getPrice(), order.getPrice())
                && Objects.equals(candidate.getQuantity(), order.getQuantity())) {

              //do the match
              Client initiator = clientsByName.get(order.getClient().getName());
              Client acceptor = clientsByName.get(candidate.getClient().getName());

              Order.Type orderType = order.getType();
              int tradedQuantity = order.getQuantity();

              initiator.modifyBalance(orderType == Type.BUY ? -sum : sum);
              acceptor.modifyBalance(orderType == Type.BUY ? sum : -sum);

              initiator.modifyAssetQuantity(asset,
                  orderType == Type.BUY
                      ? tradedQuantity
                      : -tradedQuantity);

              acceptor.modifyAssetQuantity(asset,
                  orderType == Type.BUY
                      ? -tradedQuantity
                      : tradedQuantity);

              matchCandidates.remove();
            }
          }
        }));

    return clients;
  }
}