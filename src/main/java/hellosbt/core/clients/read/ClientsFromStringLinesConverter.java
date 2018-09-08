package hellosbt.core.clients.read;

import hellosbt.data.Clients;
import java.util.Collection;
import java.util.function.Function;

/**
 * Functional interface that transforms the input Collection of String lines into Clients.
 * Follows the Java's Function semantics, delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface ClientsFromStringLinesConverter<O>
    extends Function<Collection<String>, Clients<O>> {

}
