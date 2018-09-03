package hellosbt.data.clients;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Default implementation of Assets that stores asset holders in a map with no restrictions to
 * the map's implementation.
 */

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@ToString
public class AssetsByHolders implements Assets {

  Map<String, AssetsHolder> holdersByNames;
}
