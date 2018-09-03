package hellosbt.core;

import hellosbt.data.assets.Assets;
import org.apache.logging.log4j.util.Supplier;

/**
 * Functional interface that supplies Assets to some destination.
 * Follows semantics of the Java's Supplier interface, delegates all implementation details to
 * sub-classes.
 */

@FunctionalInterface
public interface AssetsSupplier extends Supplier<Assets> {

}
