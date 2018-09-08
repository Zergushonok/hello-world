package hellosbt.data.orders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * An implementation of Order designed to be immutable.
 *
 * It does not allow for the client's name, order type, or asset to be null upon construction.
 * The price and quantity are required to be positive (>0).
 * The sum is calculated as price * quantity upon construction.
 * No parameters can be updated once the order is constructed.
 *
 * Note that trade orders are NEVER equal, unless they references to the same object.
 * Even if two orders have identical parameters, they are still considered to be different.
 * Hash code of the Object is used for the order's hash code,
 * hence it will be unwise to use these objects as keys.
 */

@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@ToString
public class TradeOrder implements Order {

  String client;
  TradeOrder.Type type;
  Asset asset;

  int price;
  int quantity;
  int sum;

  private TradeOrder(String client, TradeOrder.Type type, Asset asset,
                     int price, int quantity) {

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

    return new TradeOrder(validate(client), type, asset, validate(price), validate(quantity));
  }

  private static String validate(String client) {
    return requireNonNull(emptyToNull(client),
        "A client's name cannot be empty");
  }

  private static int validate(int int_) {
    checkArgument(int_ > 0,
        "The ordered price and quantity should be greater than 0");
    return int_;
  }

  /**
   * TradeOrder supports two types of orders: to buy or to sell an asset.
   * An implementation is a enum; an instance of BUY can be retrieved by the alias "b",
   * while an instance of SELL - by "c". No other order types are supported.
   */

  @RequiredArgsConstructor
  @FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
  public enum Type implements Order.Type {

    BUY("b"), SELL("s");

    String type;

    public static Type of(String type) {
      switch (type) {
        case "b": return BUY;
        case "s": return SELL;
        default:
          throw new EnumConstantNotPresentException(Type.class, type);
      }
    }
  }
}
