package hellosbt;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;
import hellosbt.core.orders.read.OrdersFromFileReader;
import hellosbt.core.orders.read.OrdersFromTabSeparatedLinesConverter;
import hellosbt.data.Asset;
import hellosbt.data.Orders;
import hellosbt.data.TradeOrder;
import java.util.Map;
import lombok.experimental.FieldDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
public class OrdersFromFileReaderTest extends BaseTest {

  @Autowired OrdersFromFileReader fileReader;
  @Autowired OrdersFromTabSeparatedLinesConverter converter;

  //todo: more tests to validate the correctness of the structure
  //  use dedicated test files instead of the common one
  @Test
  public void linesOfOrdersAreConvertedIntoAnOrdersStructure() {

    Orders<Map<Asset, Multimap<Integer, TradeOrder>>> orders = fileReader.get();
    assertThat(orders).isNotNull();
    assertThat(orders.getOrderTypes()).isNotEmpty();

    orders.getOrderTypes().forEach(type -> {
      assertThat(orders.getOrders(type)).isNotEmpty();
    });
  }}
