package hellosbt.core.assets.write;

import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import hellosbt.data.assets.Assets;
import hellosbt.data.clients.AssetsHolder;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service @Profile({DEFAULT, TEST})
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