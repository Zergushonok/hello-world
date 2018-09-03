package hellosbt.core.assets;

import hellosbt.data.assets.Assets;
import org.apache.logging.log4j.util.Supplier;

@FunctionalInterface
public interface AssetsSupplier extends Supplier<Assets> {

}
