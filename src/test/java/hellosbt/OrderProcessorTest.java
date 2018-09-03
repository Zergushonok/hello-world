package hellosbt;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import hellosbt.core.orders.process.NaiveClientsOrdersMatcher;
import hellosbt.core.orders.process.NaiveClientsOrdersMatcher.TransactionProcessor;
import hellosbt.data.assets.Asset;
import hellosbt.data.assets.NamedAsset;
import hellosbt.data.clients.Assets;
import hellosbt.data.clients.AssetsByHolders;
import hellosbt.data.clients.AssetsHolder;
import hellosbt.data.clients.Client;
import hellosbt.data.orders.ClientOrder;
import hellosbt.data.orders.Order;
import hellosbt.data.orders.Order.Type;
import hellosbt.data.orders.OrdersList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
@Slf4j
public class OrderProcessorTest extends BaseTest {

  @Autowired NaiveClientsOrdersMatcher naiveClientsOrdersMatcher;
  @Autowired TransactionProcessor transactionProcessor;

  //todo: tests for negative cases
  //todo: tests for telling combinations of orders (around 10)

  @Test
  public void testTest() {
    Asset a = NamedAsset.of("A");
    Asset b = NamedAsset.of("B");

    LinkedHashMap<Asset, Integer> c1Assets = new LinkedHashMap<>();
    c1Assets.put(a, 10);
    c1Assets.put(b, 20);
    LinkedHashMap<Asset, Integer> c2Assets = new LinkedHashMap<>();
    c2Assets.put(a, 32);
    c2Assets.put(b, 71);
    Client c1 = Client.of("C1", 100, c1Assets);
    Client c2 = Client.of("C2", 200, c2Assets);

    LinkedHashMap<String, AssetsHolder> assetsMap = new LinkedHashMap<>();
    assetsMap.put(c1.getName(), c1);
    assetsMap.put(c2.getName(), c2);
    AssetsByHolders assets = AssetsByHolders.of(assetsMap);

    Order o1 = ClientOrder.of(Client.of("C1"), Type.BUY, NamedAsset.of("A"), 10, 2);
    Order o2 = ClientOrder.of(Client.of("C2"), Type.SELL, NamedAsset.of("A"), 10, 2);
    OrdersList orders = OrdersList.of(Arrays.asList(o1, o2));

    Assets result = naiveClientsOrdersMatcher.apply(assets, orders);

    assertThat(result.getHoldersByNames().get("C1").getWealth()).isEqualTo(80);
    assertThat(result.getHoldersByNames().get("C2").getWealth()).isEqualTo(220);

    assertThat(result.getHoldersByNames().get("C1").getAssets().get(NamedAsset.of("A")))
        .isEqualTo(12);
    assertThat(result.getHoldersByNames().get("C2").getAssets().get(NamedAsset.of("A")))
        .isEqualTo(30);

    assertThat(result.getHoldersByNames().get("C1").getAssets().get(NamedAsset.of("B")))
        .isEqualTo(c1.getAssets().get(NamedAsset.of("B")));
    assertThat(result.getHoldersByNames().get("C2").getAssets().get(NamedAsset.of("B")))
        .isEqualTo(c2.getAssets().get(NamedAsset.of("B")));
  }
}
