package hellosbt.core.orders.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.DEFAULT;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service @Profile({DEFAULT, TEST})
@NoArgsConstructor
public class OrdersFromTabSeparatedLinesConverter implements OrdersFromStringLinesConverter {

  private static final List<Asset> expectedAssetsDictionary =
      asList(NamedAsset.of("A"), NamedAsset.of("B"), NamedAsset.of("C"), NamedAsset.of("D"));

  @Override
  public Orders apply(Collection<String> ordersLines) {
    return OrdersList.of(toOrders(ordersLines));
  }

  private List<Order> toOrders(Collection<String> ordersLines) {
    return ordersLines.stream().map(this::toOrder).collect(toList());
  }

  private Order toOrder(String orderLine) {
    List<String> orderData = asList(orderLine.split("\t"));
    validate(orderData);

    try {
      return orderFromData(orderData);

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
