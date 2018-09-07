package hellosbt.core.clients.write;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.ClientsConsumer;
import hellosbt.data.Client;
import hellosbt.data.Clients;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * ClientsConsumer implementation that consumes Clients into the specified file by first converting
 * them into the List of String lines using the provided converter.
 */

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@Slf4j
public class ClientsToFileWriter implements ClientsConsumer<Map<String, Client>> {

  Path filepath;
  ClientsToStringLinesConverter clientsConverter;

  public ClientsToFileWriter(
      @Value("#{ '${service.result.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.result.file.name}' ?: 'result.txt' }")
          Path filepath,
      @Autowired ClientsToStringLinesConverter clientsConverter) {

    this.filepath = filepath;
    this.clientsConverter = clientsConverter;
  }

  @Override
  public void accept(Clients clients) {
    log.info("Clients data will be written to the file {}", filepath);

    try {
      Collection<String> assetsAsLines = clientsConverter.apply(clients);
      synchronized (this) {
        write(createDirectories(filepath.getParent())
            .resolve(filepath.getFileName()), assetsAsLines);
      }

    } catch (IOException e) {
      throw new RuntimeException(format("Failed to write the clients data to the file %s",
          filepath), e);
    }
  }
}
