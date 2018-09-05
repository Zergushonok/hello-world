package hellosbt.services;

import static hellosbt.config.Spring.Profiles.ONETIME_ON_STARTUP;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.ClientsConsumer;
import hellosbt.core.ClientsSupplier;
import hellosbt.core.OrdersProcessor;
import hellosbt.core.OrdersSupplier;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Executes once when the Spring application starts.
 * Reads clients and orders data from the respective suppliers, processes it via the processor,
 * and flushes the resulting clients data into the consumer.
 */

@Service @Profile(ONETIME_ON_STARTUP)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class OnStartUpOneTimeOrdersProcessor {

  ClientsSupplier clientsSupplier;
  OrdersSupplier ordersSupplier;
  OrdersProcessor ordersProcessor;
  ClientsConsumer clientsConsumer;

  @PostConstruct
  public void doWork() {
    log.info("One-time orders processor has started processing orders");
    clientsConsumer.accept(ordersProcessor.apply(clientsSupplier.get(), ordersSupplier.get()));
    log.info("One-time orders processor has finished his work");
  }
}
