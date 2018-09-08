package hellosbt.core.clients.write;

import static com.google.common.base.Preconditions.checkState;
import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static hellosbt.core.locks.FileLocks.fileLock;
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.ClientsConsumer;
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
 * ClientsConsumer implementation that consumes Clients into the specified file by first converting
 * them into the List of String lines using the provided converter.
 */

//todo: clientsFromFileReader, ordersFromFileReader, and clientsToFileWriter
//  all use a similar structure; this can be refactored to reduce duplicated boilerplate

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@Slf4j
public class ClientsToFileWriter implements ClientsConsumer<Map<String, Client>> {

  Path filepath;
  ClientsToStringLinesConverter<Map<String, Client>> clientsConverter;

  public ClientsToFileWriter(
      @Value("#{ '${service.result.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.result.file.name}' ?: 'result.txt' }")
          Path filepath,
      @Autowired ClientsToStringLinesConverter<Map<String, Client>>
          clientsConverter) {

    this.filepath = filepath;
    this.clientsConverter = clientsConverter;
  }

  @Override
  public void accept(Clients<Map<String, Client>> clients) {
    log.info("Clients data will be written to the file {}", filepath);
    lockAndWrite(clients, fileLock(filepath));
  }

  private void lockAndWrite(Clients<Map<String, Client>> clients, Lock fileLock) {
    try {
      lockOrFail(fileLock);

      write(
          createDirectories(this.filepath.getParent())
              .resolve(this.filepath.getFileName()),
          clientsConverter.apply(clients));

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(format("Failed to write the clients data to the file %s",
          this.filepath), e);
    }
  }

  private void lockOrFail(Lock fileLock) throws InterruptedException {

    //todo: timeout should be configurable
    checkState(fileLock.tryLock(10, SECONDS),
        "Could not obtain a file lock (timeout): %s", filepath);
  }
}