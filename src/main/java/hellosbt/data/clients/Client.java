package hellosbt.data.clients;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A default implementation of AssetsHolder.
 * A Client's uniqueness is defined by name only, so its wealth and assets play no role
 * in its equality to other clients or hash function.
 *
 * This implementation places no restrictions onto the underlying assets map implementation
 */

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@EqualsAndHashCode(of = "name") @ToString
public class Client implements AssetsHolder {

  String name;
  Integer wealth;
  Map<Asset, Integer> assets;

  //todo: ugly, we can probably get rid of it altogether and use only string names in orders...
  /**
   * An additional constructor for the cases when we do not care about the client's parameters,
   * for example, when we are constructing an instance to be held as a part of a purchase order.
   * @param name client's name
   * @return an instance with the name specified and other parameters set to null
   */
  public static Client of(String name) {
    return of(name, null, null);
  }
}