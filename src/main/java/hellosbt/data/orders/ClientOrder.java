package hellosbt.data.orders;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import hellosbt.data.clients.AssetsHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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
