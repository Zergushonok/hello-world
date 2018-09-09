package hellosbt.readers;

import static com.google.common.collect.Iterables.getOnlyElement;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static hellosbt.data.orders.TradeOrderSignature.of;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;
import hellosbt.BaseTest;
import hellosbt.core.orders.read.OrdersFromTabSeparatedLinesConverter;
import hellosbt.data.assets.TradeableGood;
import hellosbt.data.orders.Orders;
import hellosbt.data.orders.TradeOrder;
import hellosbt.data.orders.TradeOrderSignature;
import lombok.experimental.FieldDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
public class OrdersFromTabSeparatedLinesConverterTest extends BaseTest {

  @Autowired OrdersFromTabSeparatedLinesConverter converter;

  //todo: negative cases
  @Test
  public void correctLinesAreConvertedToOrders() {

    String line1 = "C1\tb\tA\t7\t12";
    String line2 = "C2\tb\tB\t8\t2";
    String line3 = "C3\ts\tC\t1\t1";

    TradeableGood a = TradeableGood.of("A");
    TradeableGood b = TradeableGood.of("B");
    TradeableGood c = TradeableGood.of("C");

    Orders<Multimap<TradeOrderSignature, TradeOrder>> orders =
        converter.apply(asList(line1, line2, line3));

    assertThat(orders.getOrderTypes()).containsExactlyInAnyOrder(BUY, SELL);

    Multimap<TradeOrderSignature, TradeOrder> buyOrders = orders.getOrders(BUY);
    assertThat(buyOrders.asMap()).containsOnlyKeys(
        of(a, 7 * 12),
        of(b, 8 * 2));

    Multimap<TradeOrderSignature, TradeOrder> sellOrders = orders.getOrders(SELL);
    assertThat(sellOrders.asMap()).containsOnlyKeys(
        of(c, 1));

    TradeOrder order_7_12 = getOnlyElement(buyOrders.get(of(a, 7 * 12)));
    assertThat(order_7_12.getClient()).isEqualTo("C1");
    assertThat(order_7_12.getType()).isSameAs(BUY);
    assertThat(order_7_12.getAsset()).isEqualTo(a);
    assertThat(order_7_12.getPrice()).isEqualTo(7);
    assertThat(order_7_12.getQuantity()).isEqualTo(12);
    assertThat(order_7_12.getSum()).isEqualTo(7 * 12);

    TradeOrder order_8_2 = getOnlyElement(buyOrders.get(of(b, 8 * 2)));
    assertThat(order_8_2.getClient()).isEqualTo("C2");
    assertThat(order_8_2.getType()).isSameAs(BUY);
    assertThat(order_8_2.getAsset()).isEqualTo(b);
    assertThat(order_8_2.getPrice()).isEqualTo(8);
    assertThat(order_8_2.getQuantity()).isEqualTo(2);
    assertThat(order_8_2.getSum()).isEqualTo(8 * 2);

    TradeOrder order_1_1 = getOnlyElement(sellOrders.get(of(c, 1)));
    assertThat(order_1_1.getClient()).isEqualTo("C3");
    assertThat(order_1_1.getType()).isSameAs(SELL);
    assertThat(order_1_1.getAsset()).isEqualTo(c);
    assertThat(order_1_1.getPrice()).isEqualTo(1);
    assertThat(order_1_1.getQuantity()).isEqualTo(1);
    assertThat(order_1_1.getSum()).isEqualTo(1);
  }
}
