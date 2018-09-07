package hellosbt.core.clients.read;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.ClientsSupplier;
import hellosbt.data.Client;
import hellosbt.data.Clients;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * ClientsSupplier implementation that supplies Clients from the specified file by converting them
 * from the List of String lines read from this file using the provided converter.
 */

//todo: clientsFromFileReader, ordersFromFileReader, and clientsToFileWriter all use a similar
//  structure; this can be refactored to reduce duplicated boilerplate

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@Slf4j
public class ClientsFromFileReader implements ClientsSupplier<Map<String, Client>> {

  Path filepath;
  ClientsFromStringLinesConverter toClientsConverter;

  public ClientsFromFileReader(
      @Value("#{ '${service.input.clients.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.input.clients.file.name}' ?: 'clients.txt' }")
          Path filepath,
      @Autowired ClientsFromStringLinesConverter toClientsConverter) {

    this.filepath = filepath;
    this.toClientsConverter = toClientsConverter;
  }

  @Override
  public Clients get() {
    log.info("Clients will be read from the file {}", filepath);

    try {
      List<String> assetsAsLines;
      synchronized (this) { //todo: ugly, use nio filechannel and its lock
        assetsAsLines = readAllLines(filepath);
        //todo: this will exhaust RAM if the file is large enough, need to stream the file
        //  and construct the clients entity incrementally via a builder
      }
      return toClientsConverter.apply(assetsAsLines);

    } catch (IOException e) {
      throw new RuntimeException(format("Failed to read the clients data from the file %s",
         filepath), e);
    }
  }
}
