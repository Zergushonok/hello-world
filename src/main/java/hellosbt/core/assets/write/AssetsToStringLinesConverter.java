package hellosbt.core.assets.write;

import hellosbt.data.assets.Assets;
import java.util.Collection;
import java.util.function.Function;

/**
 * Functional interface that transforms the input Assets into a Collection of String lines.
 * Follows the Java's Function semantics, delegates all implementation details to sub-classes.
 */

@FunctionalInterface
public interface AssetsToStringLinesConverter extends Function<Assets, Collection<String>> {

}
