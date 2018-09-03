package hellosbt.core.orders;

import hellosbt.data.orders.Orders;
import java.util.function.Supplier;

@FunctionalInterface
public interface OrdersSupplier extends Supplier<Orders> {

}
