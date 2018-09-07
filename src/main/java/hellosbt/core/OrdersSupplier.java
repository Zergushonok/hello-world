package hellosbt.core;

import hellosbt.data.Orders;
import java.util.function.Supplier;

/**
 * Functional interface that supplies Orders to some destination.
 * Follows semantics of the Java's Supplier interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface OrdersSupplier<O> extends Supplier<Orders<O>> {

}
