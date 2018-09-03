package hellosbt.data.orders;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@ToString
public class OrdersList implements Orders {

  List<Order> orders;
}
