package hellosbt.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) @Getter
@ToString
public class TradeOrder implements Order {

  Client client;
  TradeOrder.Type type;
  Asset asset;
  int price;
  int quantity;

  int sum;

  private TradeOrder(Client client, TradeOrder.Type type, Asset asset, int price, int quantity) {
    this.client = client;
    this.type = type;
    this.asset = asset;
    this.price = price;
    this.quantity = quantity;
    this.sum = price * quantity;
  }

  public static TradeOrder of(Client client, TradeOrder.Type type, Asset asset,
                              int price, int quantity) {

    return new TradeOrder(client, type, asset, price, quantity);
  }

  @RequiredArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) @Getter
  public enum Type implements Order.Type {

    BUY("s"), SELL("b");

    String type;
  }
}
