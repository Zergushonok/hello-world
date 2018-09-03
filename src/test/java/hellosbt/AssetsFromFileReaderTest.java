package hellosbt;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.core.assets.read.AssetsFromFileReader;
import hellosbt.core.assets.read.AssetsFromTabSeparatedLinesConverter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@FieldDefaults(level = PRIVATE)
@Slf4j
public class AssetsFromFileReaderTest extends BaseTest {

  @Autowired AssetsFromFileReader assetsFromFileReader;
  @Autowired AssetsFromTabSeparatedLinesConverter assetsFromTabSeparatedLinesConverter;

  @Test
  public void testTest() {
    log.info(assetsFromFileReader.get().toString());
  }
}
