package hellosbt.writers;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import hellosbt.BaseTest;
import hellosbt.core.clients.write.ClientsToTabSeparatedLinesConverter;
import hellosbt.data.Client;
import hellosbt.data.ClientsMap;
import hellosbt.data.TradeableGood;
import hellosbt.data.Trader;
import java.util.List;
import lombok.experimental.FieldDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Repeat;

@FieldDefaults(level = PRIVATE)
public class ClientsToTabSeparatedLinesConverterTest extends BaseTest {

  @Autowired ClientsToTabSeparatedLinesConverter converter;

  //todo: negative cases

  @Test
  @Repeat(10)
  /* to make sure that the order of assets for a client
  and the order of clients in a clients map is preserved */
  public void validClientsDataIsCorrectlyConvertedIntoLines() {
    String etalonline1 = "C1\t1000\t10\t5\t15\t0";
    String etalonline2 = "C2\t2000\t3\t35\t40\t10";
    String etalonline3 = "C3\t3000\t6\t0\t0\t1";
    List<String> etalonClientsLines = asList(etalonline1, etalonline2, etalonline3);

    Client c1 = Trader.of("C1", 1000, of(
        TradeableGood.of("A"), 10,
        TradeableGood.of("B"), 5,
        TradeableGood.of("C"), 15,
        TradeableGood.of("D"), 0));
    Client c2 = Trader.of("C2", 2000, of(
        TradeableGood.of("A"), 3,
        TradeableGood.of("B"), 35,
        TradeableGood.of("C"), 40,
        TradeableGood.of("D"), 10));
    Client c3 = Trader.of("C3", 3000, of(
        TradeableGood.of("A"), 6,
        TradeableGood.of("B"), 0,
        TradeableGood.of("C"), 0,
        TradeableGood.of("D"), 1));

    ClientsMap clients = ClientsMap.of(of(
        c1.getName(), c1,
        c2.getName(), c2,
        c3.getName(), c3));

    List<String> clientsLines = converter.apply(clients);
    assertThat(clientsLines).isNotNull().isNotEmpty()
        .hasSameSizeAs(etalonClientsLines)
        .containsExactlyElementsOf(etalonClientsLines);
  }
}
