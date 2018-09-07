package hellosbt.data;

public interface Order {

  Client getClient();
  Type getType();
  Asset getAsset();
  int getPrice();
  int getQuantity();
  int getSum();

  interface Type {

    String getType();
  }
}
