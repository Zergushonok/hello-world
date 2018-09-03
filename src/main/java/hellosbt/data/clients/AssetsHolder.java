package hellosbt.data.clients;

import hellosbt.data.assets.Asset;
import java.util.Map;

/**
 * An abstraction that represents a Client with a name, balance, and a map of assets.
 */

public interface AssetsHolder {

  String getName();
  Integer getWealth();
  Map<Asset, Integer> getAssets();
}
