package hellosbt.data.orders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.MultimapBuilder.hashKeys;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import hellosbt.data.orders.Order.Type;
import hellosbt.data.assets.Asset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString
public class OrdersByAssetsByType implements Orders<Map<Asset, Multimap<Integer, TradeOrder>>> {

  Map<Type, Map<Asset, Multimap<Integer, TradeOrder>>> orders;

  public static OrdersByAssetsByType of(List<TradeOrder> rawOrdersList) {

    Map<Asset, Multimap<Integer, TradeOrder>> buyingOrdersByAsset = newHashMap();
    Map<Asset, Multimap<Integer, TradeOrder>> sellingOrdersByAsset = newHashMap();

    Map<Type, Map<Asset, Multimap<Integer, TradeOrder>>> orders = ImmutableMap.of(
        BUY, buyingOrdersByAsset, SELL, sellingOrdersByAsset);

    rawOrdersList.forEach(order -> {

      TradeOrder.Type type = order.getType();
      Asset asset = order.getAsset();
      int sum = order.getSum();

      checkArgument(type == BUY || type == SELL,
          "This structure can only hold orders of types %s and %s, "
              + "but an order of type %s has been encountered", BUY, SELL, type);

      (type == BUY ? buyingOrdersByAsset : sellingOrdersByAsset).compute(
          asset, (asset_, ordersBySum) ->
              ofNullable(ordersBySum)

                  .map(it -> {
                    it.put(sum, order);
                    return it;
                  })

                  .orElseGet(() -> {
                    Multimap<Integer, TradeOrder> it = hashKeys().arrayListValues().build();
                    it.put(sum, order);
                    return it;
                  }));
    });

    return new OrdersByAssetsByType(orders);
  }

  @Override
  public Map<Asset, Multimap<Integer, TradeOrder>> getOrders(Type type) {
    return unmodifiableMap(orders.get(type));
    //todo: this ofc does not prevent us from modifying the multimap
  }

  @Override
  public Set<Type> getOrderTypes() {
    return unmodifiableSet(orders.keySet());
  }
}
