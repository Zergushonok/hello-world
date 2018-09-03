package hellosbt;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.orders.read.OrdersFromFileReader;
import hellosbt.core.orders.read.OrdersFromTabSeparatedLinesConverter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
@Slf4j
public class OrdersFromFileReaderTest extends BaseTest {

  @Autowired OrdersFromFileReader ordersFromFileReader;
  @Autowired OrdersFromTabSeparatedLinesConverter ordersFromTabSeparatedLinesConverter;

  @Test
  public void testTest() {
    log.info(ordersFromFileReader.get().toString());
  }
}
