package hellosbt.data;

import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static hellosbt.data.orders.TradeOrder.of;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import hellosbt.BaseTest;
import hellosbt.data.assets.Asset;
import hellosbt.data.clients.Client;
import hellosbt.data.assets.TradeableGood;
import hellosbt.data.clients.Trader;
import hellosbt.data.orders.TradeOrder;
import org.junit.Test;

public class TradeOrderTest extends BaseTest {

  @Test
  public void orderSumIsCalculatedCorrectlyOnOrderConstruction() {

    TradeOrder order10 = of("C0", BUY, mock(Asset.class), 2, 5);
    assertThat(order10.getSum()).isEqualTo(2 * 5);
  }

  @Test
  public void orderTypeEnumIsSetCorrectlyOnOrderConstruction() {

    TradeOrder buyOrder1 = of("C0", BUY, mock(Asset.class), 2, 5);
    assertThat(buyOrder1.getType()).isSameAs(BUY);

    TradeOrder buyOrder2 = of("C0", BUY, mock(Asset.class), 2, 5);
    TradeOrder sellOrder = of("C0", SELL, mock(Asset.class), 2, 5);
    assertThat(buyOrder2.getType()).isSameAs(buyOrder1.getType())
        .isNotSameAs(sellOrder.getType());
  }

  @Test
  public void tradeOrdersAreNeverEqual() {
    Client client = Trader.of("C1", 0, emptyMap());
    Asset asset = TradeableGood.of("A");

    TradeOrder order = of(client.getName(), BUY, asset, 2, 5);
    TradeOrder identicalOrder = of(client.getName(), BUY, asset, 2, 5);
    assertThat(order).isNotEqualTo(identicalOrder);
  }
}
