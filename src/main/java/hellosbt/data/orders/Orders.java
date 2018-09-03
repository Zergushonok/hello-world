package hellosbt.data.orders;

import java.util.Collection;

/**
 * An abstraction that represents a collection of trade orders
 */

public interface Orders {

  Collection<Order> getOrders();
}
