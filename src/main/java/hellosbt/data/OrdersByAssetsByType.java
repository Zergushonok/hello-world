package hellosbt.data;

import com.google.common.collect.Multimap;
import hellosbt.data.Order.Type;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersByAssetsByType implements Orders<Multimap<Asset, Order>> {

  Map<Type, Multimap<Asset, Order>> orders;

  @Override
  public Multimap<Asset, Order> getOrders(Type type) {
    return orders.get(type);
  }

  @Override
  public Set<Type> getOrderTypes() {
    return orders.keySet();
  }
}
