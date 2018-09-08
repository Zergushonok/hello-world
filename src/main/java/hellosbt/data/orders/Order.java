package hellosbt.data.orders;

import hellosbt.data.assets.Asset;

public interface Order {

  String getClient();
  Type getType();
  Asset getAsset();
  int getPrice();
  int getQuantity();
  int getSum();

  interface Type {

    String getType();
  }
}
