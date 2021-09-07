package decolib.blankets;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.util.Arrays;

public class BlanketTest
{
  @Test
  public void blanketOperations_test1()
  {
    Blanket β = Blanket.create("A", "B");
    β.addTerms("A", Arrays.asList(1, 2, 3));
    β.addTerms("B", Arrays.asList(1, 2, 64));
    Assertions.assertThat(β.toString()).isEqualTo("Blanket{blocks=[A:1,2,3; B:1,2,64;]}");
    Assertions.assertThat(β.getBlock("A").getBlockPlus(β.getBlock("B")).toString()).isEqualTo("A+B:1,2,3,64;");
    Assertions.assertThat(β.getBlock("B").getBlockPlus(β.getBlock("A")).toString()).isEqualTo("B+A:1,2,3,64;");
  }

  @Test
  public void blanketOperations_test2()
  {
    Blanket βb1 = Blanket.create("0", "1");
    βb1.addTerms("0", 1, 2);
    βb1.addTerms("1", 4, 5);
    Assertions.assertThat(βb1.toString()).isEqualTo("Blanket{blocks=[0:1,2; 1:4,5;]}");

    Blanket βb2 = Blanket.create("0", "1");
    βb2.addTerms("0", 1, 2, 3);
    βb2.addTerms("1", 1, 4, 5, 6);
    Assertions.assertThat(βb2.toString()).isEqualTo("Blanket{blocks=[0:1,2,3; 1:1,4,5,6;]}");

    Blanket βb3 = Blanket.create("0", "1");
    βb3.addTerms("0", 1, 2, 3);
    βb3.addTerms("1", 3, 4, 5);
    Assertions.assertThat(βb3.toString()).isEqualTo("Blanket{blocks=[0:1,2,3; 1:3,4,5;]}");

    Assertions.assertThat(βb1.BleB(βb2)).isTrue();   // 0:1,2; 1:4,5; <= 0:1,2,3; 1:1,4,5,6; - expected value is equal to "true".
    Assertions.assertThat(βb3.BleB(βb2)).isFalse();

    Assertions.assertThat(βb1.BxB(βb2).toString()).isEqualTo("Blanket{blocks=[B1:1,2; B2:4,5;]}");
    Assertions.assertThat(βb1.BxB(βb3).toString()).isEqualTo("Blanket{blocks=[B1:1,2; B2:4,5;]}");
    Assertions.assertThat(βb2.BxB(βb3).toString()).isEqualTo("Blanket{blocks=[B2:1,2,3; B1:4,5;]}");

    Blanket βb4 = Blanket.create("0:1,2,3; 1:3,4,5;");
    Assertions.assertThat(βb3.BeqB(βb2)).isFalse();
    Assertions.assertThat(βb3.BeqB(βb4)).isTrue();

    Blanket βb5 = Blanket.create("0:4,10; 1:1,4,6,10;");
    Assertions.assertThat(βb5.getBlock("0").getBlockLe(βb5.getBlock("1"))).isTrue();   // βb5.0 le βb5.1 - true
    Assertions.assertThat(βb5.getBlock("1").getBlockLe(βb5.getBlock("0"))).isFalse();  // βb5.1 le βb5.0 - false

    Blanket βb6 = Blanket.create("0:2,4,10; 1: 1,4,6,10;");
    Assertions.assertThat(βb6.getBlock("0").getBlockLe(βb6.getBlock("1"))).isFalse();  // βb6.0 le βb6.1 - false
    Assertions.assertThat(βb6.getBlock("1").getBlockLe(βb6.getBlock("0"))).isFalse();  // βb6.1 le βb6.0 - false
  }

  @Test
  public void blanketOperations_test3()
  {
    Blanket βminus = Blanket.create("A:1,4,6,10; B:2,4,9,10;");
    Assertions.assertThat(βminus.getBlock("A").getBlockMinus(βminus.getBlock("B")).toString()).isEqualTo("1,6;");  // 1,4,6,10; - 2,4,9,10; = 1,6;
  }

  @Test
  public void blanketOperations_test4()
  {
    Blanket βcommonPart = Blanket.create("B2:1,2,4,5,6,7,8,9,13,15,17,19; B3:1,3,4,5,6,7,8,9,13,15,17,19; B1:10,11,12,14,16,18;");
    Assertions.assertThat(βcommonPart.haveBlocksCommonPart()).isTrue();
    Assertions.assertThat(βcommonPart.getBlocksCommonPart().toString()).isEqualTo("1,4,5,6,7,8,9,13,15,17,19;");

    String[] expectedValue = {"B2:2;", "B3:3;", "B1:10,11,12,14,16,18;"};
    Block commonPartBlock = βcommonPart.getBlocksCommonPart();
    for(int i = 0; i < βcommonPart.getBlocks().size(); i++)
      Assertions.assertThat(βcommonPart.getBlocks().get(i).getName() + ":" + βcommonPart.getBlocks().get(i).getBlockMinus(commonPartBlock)).isEqualTo(expectedValue[i]);
  }

  @Test
  public void blanketOperations_test5()
  {
    Blanket βA = Blanket.create("A1:1,2; A2:3,4; A3:5,9; A4:6,7,8;");
    Blanket βB = Blanket.create("B1:1;", "B2:2;", "B3:3;", "B4:4;", "B5:5;", "B6:6,8;", "B7:7;", "B8:9;");
    Blanket βF = Blanket.create("F1: 1,3,4,6,8; F2:2,5,7,9;");
    Assertions.assertThat(βF.BicB(βA).toString()).isEqualTo("[A1:1,2;, A4:6,7,8;]");  // Incompatible blocks of blanket βA with blanket βF.
    Assertions.assertThat(βF.BicB(βB).toString()).isEqualTo("[]");                    // Incompatible blocks of blanket βB with blanket βF.
  }
}
