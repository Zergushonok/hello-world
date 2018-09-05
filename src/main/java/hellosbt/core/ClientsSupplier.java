package hellosbt.core;

import hellosbt.data.Clients;
import org.apache.logging.log4j.util.Supplier;

/**
 * Functional interface that supplies Clients to some destination.
 * Follows semantics of the Java's Supplier interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface ClientsSupplier extends Supplier<Clients> {

}
