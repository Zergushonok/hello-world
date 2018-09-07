package hellosbt;

import static hellosbt.data.TradeableGood.of;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TradeableGoodTest extends BaseTest {

  @Test
  public void goodsWithSameNameAreEqual() {

    assertThat(of("A")).isEqualTo(of("A"));
  }

  @Test
  public void goodsWithDiffNameAreNotEqual() {

    assertThat(of("A")).isNotEqualTo(of("B"));
  }
}
