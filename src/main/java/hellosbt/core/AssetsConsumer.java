package hellosbt.core;

import hellosbt.data.assets.Assets;
import java.util.function.Consumer;

@FunctionalInterface
public interface AssetsConsumer extends Consumer<Assets> {

}
