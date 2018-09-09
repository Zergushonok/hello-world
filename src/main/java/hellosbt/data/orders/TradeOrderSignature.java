package hellosbt.data.orders;

import static lombok.AccessLevel.PRIVATE;

import hellosbt.data.assets.Asset;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A signature of the order that can be used for grouping in hash tables.
 * Consists of the order's asset and sum.
 *
 * Signatures are equal if their assets and sums are equal.
 * The hash code is calculated based on values of these two fields.
 */

@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = PRIVATE, makeFinal = true) @Getter
@EqualsAndHashCode
@ToString
public class TradeOrderSignature {

  @NonNull Asset asset;
  int sum;
}
