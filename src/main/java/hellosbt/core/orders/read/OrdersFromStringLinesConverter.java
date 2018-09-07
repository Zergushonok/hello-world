package hellosbt.core.orders.read;

import hellosbt.data.Orders;
import java.util.Collection;
import java.util.function.Function;

/**
 * Functional interface that transforms the input Collection of String lines into Orders.
 * Follows the Java's Function semantics, delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface OrdersFromStringLinesConverter<O> extends Function<Collection<String>, Orders<O>> {

}
