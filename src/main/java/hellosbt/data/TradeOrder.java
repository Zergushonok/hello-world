package hellosbt.data;

import static java.lang.String.format;
import static java.util.Arrays.stream;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) @Getter
@ToString
public class TradeOrder implements Order {

  String client;
  TradeOrder.Type type;
  Asset asset;
  int price;
  int quantity;

  int sum;

  private TradeOrder(String client, TradeOrder.Type type, Asset asset, int price, int quantity) {
    this.client = client;
    this.type = type;
    this.asset = asset;
    this.price = price;
    this.quantity = quantity;
    this.sum = price * quantity;
  }

  public static TradeOrder of(String client, TradeOrder.Type type, Asset asset,
                              int price, int quantity) {

    return new TradeOrder(client, type, asset, price, quantity);
  }

  @RequiredArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) @Getter
  public enum Type implements Order.Type {

    BUY("b"), SELL("s");

    String type;

    public static Type of(String type) {
      return stream(values()).filter(it -> it.getType().equals(type)).findAny()
          .orElseThrow(() -> new EnumConstantNotPresentException(Type.class, type));
    }
  }
}
