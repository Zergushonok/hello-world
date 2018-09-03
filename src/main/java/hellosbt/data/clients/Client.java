package hellosbt.data.clients;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@EqualsAndHashCode(of = "name") @ToString
public class Client implements AssetsHolder {

  String name;
  Integer wealth;
  Map<Asset, Integer> assets;

  public static Client of(String name) {
    return of(name, null, null);
  }
}