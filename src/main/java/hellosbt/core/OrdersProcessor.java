package hellosbt.core;

import hellosbt.data.Clients;
import hellosbt.data.Orders;
import java.util.function.BiFunction;

/**
 * Functional interface that processes Clients and Orders in some way which may result
 * in modifications to the Clients object that is in the end returned to the caller.
 * Follows semantics of the Java's BiFunction interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface OrdersProcessor<C, O> extends BiFunction<Clients<C>, Orders<O>, Clients<C>> {

}
