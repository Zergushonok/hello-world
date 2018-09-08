package hellosbt.data.clients;

import hellosbt.data.assets.Asset;
import java.util.Map;

public interface Client {

  String getName();
  int getBalance();
  void modifyBalance(int delta);
  Map<Asset, Integer> getAssets();
  void modifyAssetQuantity(Asset asset, int delta);
}
