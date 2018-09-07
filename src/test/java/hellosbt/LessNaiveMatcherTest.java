package hellosbt;

import static hellosbt.data.TradeOrder.Type.BUY;
import static hellosbt.data.TradeOrder.Type.SELL;
import static hellosbt.data.TradeableGood.of;

import com.google.common.collect.ImmutableMap;
import hellosbt.data.ClientsMap;
import hellosbt.data.OrdersByAssetsByType;
import hellosbt.data.TradeOrder;
import hellosbt.data.Trader;
import org.junit.Before;
import org.junit.Test;

public class LessNaiveMatcherTest {

  Trader c1, c2, c3;
  ClientsMap clients;

  @Test
  public void identicalOrdersAreMatched() {

    TradeOrder orderC1BA_2_5 = TradeOrder.of(c1, BUY, of("A"), 2, 5);
    TradeOrder orderC2SA_2_5 = TradeOrder.of(c2, SELL, of("A"), 2, 5);
    ImmutableMap.of(BUY, orderC1BA_2_5);
    ImmutableMap.of(SELL, orderC1BA_2_5);

  }

  @Before
  public void prep() {

    c1 = Trader.of("C1", 100, ImmutableMap.of(
        of("A"), 10,
        of("B"), 20,
        of("C"), 30));
    c2 = Trader.of("C2", 200, ImmutableMap.of(
        of("A"), 11,
        of("B"), 22,
        of("C"), 32));
    c3 = Trader.of("C3", 300, ImmutableMap.of(
        of("A"), 13,
        of("B"), 23,
        of("C"), 33));
    clients = ClientsMap.of(ImmutableMap.of("C1", c1, "C2", c2, "C3", c3));
  }

}
