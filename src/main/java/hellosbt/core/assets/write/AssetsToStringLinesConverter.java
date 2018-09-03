package hellosbt.core.assets.write;

import hellosbt.data.assets.Assets;
import java.util.Collection;
import java.util.function.Function;

@FunctionalInterface
public interface AssetsToStringLinesConverter extends Function<Assets, Collection<String>> {

}
