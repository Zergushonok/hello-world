package hellosbt.data.orders;

import hellosbt.data.assets.Asset;

/**
 * Represents an order, i.e. a declared intent to buy or sell some good on the exchange.
 *
 * An order should belong to a client, has to have a type (e.g. a BUYING order),
 * and must specify the asset being traded.
 * An order should also have list a price for a single unit of the traded good,
 * and the traded quantity.
 * For convenience, this contract requires its implementations to provide the order sum,
 * It cannot be enforced on this level, but it is expected that the sum == price * quantity.
 *
 * New implementations have to provide their own implementations of supported order types.
 * An order type is required to have a String alias.
 */

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
