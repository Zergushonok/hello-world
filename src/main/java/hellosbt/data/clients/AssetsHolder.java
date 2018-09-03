package hellosbt.data.clients;

import hellosbt.data.assets.Asset;
import java.util.Map;

public interface AssetsHolder {

  String getName();
  Integer getWealth();
  Map<Asset, Integer> getAssets();
}
