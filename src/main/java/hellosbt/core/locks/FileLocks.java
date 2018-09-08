package hellosbt.core.locks;

import static com.google.common.util.concurrent.Striped.lazyWeakLock;
import static lombok.AccessLevel.PRIVATE;

import com.google.common.util.concurrent.Striped;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class FileLocks {

  private static final Striped<Lock> fileLocks = lazyWeakLock(3);
  //todo: de-hardcode the number of stripes to configuration
  //  should be dependant on the expected number of file-based suppliers and consumers
  //  working at the same time

  public static Lock fileLock(Path path) {
    return fileLocks.get(path);
  }
}
