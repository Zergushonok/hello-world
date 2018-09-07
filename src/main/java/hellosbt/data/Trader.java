package hellosbt.data;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
  ConcurrentHashMap<Asset, Integer> assets;

  public static Trader of(String name, int balance, Map<Asset, Integer> assets) {
    return new Trader(name, new AtomicInteger(balance), new ConcurrentHashMap<>(assets));
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
