package hellosbt.core.assets.write;

import static hellosbt.config.Spring.Profiles.FILE_BASED;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.AssetsConsumer;
import hellosbt.data.clients.Assets;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * AssetsConsumer implementation that consumes Assets into the specified file by first converting
 * them into the List of String lines using the provided converter.
 */

@Service @Profile({FILE_BASED, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
public class AssetsToFileWriter implements AssetsConsumer {

  Path filepath;
  AssetsToStringLinesConverter assetsConverter;

  public AssetsToFileWriter(
      @Value("#{ '${service.result.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.result.file.name}' ?: 'result.txt' }")
          Path filepath,
      @Autowired AssetsToStringLinesConverter assetsConverter) {

    this.filepath = filepath;
    this.assetsConverter = assetsConverter;
  }

  @Override
  public void accept(Assets assets) {
    try {
      Collection<String> assetsAsLines = assetsConverter.apply(assets);
      synchronized (this) {
        write(createDirectories(filepath.getParent())
            .resolve(filepath.getFileName()), assetsAsLines);
      }

    } catch (IOException e) {
      throw new RuntimeException(format("Failed to write the assets %s to the file %s",
          assets, filepath), e);
    }
  }
}
