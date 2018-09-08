package hellosbt.data.orders;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

import hellosbt.data.assets.Asset;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
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

  public static TradeOrder of(@NonNull String client,
                              @NonNull TradeOrder.Type type,
                              @NonNull Asset asset,
                              int price, int quantity) {

    return new TradeOrder(validate(client), type, asset, price, quantity);
  }

  private static String validate(String client) {
    return requireNonNull(emptyToNull(client),
        "A client's name cannot be empty");
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
