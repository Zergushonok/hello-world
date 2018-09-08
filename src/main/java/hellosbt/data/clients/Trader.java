package hellosbt.data.clients;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Collections.synchronizedMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

import hellosbt.data.assets.Asset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) @Getter
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
    assets.merge(asset, delta, (affectedAsset, quantity) -> quantity += delta);
  }
}