package hellosbt.core;

import hellosbt.data.clients.Clients;
import org.apache.logging.log4j.util.Supplier;

/**
 * Functional interface that supplies Clients to some destination.
 * Follows the contract of the Java's Supplier,
 * delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface ClientsSupplier<C> extends Supplier<Clients<C>> {

}
