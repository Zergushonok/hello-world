package hellosbt.core.assets.read;

import static hellosbt.config.Spring.Profiles.DEFAULT;
import static hellosbt.config.Spring.Profiles.TEST;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.assets.AssetsSupplier;
import hellosbt.data.assets.Assets;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service @Profile({DEFAULT, TEST})
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
public class AssetsFromFileReader implements AssetsSupplier {

  Path filepath;
  AssetsFromTabSeparatedLinesConverter toAssetsConverter;

  public AssetsFromFileReader(
      @Value("#{ '${service.input.clients.file.path}' ?: systemProperties['user.home'] }"
          + "/#{ '${service.input.clients.file.name}' ?: 'clients.txt' }")
          Path filepath,
      @Autowired AssetsFromTabSeparatedLinesConverter toAssetsConverter) {

    this.filepath = filepath;
    this.toAssetsConverter = toAssetsConverter;
  }

  @Override
  public Assets get() {
    try {
      List<String> assetsAsLines;
      synchronized (this) { //todo: ugly, use nio filechannel and its lock
        assetsAsLines = readAllLines(filepath);
      }
      return toAssetsConverter.apply(assetsAsLines);

    } catch (IOException e) {
      throw new RuntimeException(format("Failed to read the clients assets from the file %s",
         filepath), e);
    }
  }
}
