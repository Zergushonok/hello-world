package hellosbt.core.orders.read;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.Striped.lazyWeakLock;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Striped;
import hellosbt.core.OrdersSupplier;
import hellosbt.data.Asset;
import hellosbt.data.Orders;
import hellosbt.data.TradeOrder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.locks.Lock;
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

//todo: clientsFromFileReader, ordersFromFileReader, and clientsToFileWriter all use a similar
//  structure; this can be refactored to reduce duplicated boilerplate

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@Slf4j
public class OrdersFromFileReader
    implements OrdersSupplier<Map<Asset, Multimap<Integer, TradeOrder>>> {

  private static final Striped<Lock> fileLocks = lazyWeakLock(getRuntime().availableProcessors());

  Path filepath;
  OrdersFromStringLinesConverter<Map<Asset, Multimap<Integer, TradeOrder>>> toOrdersConverter;

  public OrdersFromFileReader(
      @Value("#{ '${service.input.orders.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.input.orders.file.name}' ?: 'orders.txt' }")
          Path filepath,
      @Autowired OrdersFromStringLinesConverter<Map<Asset, Multimap<Integer, TradeOrder>>>
          toOrdersConverter) {

    this.filepath = filepath;
    this.toOrdersConverter = toOrdersConverter;
  }

  @Override
  public Orders<Map<Asset, Multimap<Integer, TradeOrder>>> get() {
    log.info("Orders will be read from the file {}", filepath);
    return lockAndRead(fileLocks.get(filepath));
  }

  private Orders<Map<Asset, Multimap<Integer, TradeOrder>>> lockAndRead(Lock fileLock) {
    try {
      lockOrFail(fileLock);

      //todo: this will exhaust RAM if the file is large enough, need to stream the file
      //  and construct the orders entity incrementally via a builder
      return toOrdersConverter.apply(readAllLines(filepath));

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(format("Failed to read the clients orders from the file %s",
          filepath), e);

    } finally {
      fileLock.unlock();
    }
  }

  private void lockOrFail(Lock fileLock) throws InterruptedException {

    checkState(fileLock.tryLock(10, SECONDS),
        "Could not obtain a file lock (timeout): %s", filepath);
  }
}
