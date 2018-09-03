package hellosbt.data.clients;

import java.util.Map;

/**
 * An abstraction that represents a map of asset holders (i.e. clients)
 */

public interface Assets {

  Map<String, AssetsHolder> getHoldersByNames();
}
