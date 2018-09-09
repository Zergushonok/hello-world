package hellosbt.readers;

import static hellosbt.data.assets.TradeableGood.of;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import hellosbt.BaseTest;
import hellosbt.core.clients.read.ClientsFromTabSeparatedLinesConverter;
import hellosbt.data.assets.Asset;
import hellosbt.data.clients.Client;
import hellosbt.data.clients.Clients;
import java.util.Map;
import lombok.experimental.FieldDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
public class ClientsFromTabSeparatedLinesConverterTest extends BaseTest {

  @Autowired ClientsFromTabSeparatedLinesConverter converter;

  //todo: negative cases
  @Test
  public void correctClientsLinesAreConvertedToClients() {

    String line1 = "C1\t1000\t10\t5\t15\t0";
    String line2 = "C2\t2000\t3\t35\t40\t10";
    String line3 = "C3\t3000\t6\t0\t0\t1";

    Clients<Map<String, Client>> clients =
        converter.apply(asList(line1, line2, line3));

    assertThat(clients).isNotNull();

    Map<String, Client> clientsMap = clients.getClients();
    assertThat(clientsMap).isNotEmpty().hasSize(3)
        .containsOnlyKeys("C1", "C2", "C3");

    Client c1 = clientsMap.get("C1");
    Client c2 = clientsMap.get("C2");
    Client c3 = clientsMap.get("C3");

    assertThat(c1).isNotNull();
    assertThat(c2).isNotNull();
    assertThat(c3).isNotNull();

    assertThat(c1.getName()).isEqualTo("C1");
    assertThat(c2.getName()).isEqualTo("C2");
    assertThat(c3.getName()).isEqualTo("C3");

    assertThat(c1.getBalance()).isEqualTo(1000);
    assertThat(c2.getBalance()).isEqualTo(2000);
    assertThat(c3.getBalance()).isEqualTo(3000);

    Map<Asset, Integer> c1Assets = c1.getAssets();
    Map<Asset, Integer> c2Assets = c2.getAssets();
    Map<Asset, Integer> c3Assets = c3.getAssets();

    testEntries(c1Assets, 10, 5, 15, 0);
    testEntries(c2Assets, 3, 35, 40, 10);
    testEntries(c3Assets, 6, 0, 0, 1);
  }

  private void testEntries(Map<Asset, Integer> assets,
                           int A, int B, int C, int D) {

    assertThat(assets).isNotNull().isNotEmpty().hasSize(expectedAssets().length)
        .containsOnlyKeys(expectedAssets())
        .containsEntry(of("A"), A)
        .containsEntry(of("B"), B)
        .containsEntry(of("C"), C)
        .containsEntry(of("D"), D);
  }

  private Asset[] expectedAssets() {
    return new Asset[]{of("A"), of("B"), of("C"), of("D")};
  }
}
