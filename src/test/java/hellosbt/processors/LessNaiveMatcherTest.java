package hellosbt.processors;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static hellosbt.data.orders.TradeOrder.Type.BUY;
import static hellosbt.data.orders.TradeOrder.Type.SELL;
import static hellosbt.data.assets.TradeableGood.of;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import hellosbt.BaseTest;
import hellosbt.core.orders.process.LessNaiveClientsOrdersMatcher;
import hellosbt.data.clients.Client;
import hellosbt.data.clients.Clients;
import hellosbt.data.clients.ClientsMap;
import hellosbt.data.orders.OrdersByAssetsByType;
import hellosbt.data.orders.TradeOrder;
import hellosbt.data.clients.Trader;
import java.util.Map;
import lombok.experimental.FieldDefaults;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
public class LessNaiveMatcherTest extends BaseTest {

  @Autowired LessNaiveClientsOrdersMatcher matcher;

  private static Trader c1, c2, c3;
  private static ClientsMap clients;

  //todo: more test-cases including negative ones

  @Test
  public void identicalOrdersAreMatched() {

    TradeOrder orderC1BA_2_5 = TradeOrder.of(c1.getName(), BUY, of("A"), 2, 5);
    TradeOrder orderC2SA_2_5 = TradeOrder.of(c2.getName(), SELL, of("A"), 2, 5);

    OrdersByAssetsByType orders = OrdersByAssetsByType.of(asList(orderC1BA_2_5, orderC2SA_2_5));

    Clients<Map<String, Client>> updatedClientsMap = matcher.apply(clients, orders);
    assertThat(updatedClientsMap).isNotNull();

    Map<String, Client> updatedClients = updatedClientsMap.getClients();
    assertThat(updatedClients).isNotEmpty().hasSameSizeAs(clients.getClients());

    Client uc1 = updatedClients.get("C1");
    Client uc2 = updatedClients.get("C2");
    Client uc3 = updatedClients.get("C3");

    assertThat(uc1).isNotNull();
    assertThat(uc2).isNotNull();
    assertThat(uc3).isNotNull();

    assertThat(uc1.getBalance()).isEqualTo(100 - orderC1BA_2_5.getSum());
    assertThat(uc2.getBalance()).isEqualTo(200 + orderC2SA_2_5.getSum());

    assertThat(uc1.getAssets().get(of("A")))
        .isEqualTo(10 + orderC1BA_2_5.getQuantity());

    assertThat(uc2.getAssets().get(of("A")))
        .isEqualTo(11 - orderC2SA_2_5.getQuantity());

    assertThat(uc3.getBalance()).isEqualTo(300);
    assertThat(uc3.getAssets().get(of("A"))).isEqualTo(13);
  }

  @BeforeClass
  public static void prep() {

    c1 = Trader.of("C1", 100, newLinkedHashMap(ImmutableMap.of(
        of("A"), 10,
        of("B"), 20,
        of("C"), 30)));
    c2 = Trader.of("C2", 200, newLinkedHashMap(ImmutableMap.of(
        of("A"), 11,
        of("B"), 22,
        of("C"), 32)));
    c3 = Trader.of("C3", 300, newLinkedHashMap(ImmutableMap.of(
        of("A"), 13,
        of("B"), 23,
        of("C"), 33)));
    clients = ClientsMap.of(ImmutableMap.of("C1", c1, "C2", c2, "C3", c3));
  }
}
