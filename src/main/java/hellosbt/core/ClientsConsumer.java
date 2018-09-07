package hellosbt.core;

import hellosbt.data.Clients;
import java.util.function.Consumer;

/**
 * Functional interface that consumes Clients from some source.
 * Follows semantics of the Java's Consumer interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface ClientsConsumer<C> extends Consumer<Clients<C>> {

}
