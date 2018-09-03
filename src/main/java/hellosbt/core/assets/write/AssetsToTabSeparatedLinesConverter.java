package hellosbt.core.assets.write;

import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import hellosbt.data.assets.Assets;
import hellosbt.data.clients.AssetsHolder;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Assets to string lines converter implementation that transforms each AssetHolder (i.e. Client)
 * into a line of text that is going to be the list of the Client's pre-defined parameters,
 * separated by \t, in a pre-defined order: name, wealth, first asset quantity, second, and so on.
 *
 * Asset quantities are going to be written into the line in the same order they were encountered
 * while traversing the client's Assets map.
 */

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
public class AssetsToTabSeparatedLinesConverter implements AssetsToStringLinesConverter {

  @Override
  public List<String> apply(Assets assets) {
    return assets.getHoldersByNames().values().stream()
        .map(this::toTabulatedLine)
        .collect(toList());
  }

  private String toTabulatedLine(AssetsHolder holder) {
    return holder.getAssets().values().stream()
        .map(this::toAssetQuantity)
        .reduce(holder.getName() + '\t' + holder.getWealth(),
            (first, second) -> first + '\t' + second);
  }

  private String toAssetQuantity(Integer assetQuantity) {
    return valueOf(assetQuantity);
  }
}