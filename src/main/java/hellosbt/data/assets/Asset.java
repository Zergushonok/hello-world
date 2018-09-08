package hellosbt.data.assets;

/**
 * Represents an asset, i.e. some kind of good tradeable on the exchange between its clients.
 * An asset is required to have an identifier.
 */

//todo: can be generified to support other types of keys (e.g. UUID)
public interface Asset {

  String getId();
}
