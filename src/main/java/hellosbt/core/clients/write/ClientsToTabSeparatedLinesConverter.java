package hellosbt.core.clients.write;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import hellosbt.data.Client;
import hellosbt.data.Clients;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Clients to string lines converter implementation that transforms each Client
 * into a line of text.
 */

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
@Slf4j
public class ClientsToTabSeparatedLinesConverter
    implements ClientsToStringLinesConverter<Map<String, Client>> {

  @Override
  public List<String> apply(Clients<Map<String, Client>> clientsData) {

    Map<String, Client> clients = clientsData.getClients();
    log.debug("Transforming {} clients into text lines", clients.size());

    List<String> lines = clients.values().stream()
        .map(this::toTabSeparatedLine)
        .collect(toList());

    log.debug("Transformed {} clients into {} text lines", clients.size(), lines.size());
    return lines;
  }

  private String toTabSeparatedLine(Client client) {
    log.trace("Transforming the client {} into a text line", client);

    String text = client.getAssets().values().stream()
        .map(this::toAssetQuantity)
        .reduce(client.getName() + '\t' + client.getBalance(),
            (asset1, asset2) -> asset1 + '\t' + asset2);

    log.trace("Transformed the client {} into the text line {}", client, text);
    return text;
  }

  private String toAssetQuantity(Integer quantity) {
    return valueOf(quantity);
  }
}