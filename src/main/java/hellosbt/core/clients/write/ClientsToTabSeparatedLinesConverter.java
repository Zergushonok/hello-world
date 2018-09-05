package hellosbt.core.clients.write;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;

import hellosbt.data.Clients;
import java.util.List;
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
public class ClientsToTabSeparatedLinesConverter implements ClientsToStringLinesConverter {

  //todo: reimplement
  @Override
  public List<String> apply(Clients clients) {
    log.debug("Transforming clients data into text lines");

    return null;
  }
}