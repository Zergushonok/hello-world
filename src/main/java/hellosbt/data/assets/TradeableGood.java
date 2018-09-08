package hellosbt.data.assets;

import static lombok.AccessLevel.PRIVATE;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * An implementation of the Asset with an arbitrary String (e.g. the name) as the identifier.
 * The hashcode of this object's ID serves as the hashcode of the object itself.
 * The goods are equal if their IDs are equal.
 */

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@EqualsAndHashCode
@ToString
public class TradeableGood implements Asset {

  @NonNull String id;
}
