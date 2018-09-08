package hellosbt.data.clients;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Collections.synchronizedMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * An implementation of Client intended to be partially mutable but thread safe.
 *
 * Upon construction, the provided initial balance is stored as AtomicInteger,
 * while the supplied map of assets is wrapped into a synchronized map.
 * The more efficient ConcurrentHashMap is not used, because this impl wants to preserve
 * the order of entries from the input Map in case it is a LinkedHashMap.
 *
 * Updates to the client's balance and assets are atomic.
 * The map returned by getAssets is a read-only view, hence the only way to modify it
 * is via the modifyAssetQuantity method.
 * The case, when the modifyAssetQuantity method is asked to modify the quantity of an asset
 * that is not in the map, is handled as if this asset's quantity was 0.
 *
 * The client's name provided upon construction should be a non-null/non-empty String.
 * The assets map provided upon construction can be empty but cannot be null.
 * modifyAssetQuantity method will obviously fail if the assets map is immutable.
 *
 * Note that this implementation does not pay any attention to the negative balance
 * or negative quantities of assets.
 */

@RequiredArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@EqualsAndHashCode(of = "name")
@ToString
public class Trader implements Client {

  String name;
  AtomicInteger balance;
  Map<Asset, Integer> assets;

  public static Trader of(@NonNull String name,
                          int balance,
                          @NonNull Map<Asset, Integer> assets) {

    return new Trader(validate(name),
        new AtomicInteger(balance),
        synchronizedMap(assets));
    /* since we need to guarantee both:
      - that the order of entries is preserved in case the LinkedHashMap is supplied
      - that the computeIfAbsent is atomic */
  }

  //todo: move to a validator
  private static String validate(String name) {
    return requireNonNull(emptyToNull(name),
        "A client's name cannot be empty");
  }

  @Override
  public int getBalance() {
    return balance.intValue();
  }

  @Override
  public Map<Asset, Integer> getAssets() {
    return unmodifiableMap(assets);
  }

  @Override
  public void modifyBalance(int delta) {
    balance.addAndGet(delta);
  }

  @Override
  public void modifyAssetQuantity(Asset asset, int delta) {
    assets.computeIfPresent(asset, (affectedAsset, quantity) -> quantity += delta);
  }
}