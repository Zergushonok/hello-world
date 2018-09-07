package hellosbt;

import static hellosbt.data.TradeOrder.Type.BUY;
import static hellosbt.data.TradeOrder.Type.SELL;
import static hellosbt.data.TradeOrder.of;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import hellosbt.data.Asset;
import hellosbt.data.Client;
import hellosbt.data.TradeOrder;
import hellosbt.data.TradeableGood;
import hellosbt.data.Trader;
import org.junit.Test;

public class TradeOrderTest extends BaseTest {

  @Test
  public void orderSumIsCalculatedCorrectlyOnOrderConstruction() {

    TradeOrder order10 = of(mock(Client.class), BUY, mock(Asset.class), 2, 5);
    assertThat(order10.getSum()).isEqualTo(2 * 5);
  }

  @Test
  public void orderTypeEnumIsSetCorrectlyOnOrderConstruction() {

    TradeOrder buyOrder1 = of(mock(Client.class), BUY, mock(Asset.class), 2, 5);
    assertThat(buyOrder1.getType()).isSameAs(BUY);

    TradeOrder buyOrder2 = of(mock(Client.class), BUY, mock(Asset.class), 2, 5);
    TradeOrder sellOrder = of(mock(Client.class), SELL, mock(Asset.class), 2, 5);
    assertThat(buyOrder2.getType()).isSameAs(buyOrder1.getType())
        .isNotSameAs(sellOrder.getType());
  }

  @Test
  public void tradeOrdersAreNeverEqual() {
    Client client = Trader.of("C1", 0, emptyMap());
    Asset asset = TradeableGood.of("A");

    TradeOrder order = of(client, BUY, asset, 2, 5);
    TradeOrder identicalOrder = of(client, BUY, asset, 2, 5);
    assertThat(order).isNotEqualTo(identicalOrder);
  }
}
