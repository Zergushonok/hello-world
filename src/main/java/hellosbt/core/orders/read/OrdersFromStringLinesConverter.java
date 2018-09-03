package hellosbt.core.orders.read;

import hellosbt.data.orders.Orders;
import java.util.Collection;
import java.util.function.Function;

@FunctionalInterface
public interface OrdersFromStringLinesConverter extends Function<Collection<String>, Orders> {

}
