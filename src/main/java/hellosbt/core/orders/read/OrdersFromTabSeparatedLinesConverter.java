package hellosbt.core.orders.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.orders.OrdersBySignatureByType.of;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.Multimap;
import hellosbt.data.assets.Asset;
import hellosbt.data.assets.TradeableGood;
import hellosbt.data.orders.Orders;
import hellosbt.data.orders.TradeOrder;
import hellosbt.data.orders.TradeOrder.Type;
import hellosbt.data.orders.TradeOrderSignature;
import java.util.Collection;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Orders form String lines converter implementation
 * that expects each input line to be an array
 * of an orders's parameters separated by \t.
 *
 * This implementation expects that the 1st element of the array
 * will be the client's name, 2nd -- the order type, 3rd -- the traded asset,
 * and the 4th and 5th -- the price and quantity of the traded asset.
 *
 * The transformation of the converted array of orders into the final orders-holding structure
 * is performed by the TradeOrder's constructor.
 */

//todo: extract the schema of an order into a separate component
//  to support other types of orders easier

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
@Slf4j
public class OrdersFromTabSeparatedLinesConverter
    implements OrdersFromStringLinesConverter<Multimap<TradeOrderSignature, TradeOrder>> {

  @Override
  public Orders<Multimap<TradeOrderSignature, TradeOrder>> apply(Collection<String> ordersLines) {
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
