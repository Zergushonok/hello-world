package hellosbt.data.clients;

/**
 * Represents a number of clients of the exchange.
 * The implementations should specify exactly how the clients are stored inside this structure,
 * as well as provide a way to extract them.
 *
 * The underlying data structure is NOT required to be immutable, but this contract
 * intentionally does not provide any info on how the clients inside can be updated.
 */

public interface Clients<T> {

  T getClients();
}
