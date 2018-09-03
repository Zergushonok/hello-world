package hellosbt.core.assets.read;

import hellosbt.data.clients.Assets;
import java.util.Collection;
import java.util.function.Function;

/**
 * Functional interface that transforms the input Collection of String lines into Assets.
 * Follows the Java's Function semantics, delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface AssetsFromStringLinesConverter extends Function<Collection<String>, Assets> {

}
