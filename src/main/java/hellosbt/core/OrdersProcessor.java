package hellosbt.core;

import hellosbt.data.assets.Assets;
import hellosbt.data.orders.Orders;
import java.util.function.BiFunction;

/**
 * Functional interface that processes Assets and Orders in some way which may result
 * in modifications to the Assets object that is in the end returned to the caller.
 * Follows semantics of the Java's BiFunction interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface OrdersProcessor extends BiFunction<Assets, Orders, Assets> {

}
