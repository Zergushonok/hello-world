package hellosbt.core.assets.read;

import hellosbt.data.assets.Assets;
import java.util.Collection;
import java.util.function.Function;

@FunctionalInterface
public interface AssetsFromStringLinesConverter extends Function<Collection<String>, Assets> {

}
