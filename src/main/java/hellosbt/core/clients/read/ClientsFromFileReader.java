package hellosbt.core.clients.read;

import static com.google.common.base.Preconditions.checkState;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.core.locks.FileLocks.fileLock;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.ClientsSupplier;
import hellosbt.data.Client;
import hellosbt.data.Clients;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.locks.Lock;
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

//todo: clientsFromFileReader, ordersFromFileReader, and clientsToFileWriter
//  all use a similar structure; this can be refactored to reduce duplicated boilerplate

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@Slf4j
public class ClientsFromFileReader implements ClientsSupplier<Map<String, Client>> {

  Path filepath;
  ClientsFromStringLinesConverter<Map<String, Client>> toClientsConverter;

  public ClientsFromFileReader(
      @Value("#{ '${service.input.clients.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.input.clients.file.name}' ?: 'clients.txt' }")
          Path filepath,
      @Autowired ClientsFromStringLinesConverter<Map<String, Client>>
          toClientsConverter) {

    this.filepath = filepath;
    this.toClientsConverter = toClientsConverter;
  }

  @Override
  public Clients<Map<String, Client>> get() {
    log.info("Clients will be read from the file {}", filepath);
    return lockAndRead(fileLock(filepath));
  }

  private Clients<Map<String, Client>> lockAndRead(Lock fileLock) {
    try {
      lockOrFail(fileLock);

      //todo: this will exhaust RAM if the file is large enough, need to stream the file
      //  and construct the orders entity incrementally via a builder
      //  this will also speed up the program overall, as the time will not be wasted
      //  on first reading all lines, and then converting them into a map

      return toClientsConverter.apply(readAllLines(filepath));

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(format("Failed to read the clients data from the file %s",
         filepath), e);
    }
  }

  private void lockOrFail(Lock fileLock) throws InterruptedException {

    //todo: timeout should be configurable
    checkState(fileLock.tryLock(10, SECONDS),
        "Could not obtain a file lock (timeout): %s", filepath);
  }
}
