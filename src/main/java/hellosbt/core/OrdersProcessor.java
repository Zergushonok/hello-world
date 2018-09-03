package hellosbt.core;

import hellosbt.data.assets.Assets;
import hellosbt.data.orders.Orders;
import java.util.function.BiFunction;

@FunctionalInterface
public interface OrdersProcessor extends BiFunction<Assets, Orders, Assets> {

}
