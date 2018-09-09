package hellosbt.core;

import hellosbt.data.orders.Orders;
import java.util.function.Supplier;

/**
 * Functional interface that supplies Orders to some destination.
 * Follows the contract of the Java's Supplier,
 * delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface OrdersSupplier<O> extends Supplier<Orders<O>> {

}
