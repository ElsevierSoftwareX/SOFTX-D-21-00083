package decolib.decompositions;

import decolib.blankets.Blanket;
import org.fest.assertions.Assertions;
import org.junit.Test;

public class DecompositionsHelpersTest
{
  @Test
  public void decompositionsHelpers_test1()
  {
    Blanket βA = Blanket.create("A1:1,5,8,10,13,17,18,21,25; A2:2,4,19,20,23,24; A3:3,7,11,12,15,16; A4:6,9,14,22;");
    Blanket βB = Blanket.create("B1:1,2,3; B2:4,5,6; B3:7,8,9; B4:10,11; B5:12,13; B6:14,15; B7:16,17; B8:18,19; B9:20,21; B10:22,23; B11:24,25;");
    Blanket βF = Blanket.create("F1:1,2,3; F2:4,5,6; F3:7,8,9; F4:10,11; F5:12,13; F6:14,15; F7:16,17; F8:18,19; F9:20,21; F10:22,23; F11:24,25;");
    Blanket βQ = DecompositionsHelpers.generateMinimalBlanket(βA, βB, βF);
    Assertions.assertThat(βQ).isNotNull();
    Assertions.assertThat(βQ.toString()).isEqualTo("Blanket{blocks=[B4:10,11; B5:12,13; B8:18,19; B9:20,21; B1:1,2,3; B2:4,5,6; B3:7,8,9; B11+B6:14,15,24,25; B10+B7:16,17,22,23;]}");
    Assertions.assertThat(βA.BxB(βQ).BleB(βF)).isTrue();

    Blanket βQ1 = Blanket.create("B1:10,11; B2:12,13; B3:14,15; B4:16,17; B5:18,19; B6:20,21; B7:22,23; B8:24,25; B9:1,2,3; B10:4,5,6; B11:7,8,9;");
    Blanket βQ2 = DecompositionsHelpers.generateMinimalBlanket(βA, βB, βQ1, βF);
    Assertions.assertThat(βQ2).isNotNull();
    Assertions.assertThat(βQ2.toString()).isEqualTo("Blanket{blocks=[B4:10,11; B5:12,13; B8:18,19; B9:20,21; B1:1,2,3; B2:4,5,6; B3:7,8,9; B11+B6:14,15,24,25; B10+B7:16,17,22,23;]}");
    Assertions.assertThat(βA.BxB(βQ2).BleB(βF)).isTrue();
    Assertions.assertThat(βQ1.BxB(βQ2).BeqB(βB)).isTrue();

    βQ1 = Blanket.create("B1:10,12,24; B2:11,13,25; B3:14,15; B4:16,17; B5:18,19; B6:20,21; B7:22,23; B9:1,2,3; B10:4,5,6; B11:7,8,9;");
    βQ2 = DecompositionsHelpers.generateMinimalBlanket(βA, βB, βQ1, βF);
    Assertions.assertThat(βQ2).isNull();
  }

  @Test
  public void decompositionsHelpers_test2()
  {
    Blanket βI = Blanket.create("1,5,8,10,13,17,18,21,25; 2,4,19,20,23,24; 3,7,11,12,15,16; 6,9,14,22;");
    Blanket βX = Blanket.create("7,8,9,18,19; 12,13,24,25; 14,15,16,17;");
    Blanket βB = Blanket.create("7,8,9; 18,19; 12,13; 24,25; 14,15; 16,17;");
    Blanket βF = Blanket.create("1,2,4,13,17,25; 3,7,9,15,16,21,22; 5,8,10,18,19,20,23,24; 6,11,12,14;");
    Blanket βQ = DecompositionsHelpers.generateMinimalBlanket(βI, βB, βX, βF);
    Assertions.assertThat(βQ).isNotNull();
    Assertions.assertThat(βQ.toString()).isEqualTo("Blanket{blocks=[12,13; 7,8,9; 14,15,18,19; 16,17,24,25;]}");
    Assertions.assertThat(βB.BeqB(βQ.BxB(βX))).isTrue();
  }
}
