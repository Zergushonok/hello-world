package hellosbt.data.orders;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import hellosbt.data.clients.AssetsHolder;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * An abstraction that represents a trade order
 */

public interface Order {

  AssetsHolder getHolder();

  Type getType();

  Asset getAsset();

  Integer getPrice();

  Integer getQuantity();

  //todo: should it really be here? probably not
  @RequiredArgsConstructor(access = PRIVATE)
  @FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
  enum Type {
    BUY("b"), SELL("s");

    String alias;

    public static Type of(String alias) {
      return stream(values())
          .filter(type -> Objects.equals(type.getAlias(), alias))
          .findAny().orElseThrow(() -> new IllegalArgumentException(
              format("The operation \"%s\" is not supported for this type of order", alias)));
    }
  }
}
