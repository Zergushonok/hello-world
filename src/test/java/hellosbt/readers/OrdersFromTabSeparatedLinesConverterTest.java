package hellosbt.readers;

import static com.google.common.collect.Iterables.get;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static hellosbt.data.assets.TradeableGood.of;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;
import hellosbt.BaseTest;
import hellosbt.core.orders.read.OrdersFromTabSeparatedLinesConverter;
import hellosbt.data.assets.Asset;
import hellosbt.data.orders.Orders;
import hellosbt.data.orders.TradeOrder;
import java.util.Map;
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

    Orders<Map<Asset, Multimap<Integer, TradeOrder>>> orders =
        converter.apply(asList(line1, line2, line3));

    assertThat(orders.getOrderTypes()).containsExactlyInAnyOrder(BUY, SELL);

    Map<Asset, Multimap<Integer, TradeOrder>> buyOrders = orders.getOrders(BUY);
    assertThat(buyOrders).containsOnlyKeys(of("A"), of("B"));

    Map<Asset, Multimap<Integer, TradeOrder>> sellOrders = orders.getOrders(SELL);
    assertThat(sellOrders).containsOnlyKeys(of("C"));

    Multimap<Integer, TradeOrder> a = buyOrders.get(of("A"));
    assertThat(a.asMap()).containsOnlyKeys(7 * 12);
    assertThat(a.get(7 * 12)).hasSize(1);

    TradeOrder order_7_12 = get(a.get(7 * 12), 0);
    assertThat(order_7_12.getClient()).isEqualTo("C1");
    assertThat(order_7_12.getType()).isSameAs(BUY);
    assertThat(order_7_12.getAsset()).isEqualTo(of("A"));
    assertThat(order_7_12.getPrice()).isEqualTo(7);
    assertThat(order_7_12.getQuantity()).isEqualTo(12);
    assertThat(order_7_12.getSum()).isEqualTo(7 * 12);

    Multimap<Integer, TradeOrder> b = buyOrders.get(of("B"));
    assertThat(b.asMap()).containsOnlyKeys(8 * 2);
    assertThat(b.get(8 * 2)).hasSize(1);

    TradeOrder order_8_2 = get(b.get(8 * 2), 0);
    assertThat(order_8_2.getClient()).isEqualTo("C2");
    assertThat(order_8_2.getType()).isSameAs(BUY);
    assertThat(order_8_2.getAsset()).isEqualTo(of("B"));
    assertThat(order_8_2.getPrice()).isEqualTo(8);
    assertThat(order_8_2.getQuantity()).isEqualTo(2);
    assertThat(order_8_2.getSum()).isEqualTo(8 * 2);

    Multimap<Integer, TradeOrder> c = sellOrders.get(of("C"));
    assertThat(c.asMap()).containsOnlyKeys(1);
    assertThat(c.get(1)).hasSize(1);

    TradeOrder order_1_1 = get(c.get(1), 0);
    assertThat(order_1_1.getClient()).isEqualTo("C3");
    assertThat(order_1_1.getType()).isSameAs(SELL);
    assertThat(order_1_1.getAsset()).isEqualTo(of("C"));
    assertThat(order_1_1.getPrice()).isEqualTo(1);
    assertThat(order_1_1.getQuantity()).isEqualTo(1);
    assertThat(order_1_1.getSum()).isEqualTo(1);
  }
}
