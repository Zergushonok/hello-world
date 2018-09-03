package hellosbt.data.orders;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import hellosbt.data.clients.AssetsHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A default implementation of Order.
 * This implementation intentionally does not override equals or hashcode so that no two orders
 * could be considered equal (aside when they are the same object). Hence it would be a questionable
 * idea to use this object as a map key.
 *
 * The logic behind this idea is that two orders placed by the same person,
 * with the exact same parameters, are still different orders.
 */

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@ToString
public class ClientOrder implements Order {

  AssetsHolder holder;
  Type type;
  Asset asset;
  Integer price;
  Integer quantity;
}
