package hellosbt.data.orders;

import hellosbt.data.orders.Order.Type;
import java.util.Set;

/**
 * Represents a number of orders.
 * The implementations should specify exactly how the orders are stored inside this structure,
 * as well as provide ways to extract them.
 *
 * This contract allows reading orders only by a specified type.
 * To learn what types of orders exist in the underlying structure,
 * the getOrderTypes method should be used.
 * It is expected that getOrderTypes does not lie.
 *
 * The underlying data structure is NOT required to be immutable, but this contract
 * intentionally does not provide any info on how the orders inside can be updated.
 */

public interface Orders<T> {

  T getOrders(Order.Type type);
  Set<Type> getOrderTypes();
}
