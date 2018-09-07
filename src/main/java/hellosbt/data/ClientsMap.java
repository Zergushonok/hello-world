package hellosbt.data;

import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) @Getter
public class ClientsMap implements Clients<Map<String, Client>> {

  Map<String, Client> clients;
}
