package hellosbt.data.clients;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * An implementation of Clients that stores the clients in a Map with String keys.
 * An example of such a key is the client's name.
 */

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
public class ClientsMap implements Clients<Map<String, Client>> {

  Map<String, Client> clients;
}
