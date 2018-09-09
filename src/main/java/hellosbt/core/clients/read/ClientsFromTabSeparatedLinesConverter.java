package hellosbt.core.clients.read;

import static com.google.common.base.Preconditions.checkArgument;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.data.clients.ClientsMap.of;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

import hellosbt.data.assets.Asset;
import hellosbt.data.assets.TradeableGood;
import hellosbt.data.clients.Client;
import hellosbt.data.clients.Clients;
import hellosbt.data.clients.Trader;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Clients form String lines converter implementation
 * that expects each input line to be an array
 * of a client's info, his balance, and his assets quantities separated by \t.
 *
 * This implementations expects that the first element of the array will be the client's name,
 * the second -- his balance, and the remaining elements -- quantities of his assets.
 *
 * This converter comes with a supplied expected assets dictionary
 * that specifies the names of assets each client should have.
 * Each client is required to possess the same number of assets as listed in the dictionary.
 *
 * This converter respects the encounter order of both the dictionary
 * and the array of a client's assets quantities, i.e. it will treat the first quantity
 * as the quantity of the first asset from the dictionary, and so on.
 *
 * In the resulting map of assets quantities for each client
 * the assets will have the same encounter order as their names in the dictionary.
 *
 * The encounter order of the resulting map of clients
 * will be the same as of the input clients collection.
 */

//todo: extract the schema of a client into a separate component
//  to support other types of clients easier

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

        .collect(toMap(Client::getName, identity(),
            (c1, c2) -> {
              throw new IllegalArgumentException(format("Clients names should be unique "
                  + "but two clients with the same name %s were encountered", c1.getName()));
            },
            LinkedHashMap::new)));
    /* To preserve the order in which the clients were provided by the source */
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
            toMap(Entry::getKey, Entry::getValue,
                (v1, v2) -> {
                  throw new IllegalArgumentException(
                      "Assets possessed by a client should be unique");
                },
                LinkedHashMap::new));
    /* To preserve the order in which the client's assets were provided by the source */
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
