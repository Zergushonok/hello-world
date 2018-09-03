package hellosbt.data.assets;

import static lombok.AccessLevel.PRIVATE;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@EqualsAndHashCode @ToString
public class NamedAsset implements Asset {

  String name;

  @Override
  public String getIdentifier() {
    return getName();
  }
}