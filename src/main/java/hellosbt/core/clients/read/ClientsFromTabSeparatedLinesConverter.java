package hellosbt.core.clients.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.ClientsMap.of;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

import hellosbt.data.Asset;
import hellosbt.data.Client;
import hellosbt.data.Clients;
import hellosbt.data.TradeableGood;
import hellosbt.data.Trader;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Clients form String lines converter implementation that expects each input line to be an array
 * of a client's info, his balance, and his assets quantities separated by \t.
 */

@Service @Profile({FILE_BASED, TEST})
@NoArgsConstructor
@Slf4j
public class ClientsFromTabSeparatedLinesConverter
    implements ClientsFromStringLinesConverter<Map<String, Client>> {

  //todo: inject the dict as a dependency instead of hard-coding
  private static final List<String> expectedAssetsInOrder =
      asList("A", "B", "C", "D");

  @Override
  public Clients<Map<String, Client>> apply(Collection<String> clientsLines) {
    log.debug("Processing {} lines of clients", clientsLines.size());

    return of(clientsLines.stream().map(this::toClient)
        .collect(toMap(Client::getName, identity())));
  }

  private Trader toClient(String rawClientData) {
    log.trace("Parsing the line {} into a Client", rawClientData);

    List<String> clientParts = asList(rawClientData.split("\t"));
    checkArgument(clientParts.size() == 2 + expectedAssetsInOrder.size(),
        "The line %s does not contain a valid client data", rawClientData);

    String name = clientParts.get(0);
    int balance = parseInt(clientParts.get(1));
    Map<Asset, Integer> assets = constructMapOfAssets(clientParts);

    Trader client = Trader.of(name, balance, assets);
    log.trace("The client {} has been parsed from the line {}", client, rawClientData);
    return client;
  }

  private Map<Asset, Integer> constructMapOfAssets(List<String> rawClientData) {

    return range(0, expectedAssetsInOrder.size())
        .mapToObj(dictIndex ->
            assetWithQuantity(rawClientData, dictIndex))
        .collect(
            toMap(Entry::getKey, Entry::getValue));
  }

  private Entry<Asset, Integer> assetWithQuantity(List<String> rawClientData,
                                                  int expectedAssetIndex) {
    return assetWithQuantity(
        TradeableGood.of(
            expectedAssetsInOrder.get(expectedAssetIndex)),
        parseInt(
            rawClientData.get(expectedAssetIndex + 2)));
    /* 3rd piece of the client's data is a quantity of the 1st asset in the dictionary and so on
     * 1st and 2nd pieces are his name and balance */
  }

  private Entry<Asset, Integer> assetWithQuantity(Asset asset, int quantity) {
    return new SimpleEntry<>(asset, quantity);
  }
}
