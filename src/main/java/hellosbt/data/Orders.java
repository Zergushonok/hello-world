package hellosbt.data;

import hellosbt.data.Order.Type;
import java.util.Set;

/**
 * An abstraction that represents a bunch of orders
 */

public interface Orders<T> {

  T getOrders(Order.Type type);
  Set<Type> getOrderTypes();
}
