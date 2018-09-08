package hellosbt.data.orders;

import hellosbt.data.orders.Order;
import hellosbt.data.orders.Order.Type;
import java.util.Set;

/**
 * An abstraction that represents a bunch of orders
 */

public interface Orders<T> {

  T getOrders(Order.Type type);
  Set<Type> getOrderTypes();
}
