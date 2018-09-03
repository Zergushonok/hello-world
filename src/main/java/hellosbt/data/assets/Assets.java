package hellosbt.data.assets;

import hellosbt.data.clients.AssetsHolder;
import java.util.Map;

public interface Assets {

  Map<String, AssetsHolder> getHoldersByNames();
}
