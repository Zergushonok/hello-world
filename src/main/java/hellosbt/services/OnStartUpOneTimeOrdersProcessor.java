package hellosbt.services;

import static hellosbt.config.Spring.Profiles.ONETIME_ON_STARTUP;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.assets.AssetsConsumer;
import hellosbt.core.assets.AssetsSupplier;
import hellosbt.core.orders.OrdersProcessor;
import hellosbt.core.orders.OrdersSupplier;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service @Profile(ONETIME_ON_STARTUP)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class OnStartUpOneTimeOrdersProcessor {

  AssetsSupplier assetsSupplier;
  OrdersSupplier ordersSupplier;
  OrdersProcessor ordersProcessor;
  AssetsConsumer assetsConsumer;

  @PostConstruct
  public void doWork() {
    assetsConsumer.accept(ordersProcessor.apply(assetsSupplier.get(), ordersSupplier.get()));
  }
}
