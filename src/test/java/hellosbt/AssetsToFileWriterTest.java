package hellosbt;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.assets.write.AssetsToFileWriter;
import hellosbt.core.assets.write.AssetsToTabSeparatedLinesConverter;
import hellosbt.data.assets.Asset;
import hellosbt.data.clients.AssetsByHolders;
import hellosbt.data.clients.AssetsHolder;
import hellosbt.data.clients.Client;
import hellosbt.data.assets.NamedAsset;
import java.util.LinkedHashMap;
import lombok.experimental.FieldDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
public class AssetsToFileWriterTest extends BaseTest {

  @Autowired AssetsToFileWriter assetsToFileWriter;
  @Autowired AssetsToTabSeparatedLinesConverter assetsToTabSeparatedLinesConverter;

  @Test
  public void testTest() {
    Asset a = NamedAsset.of("A");
    Asset b = NamedAsset.of("B");

    LinkedHashMap<Asset, Integer> c1Assets = new LinkedHashMap<>();
    c1Assets.put(a, 10);
    LinkedHashMap<Asset, Integer> c2Assets = new LinkedHashMap<>();
    c2Assets.put(b, 71);
    Client c1 = Client.of("C1", 100, c1Assets);
    Client c2 = Client.of("C2", 200, c2Assets);

    LinkedHashMap<String, AssetsHolder> assetsMap = new LinkedHashMap<>();
    assetsMap.put(c1.getName(), c1);
    assetsMap.put(c2.getName(), c2);
    AssetsByHolders assets = AssetsByHolders.of(assetsMap);

    assetsToFileWriter.accept(assets);
  }
}