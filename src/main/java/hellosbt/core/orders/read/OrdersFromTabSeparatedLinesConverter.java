package hellosbt.core.orders.read;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;

import hellosbt.data.Orders;
import java.util.Collection;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Orders form String lines converter implementation that expects each input line to be an array
 * of an orders's parameters separated by \t.
 */

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
@Slf4j
public class OrdersFromTabSeparatedLinesConverter implements OrdersFromStringLinesConverter {

  @Override
  public Orders apply(Collection<String> ordersLines) {
    log.debug("Processing {} lines of orders.");

    return null;
  }
}
