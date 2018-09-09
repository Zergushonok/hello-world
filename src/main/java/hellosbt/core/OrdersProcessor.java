package hellosbt.core;

import hellosbt.data.clients.Clients;
import hellosbt.data.orders.Orders;
import java.util.function.BiFunction;

/**
 * Functional interface that processes Clients and Orders in some way that may result
 * in modifications to the Clients, which are then returned to the caller.
 * Follows the contract of the Java's BiFunction,
 * delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface OrdersProcessor<C, O> extends BiFunction<Clients<C>, Orders<O>, Clients<C>> {

}
