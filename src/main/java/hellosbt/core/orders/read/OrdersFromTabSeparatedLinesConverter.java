package hellosbt.core.orders.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.OrdersByAssetsByType.of;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.Multimap;
import hellosbt.data.Asset;
import hellosbt.data.Orders;
import hellosbt.data.TradeOrder;
import hellosbt.data.TradeOrder.Type;
import hellosbt.data.TradeableGood;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
public class OrdersFromTabSeparatedLinesConverter
    implements OrdersFromStringLinesConverter<Map<Asset, Multimap<Integer, TradeOrder>>> {

  @Override
  public Orders<Map<Asset, Multimap<Integer, TradeOrder>>> apply(Collection<String> ordersLines) {
    log.debug("Processing {} lines of orders", ordersLines.size());

    return of(ordersLines.stream().map(this::toOrder).collect(toList()));
  }

  private TradeOrder toOrder(String rawOrderData) {
    log.trace("Parsing the line {} into an Order", rawOrderData);

    List<String> orderParts = asList(rawOrderData.split("\t"));
    checkArgument(orderParts.size() == 5,
        "The line %s does not contain a valid order data", orderParts);

    String client = orderParts.get(0);
    Type type = Type.of(orderParts.get(1));
    Asset asset = TradeableGood.of(orderParts.get(2));

    int price = parseInt(orderParts.get(3));
    int quantity = parseInt(orderParts.get(4));

    TradeOrder order = TradeOrder.of(client, type, asset, price, quantity);
    log.trace("The order {} has been parsed from the line {}", order, rawOrderData);
    return order;
  }
}
