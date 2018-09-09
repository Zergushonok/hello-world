package hellosbt.data.orders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.MultimapBuilder.hashKeys;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import hellosbt.data.assets.Asset;
import hellosbt.data.orders.Order.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * An implementation of orders that represents them as two trees -
 * for buying and for selling orders respectively.
 *
 * The first level of the tree is for unique target assets,
 * the second level - for distinct order sums (price * quantity).
 * Each sum is associated with the list of orders that have this sum and this asset.
 *
 * Example:
 *  BUY
 * |   \
 * A    B ---
 * | \   \   \
 * 10 20  10  18
 * |   |   |   |
 * a1  a3  b1  b3
 * a2  a4  b2
 *     a5
 *
 * While this implementation provides the constructor that accepts the whole structure,
 * it is more convenient to use the static "of" method that accepts a simple list of orders
 * to obtain an instance of this class.
 *
 * The encounter order of lists of orders, associated with each sum for each asset for each type,
 * will be the same as the encounter order of these orders in the input orders list.
 *
 * For example, o_buy_A_10_2, o_buy_A_20_1, o_buy_A_10_2 from the input list will be stored
 * in this exact order in the list of orders associated with the sum 20, asset A, type BUY.
 * Thus the chronological sequence of orders is preserved inside each group.
 *
 * getOrderTypes and getOrders return read-only views of types and assets collections.
 */

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@ToString
public class OrdersByAssetsByType
    implements Orders<Map<Asset, Multimap<Integer, TradeOrder>>> {

  Map<Type, Map<Asset, Multimap<Integer, TradeOrder>>> orders;

  public static OrdersByAssetsByType of(List<TradeOrder> rawOrdersList) {

    Map<Asset, Multimap<Integer, TradeOrder>> buyingOrdersByAsset = newHashMap();
    Map<Asset, Multimap<Integer, TradeOrder>> sellingOrdersByAsset = newHashMap();

    Map<Type, Map<Asset, Multimap<Integer, TradeOrder>>> orders = ImmutableMap.of(
        BUY, buyingOrdersByAsset, SELL, sellingOrdersByAsset);

    rawOrdersList.forEach(order ->
        storeOrderToOneOfTheMaps(order, buyingOrdersByAsset, sellingOrdersByAsset));

    return new OrdersByAssetsByType(orders);
  }

  private static void storeOrderToOneOfTheMaps(

      TradeOrder orderToStore,
      Map<Asset, Multimap<Integer, TradeOrder>> buyingOrdersByAsset,
      Map<Asset, Multimap<Integer, TradeOrder>> sellingOrdersByAsset) {

    TradeOrder.Type type = orderToStore.getType();
    Asset asset = orderToStore.getAsset();

    checkArgument(type == BUY || type == SELL,
        "This structure can only hold orders of types %s and %s, "
            + "but an order of type %s has been encountered", BUY, SELL, type);

    (type == BUY ? buyingOrdersByAsset : sellingOrdersByAsset).compute(
        asset, (asset_, ordersBySum) ->
            storeOrderAccordingToItsSum(orderToStore, ordersBySum));
  }

  private static Multimap<Integer, TradeOrder> storeOrderAccordingToItsSum(

      TradeOrder orderToStore,
      Multimap<Integer, TradeOrder> ordersBySum) {

    return ofNullable(ordersBySum)
        .map(it -> associateWithSum(orderToStore, it))
        .orElseGet(() -> associateWithSum(orderToStore, newStorage()));
  }

  private static Multimap<Integer, TradeOrder> associateWithSum(

      TradeOrder orderToStore,
      Multimap<Integer, TradeOrder> ordersBySum) {

    ordersBySum.put(orderToStore.getSum(), orderToStore);
    return ordersBySum;
  }

  private static Multimap<Integer, TradeOrder> newStorage() {
    return hashKeys().arrayListValues().build();
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
