package hellosbt;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;
import hellosbt.core.orders.read.OrdersFromFileReader;
import hellosbt.core.orders.read.OrdersFromTabSeparatedLinesConverter;
import hellosbt.data.Asset;
import hellosbt.data.Orders;
import hellosbt.data.TradeOrder;
import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrdersFromFileReaderTest extends BaseTest {

  @Autowired OrdersFromFileReader fileReader;
  @Autowired OrdersFromTabSeparatedLinesConverter converter;

  @Test
  public void linesOfOrdersAreConvertedIntoAnOrdersStructure() {

    Orders<Map<Asset, Multimap<Integer, TradeOrder>>> orders = fileReader.get();
    assertThat(orders).isNotNull();
    assertThat(orders.getOrderTypes()).isNotEmpty();

    orders.getOrderTypes().forEach(type -> {
      assertThat(orders.getOrders(type)).isNotEmpty();
    });
  }}
