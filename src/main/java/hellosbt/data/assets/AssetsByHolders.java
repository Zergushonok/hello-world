package hellosbt.data.assets;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.clients.AssetsHolder;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@ToString
public class AssetsByHolders implements Assets {

  Map<String, AssetsHolder> holdersByNames;
}
