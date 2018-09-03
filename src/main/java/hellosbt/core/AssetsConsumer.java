package hellosbt.core;

import hellosbt.data.assets.Assets;
import java.util.function.Consumer;

/**
 * Functional interface that consumes Assets from some source.
 * Follows semantics of the Java's Consumer interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface AssetsConsumer extends Consumer<Assets> {

}
