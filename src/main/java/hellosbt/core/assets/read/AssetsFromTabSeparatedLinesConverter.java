package hellosbt.core.assets.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import hellosbt.data.assets.Asset;
import hellosbt.data.clients.Assets;
import hellosbt.data.clients.AssetsByHolders;
import hellosbt.data.clients.AssetsHolder;
import hellosbt.data.clients.Client;
import hellosbt.data.assets.NamedAsset;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Assets form String lines converter implementation that expects each input line to be an array
 * of a client's info, his balance, and his assets quantities separated by \t.
 *
 * It expects each line in the input collection to be valid, that is:
 * - It has the same number of assets as listed in the supplied dictionary
 * - All asset quantities, as well as the client's balance, are valid Integers (can be negative)
 * If those conditions are not met, an exception is thrown.
 *
 * An Assets instance created by this converter is going to have all of the Assets listed in the
 * dictionary, in the same order they are listed. The quantities for each asset are set in the
 * same order they were encountered while parsing the line.
 */

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
public class AssetsFromTabSeparatedLinesConverter implements AssetsFromStringLinesConverter {

  //todo: de-hardcode
  private static final List<Asset> expectedAssetsOrderedDictionary = asList(
      NamedAsset.of("A"), NamedAsset.of("B"), NamedAsset.of("C"), NamedAsset.of("D"));

  @Override
  public Assets apply(Collection<String> assetsLines) {

    return AssetsByHolders.of(toClientsSet(assetsLines).stream()
        .collect(LinkedHashMap::new,
            (map, holder) -> map.put(holder.getName(), holder),
            Map::putAll));
  }

  private Set<AssetsHolder> toClientsSet(Collection<String> assetsLines) {
    return assetsLines.stream().map(this::toClient).collect(toCollection(LinkedHashSet::new));
  }

  private AssetsHolder toClient(String assetsLine) {
    List<String> nameAndAssetsData = asList(assetsLine.split("\t"));
    failIfAssetsNumberDiffersFromExpected(nameAndAssetsData, assetsLine);

    try {
      return Client.of(nameAndAssetsData.get(0), Integer.valueOf(nameAndAssetsData.get(1)),
          toAssetsQuantities(nameAndAssetsData));

    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          format("Please check that the quantities of money and assets are valid integers "
              + "for the client represented by the line \"%s\"", nameAndAssetsData), e);
    }
  }

  //todo: extract into a separate validator (along with the dictionary)
  private void failIfAssetsNumberDiffersFromExpected(List<String> nameAndAssetsData, String line) {

    checkArgument(isAssetsNumberAsExpected(nameAndAssetsData),
        "The line \"%s\" does not contain values for all expected assets: %s. "
            + "Expected %s values after the client's name \"%s\" but received %s.",
        line, expectedAssetsOrderedDictionary.stream().map(Asset::getIdentifier).collect(toList()),
        expectedAssetsOrderedDictionary.size(), nameAndAssetsData.get(0),
        nameAndAssetsData.size() - 2);
    // Correct nameAndAssetsData contains 6 items: name, wealth, and 4 assets
  }

  private boolean isAssetsNumberAsExpected(List<String> nameAndAssetsData) {
    return Objects.equals(nameAndAssetsData.size() - 2, expectedAssetsOrderedDictionary.size());
  }

  private LinkedHashMap<Asset, Integer> toAssetsQuantities(List<String> nameAndAssetsData) {

    LinkedHashMap<Asset, Integer> clientAssets = new LinkedHashMap<>();
    // Starting from 2, because the index 0 is occupied by the client's name and index 1 - by wealth
    // Therefore the 3rd value in the line corresponds to the 1st asset in the dictionary and so on
    for (int i = 2; i < nameAndAssetsData.size(); i++) {
      clientAssets.put(
          expectedAssetsOrderedDictionary.get(i - 2), valueOf(nameAndAssetsData.get(i)));
    }
    return clientAssets;
  }
}
