package hellosbt;

import static hellosbt.data.TradeOrder.Type.BUY;
import static hellosbt.data.TradeOrder.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import hellosbt.data.Asset;
import hellosbt.data.Client;
import hellosbt.data.TradeOrder;
import org.junit.Test;

public class TradeOrderTest extends BaseTest {

  @Test
  public void testTradeOrderConstruction() {
    TradeOrder order10 = of(mock(Client.class), BUY, mock(Asset.class), 2, 5);
    assertThat(order10.getSum()).isEqualTo(10);
    assertThat(order10.getType()).isSameAs(BUY);
  }
}
