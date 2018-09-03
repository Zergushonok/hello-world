package hellosbt.core.orders.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import hellosbt.data.assets.Asset;
import hellosbt.data.clients.Client;
import hellosbt.data.orders.ClientOrder;
import hellosbt.data.assets.NamedAsset;
import hellosbt.data.orders.Order;
import hellosbt.data.orders.Orders;
import hellosbt.data.orders.OrdersList;
import java.util.Collection;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Orders form String lines converter implementation that expects each input line to be an array
 * of an orders's parameters separated by \t.
 *
 * It expects each line in the input collection to be valid, that is:
 * - It has exactly 5 values: name, type, asset, price, and quantity
 * - Price and quantity are valid positive Integers
 * - Asset name can be found in the supplied expected assets dictionary
 * - Order type is described in the Order.Type enum
 * If those conditions are not met, an exception is thrown.
 *
 * An Orders instance created by this converter is going to have all of the Orders provided to
 * this converter as String lines, in the same order they were provided.
 */

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
@Slf4j
public class OrdersFromTabSeparatedLinesConverter implements OrdersFromStringLinesConverter {

  //todo: de-hardcode
  private static final List<Asset> expectedAssetsDictionary =
      asList(NamedAsset.of("A"), NamedAsset.of("B"), NamedAsset.of("C"), NamedAsset.of("D"));

  @Override
  public Orders apply(Collection<String> ordersLines) {
    log.debug("Processing {} lines of orders. Expected assets dictionary is: {}",
        ordersLines.size(), expectedAssetsDictionary);

    return OrdersList.of(toOrders(ordersLines));
  }

  private List<Order> toOrders(Collection<String> ordersLines) {
    List<Order> orders = ordersLines.stream().map(this::toOrder).collect(toList());

    log.debug("Converted {} lines of orders into {} orders", ordersLines.size(), orders.size());
    return orders;
  }

  private Order toOrder(String orderLine) {
    log.trace("Parsing the order line {}", orderLine);

    List<String> orderData = asList(orderLine.split("\t"));
    validate(orderData);

    try {
      Order order = orderFromData(orderData);

      log.trace("Parsed the order line {} into the order {}", orderLine, order);
      return order;

    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          format("Please check that the asset price and quantity are valid integers "
              + "for the order represented by the line \"%s\"", orderLine), e);
    }
  }

  //todo: extract into a separate validator, use some kind of schema
  private void validate(List<String> orderData) {
    checkArgument(orderData.size() == 5,
        "The line \"%s\" does not look like a valid order. Expected format: "
            + "\"{client name}\t{operation type}\t{asset name}\t{item price}\t{items quantity}\"",
        orderData);
  }

  private Order orderFromData(List<String> orderData) {
    Integer price = Integer.valueOf(orderData.get(3));
    Integer quantity = Integer.valueOf(orderData.get(4));

    //todo: this too should be extracted from here into a separate validator
    checkArgument(price >= 0 && quantity >= 0,
        "This converter has no desire to handle negative quantities and prices "
            + "(order data: %s)", orderData);

    return ClientOrder.of(Client.of(orderData.get(0)),
        Order.Type.of(orderData.get(1)),
        failIfUnknown(NamedAsset.of(orderData.get(2))),
        price,
        quantity);
  }

  private Asset failIfUnknown(Asset asset) {
    checkArgument(expectedAssetsDictionary.contains(asset),
        "Asset \"%s\" is unknown to this converter. "
            + "Known assets: %s", asset.getIdentifier(),
        expectedAssetsDictionary.stream().map(Asset::getIdentifier).collect(toList()));
    return asset;
  }
}
