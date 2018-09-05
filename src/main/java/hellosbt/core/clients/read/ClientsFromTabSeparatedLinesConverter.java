package hellosbt.core.clients.read;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;

import hellosbt.data.Clients;
import java.util.Collection;
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
public class ClientsFromTabSeparatedLinesConverter implements ClientsFromStringLinesConverter {

  //todo: reimplement
  @Override
  public Clients apply(Collection<String> assetsLines) {
    log.debug("Processing {} lines of assets.", assetsLines.size());

    return null;
  }
}
