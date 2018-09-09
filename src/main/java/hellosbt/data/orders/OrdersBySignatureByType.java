package hellosbt.data.orders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.MultimapBuilder.hashKeys;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static java.util.Collections.unmodifiableSet;
import static lombok.AccessLevel.PRIVATE;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import hellosbt.data.orders.Order.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * An implementation of orders that represents them as two multimaps --
 * for buying and for selling orders respectively.
 *
 * In each of the multimaps the orders are grouped by their Signatures,
 * a Signature is a compound key of the order's asset and sum.
 *
 * While this implementation provides the constructor that accepts the whole structure,
 * it is more convenient to use the static "of" method, that accepts a simple list of orders,
 * to obtain an instance of this class.
 *
 * The encounter order of lists of orders, associated with each signature for each type,
 * will be the same as their encounter order in the input orders list.
 *
 * For example, o_buy_A_10_2, o_buy_A_20_1, o_buy_A_10_2 from the input list will be stored
 * in this exact order in the list of buying orders with the signature {A, 20}.
 * Thus the chronological sequence of orders is preserved inside each group.
 *
 * getOrderTypes returns a read-only view of types stored in the structure.
 * getOrders(Type) returns a mutable multimap of orders of this type --
 *   this implementations does not safeguard this multimap of orders
 *   from being populated with the order of a different type,
 *   thus making the whole structure invalid.
 *   //todo: introduce a safety check against that or make the structure compl. immutable
 *   Users of this structure should take care
 *   of such an illegal modification's possibility on their own.
 */

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@ToString
public class OrdersBySignatureByType
    implements Orders<Multimap<TradeOrderSignature, TradeOrder>> {

  Map<Type, Multimap<TradeOrderSignature, TradeOrder>> orders;

  public static OrdersBySignatureByType of(List<TradeOrder> rawOrdersList) {

    Multimap<TradeOrderSignature, TradeOrder> buyingOrders = newStorage();
    Multimap<TradeOrderSignature, TradeOrder> sellingOrders = newStorage();

    Map<Type, Multimap<TradeOrderSignature, TradeOrder>> orders =
        ImmutableMap.of(BUY, buyingOrders, SELL, sellingOrders);

    rawOrdersList.forEach(order ->
        storeOrderToOneOfTheMaps(order, buyingOrders, sellingOrders));

    return new OrdersBySignatureByType(orders);
  }

  private static void storeOrderToOneOfTheMaps(

      TradeOrder orderToStore,
      Multimap<TradeOrderSignature, TradeOrder> buyingOrders,
      Multimap<TradeOrderSignature, TradeOrder> sellingOrders) {

    TradeOrder.Type type = orderToStore.getType();
    checkArgument(type == BUY || type == SELL,
        "This structure can only hold orders of types %s and %s, "
            + "but an order of type %s has been encountered", BUY, SELL, type);

    (type == BUY ? buyingOrders : sellingOrders)
        .put(orderToStore.getSignature(), orderToStore);
  }

  private static Multimap<TradeOrderSignature, TradeOrder> newStorage() {
    return hashKeys().arrayListValues().build();
  }

  @Override
  public Multimap<TradeOrderSignature, TradeOrder> getOrders(Type type) {
    return orders.get(type);
  }

  @Override
  public Set<Type> getOrderTypes() {
    return unmodifiableSet(orders.keySet());
  }
}
