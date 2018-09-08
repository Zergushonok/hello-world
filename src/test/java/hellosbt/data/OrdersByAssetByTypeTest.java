package hellosbt.data;

import static com.google.common.collect.Iterables.get;
import static hellosbt.data.TradeOrder.Type.BUY;
import static hellosbt.data.TradeOrder.Type.SELL;
import static hellosbt.data.TradeOrder.of;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;
import hellosbt.BaseTest;
import hellosbt.data.Asset;
import hellosbt.data.OrdersByAssetsByType;
import hellosbt.data.TradeOrder;
import hellosbt.data.TradeableGood;
import java.util.Collection;
import java.util.Map;
import org.junit.Test;

public class OrdersByAssetByTypeTest extends BaseTest {

  //todo: split into several easier tests
  @Test
  public void rawOrdersListIsCorrectlyConvertedIntoThisStructure() {

    TradeOrder bOrder1 = of("C0", BUY, TradeableGood.of("A"), 2, 5);
    TradeOrder bOrder2 = of("C0", BUY, TradeableGood.of("A"), 3, 6);
    TradeOrder bOrder3 = of("C0", BUY, TradeableGood.of("B"), 4, 7);
    TradeOrder bOrder4 = of("C0", BUY, TradeableGood.of("B"), 3, 6);

    TradeOrder sOrder1 = of("C0", SELL, TradeableGood.of("B"), 4, 7);
    TradeOrder sOrder2 = of("C0", SELL, TradeableGood.of("B"), 3, 6);
    TradeOrder sOrder3 = of("C0", SELL, TradeableGood.of("C"), 2, 5);
    TradeOrder sOrder4 = of("C0", SELL, TradeableGood.of("D"), 4, 7);
    TradeOrder sOrder5 = of("C0", SELL, TradeableGood.of("D"), 7, 4);
    TradeOrder sOrder6 = of("C0", SELL, TradeableGood.of("D"), 3, 5);

    OrdersByAssetsByType test = OrdersByAssetsByType
        .of(asList(bOrder1, sOrder1, sOrder2, bOrder2, sOrder3,
            bOrder3, sOrder4, sOrder5, bOrder4, sOrder6));

    Map<Asset, Multimap<Integer, TradeOrder>> buyOrders = test.getOrders(BUY);
    assertThat(buyOrders).isNotEmpty();
    Map<Asset, Multimap<Integer, TradeOrder>> sellOrders = test.getOrders(SELL);
    assertThat(sellOrders).isNotEmpty();

    assertThat(buyOrders.get(TradeableGood.of("A")).asMap())
        .hasSize(2).containsOnlyKeys(2 * 5, 3 * 6);
    assertThat(buyOrders.get(TradeableGood.of("B")).asMap())
        .hasSize(2).containsOnlyKeys(4 * 7, 3 * 6);

    assertThat(sellOrders.get(TradeableGood.of("B")).asMap())
        .hasSize(2).containsOnlyKeys(4 * 7, 3 * 6);
    assertThat(sellOrders.get(TradeableGood.of("C")).asMap())
        .hasSize(1).containsOnlyKeys(2 * 5);

    Multimap<Integer, TradeOrder> ordersForD = sellOrders.get(TradeableGood.of("D"));
    assertThat(ordersForD.asMap()).hasSize(2).containsOnlyKeys(4 * 7, 3 * 5);

    Collection<TradeOrder> ordersForD_sum28 = ordersForD.get(7 * 4);
    assertThat(ordersForD_sum28).hasSize(2);

    TradeOrder firstD28order = get(ordersForD_sum28, 0);
    TradeOrder secondD28order = get(ordersForD_sum28, 1);
    assertThat(firstD28order.getPrice()).isEqualTo(4);
    assertThat(firstD28order.getQuantity()).isEqualTo(7);
    assertThat(secondD28order.getPrice()).isEqualTo(7);
    assertThat(secondD28order.getQuantity()).isEqualTo(4);
  }

}
