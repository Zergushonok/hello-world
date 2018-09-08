package hellosbt.data.clients;

import hellosbt.data.assets.Asset;
import java.util.Map;

/**
 * Represents a client of the exchange. *
 * A client is required to have a name, balance, and a number of assets.
 *
 * Clients financial data is mutable: the implementations are expected
 * to provide ways for updating their balance and quantities of possessed assets.
 * Their names, however, are set once upon creation and cannot be updated.
 *
 * It is also encouraged (though not enforced)
 * to make the Map returned by getAssets() to be a read-only view.
 */

public interface Client {

  String getName();
  int getBalance(); //todo: can probably be Number to support impls with float balances
  void modifyBalance(int delta);
  Map<Asset, Integer> getAssets(); //todo: same for assets quantities
  void modifyAssetQuantity(Asset asset, int delta);
}
