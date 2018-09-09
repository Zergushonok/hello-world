package hellosbt.data;

import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static hellosbt.data.orders.TradeOrder.of;
import static hellosbt.data.orders.TradeOrderSignature.of;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import hellosbt.BaseTest;
import hellosbt.data.assets.TradeableGood;
import hellosbt.data.orders.OrdersBySignatureByType;
import hellosbt.data.orders.TradeOrder;
import hellosbt.data.orders.TradeOrderSignature;
import java.util.Collection;
import org.junit.Test;

public class OrdersBySignatureByTypeTest extends BaseTest {

  //todo: split into several easier tests, cover negatives
  @Test
  public void rawOrdersListIsCorrectlyConvertedIntoThisStructure() {

    TradeableGood a = TradeableGood.of("A");
    TradeableGood b = TradeableGood.of("B");
    TradeableGood c = TradeableGood.of("C");
    TradeableGood d = TradeableGood.of("D");

    TradeOrder bOrder1 = of("C0", BUY, a, 2, 5);
    TradeOrder bOrder2 = of("C0", BUY, a, 3, 6);
    TradeOrder bOrder3 = of("C0", BUY, b, 4, 7);
    TradeOrder bOrder4 = of("C0", BUY, b, 3, 6);

    TradeOrder sOrder1 = of("C0", SELL, b, 4, 7);
    TradeOrder sOrder2 = of("C0", SELL, b, 3, 6);
    TradeOrder sOrder3 = of("C0", SELL, c, 2, 5);
    TradeOrder sOrder4 = of("C0", SELL, d, 4, 7);
    TradeOrder sOrder5 = of("C0", SELL, d, 7, 4);
    TradeOrder sOrder6 = of("C0", SELL, d, 3, 5);

    OrdersBySignatureByType test = OrdersBySignatureByType
        .of(asList(bOrder1, sOrder1, sOrder2, bOrder2, sOrder3,
            bOrder3, sOrder4, sOrder5, bOrder4, sOrder6));

    Multimap<TradeOrderSignature, TradeOrder> buyOrders = test.getOrders(BUY);
    assertThat(buyOrders.asMap()).isNotEmpty();
    Multimap<TradeOrderSignature, TradeOrder> sellOrders = test.getOrders(SELL);
    assertThat(sellOrders.asMap()).isNotEmpty();

    assertThat(buyOrders.asMap())
        .hasSize(4)
        .containsOnlyKeys(
            of(a, 10),
            of(a, 18),
            of(b, 28),
            of(b, 18));

    assertThat(sellOrders.size()).isEqualTo(6);
    assertThat(sellOrders.asMap())
        .hasSize(5)
        .containsOnlyKeys(
            of(b, 28),
            of(b, 18),
            of(c, 10),
            of(d, 28),
            of(d, 15));

    Collection<TradeOrder> ordersForD28 =
        sellOrders.get(of(d, 28));

    assertThat(ordersForD28).hasSize(2);

    TradeOrder firstD28order = Iterables.get(ordersForD28, 0);
    TradeOrder secondD28order = Iterables.get(ordersForD28, 1);

    assertThat(firstD28order.getPrice()).isEqualTo(4);
    assertThat(firstD28order.getQuantity()).isEqualTo(7);
    assertThat(secondD28order.getPrice()).isEqualTo(7);
    assertThat(secondD28order.getQuantity()).isEqualTo(4);
  }

}
