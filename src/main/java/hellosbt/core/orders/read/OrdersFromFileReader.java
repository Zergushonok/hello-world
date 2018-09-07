package hellosbt.core.orders.read;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static lombok.AccessLevel.PRIVATE;

import com.google.common.collect.Multimap;
import hellosbt.core.OrdersSupplier;
import hellosbt.data.Asset;
import hellosbt.data.Order;
import hellosbt.data.Orders;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * OrdersSupplier implementation that supplies Orders from the specified file by converting them
 * from the List of String lines read from this file using the provided converter.
 */

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@Slf4j
public class OrdersFromFileReader implements OrdersSupplier<Multimap<Asset, Order>> {

  Path filepath;
  OrdersFromStringLinesConverter toOrdersConverter;

  public OrdersFromFileReader(
      @Value("#{ '${service.input.orders.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.input.orders.file.name}' ?: 'orders.txt' }")
          Path filepath,
      @Autowired OrdersFromStringLinesConverter toOrdersConverter) {

    this.filepath = filepath;
    this.toOrdersConverter = toOrdersConverter;
  }

  @Override
  public Orders get() {
    log.info("Orders will be read from file {}", filepath);

    try {
      List<String> ordersAsLines;
      synchronized (this) { //todo: ugly, use nio filechannel and its lock
        ordersAsLines = readAllLines(filepath);
      }
      return toOrdersConverter.apply(ordersAsLines);

    } catch (IOException e) {
      throw new RuntimeException(format("Failed to read the clients orders from the file %s",
          filepath), e);
    }
  }
}
