package hellosbt.core;

import hellosbt.data.clients.Clients;
import java.util.function.Consumer;

/**
 * Functional interface that consumes Clients from some source.
 * Follows the contract of the Java's Consumer,
 * delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface ClientsConsumer<C> extends Consumer<Clients<C>> {

}
