package hellosbt.core.orders.process;

import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.OrdersProcessor;
import hellosbt.data.Clients;
import hellosbt.data.Orders;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A less naive implementation of OrdersProcessor that looks for matching orders from different clients
 * in the input clients list, emulates a trade between these clients with matching orders,
 * reflecting the changes to clients' balances and asset quantities
 * in the resulting clients instance.
 */

@Service @Profile({DEFAULT, TEST})
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Slf4j
public class LessNaiveClientsOrdersMatcher implements OrdersProcessor {

  @Override
  public Clients apply(Clients clients, Orders orders) {

    return clients;
  }
}