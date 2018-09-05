package hellosbt.core.clients.write;

import hellosbt.data.Clients;
import java.util.Collection;
import java.util.function.Function;

/**
 * Functional interface that transforms the input Clients into a Collection of String lines.
 * Follows the Java's Function semantics, delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface ClientsToStringLinesConverter extends Function<Clients, Collection<String>> {

}
