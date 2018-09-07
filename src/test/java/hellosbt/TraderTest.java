package hellosbt;

import static hellosbt.data.TradeableGood.of;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import hellosbt.data.Trader;
import org.junit.Before;
import org.junit.Test;

public class TraderTest extends BaseTest {

  private Trader c1, identicalC1, differentC1, veryDifferentC1, emptyC1,
      notC1WithSameAssets, emptyNotC1;

  @Test
  public void tradersAreEqualIfTheirNamesAreEqual() {
    assertThat(c1).isEqualTo(identicalC1)
        .isEqualTo(differentC1)
        .isEqualTo(veryDifferentC1)
        .isEqualTo(emptyC1);
  }

  @Test
  public void tradersAreNotEqualIfTheirNamesDiffer() {

    assertThat(c1).isNotEqualTo(notC1WithSameAssets);
    assertThat(differentC1).isNotEqualTo(notC1WithSameAssets);
    assertThat(veryDifferentC1).isNotEqualTo(notC1WithSameAssets);
    assertThat(emptyC1).isNotEqualTo(emptyNotC1);
  }

  @Before
  public void prep() {
    c1 = Trader.of("C1", 100, ImmutableMap.of(
        of("A"), 10,
        of("B"), 20,
        of("C"), 30));
    identicalC1 = Trader.of("C1", 100, ImmutableMap.of(
        of("A"), 10,
        of("B"), 20,
        of("C"), 30));
    differentC1 = Trader.of("C1", 200, ImmutableMap.of(
        of("A"), 11,
        of("B"), 22,
        of("C"), 32));
    veryDifferentC1 = Trader.of("C1", 200, ImmutableMap.of(
        of("C"), 11,
        of("D"), 23,
        of("E"), 35,
        of("F"), 12));
    emptyC1 = Trader.of("C1", 0, emptyMap());

    notC1WithSameAssets = Trader.of("C2", 100, ImmutableMap.of(
        of("A"), 10,
        of("B"), 20,
        of("C"), 30));
    emptyNotC1 = Trader.of("C2", 0, emptyMap());
  }
}
