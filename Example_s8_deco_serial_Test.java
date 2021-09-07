package decolib.decompositions;

import decolib.blankets.Blanket;
import decolib.decompositions.results.DecompositionBlankets;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static decolib.decompositions.DecompositionType.SERIAL_QGH;

public class Example_s8_deco_serial_Test
{
  // This example shows the serial decomposition for function from MCNC Benchmark "s8", which some inputs were encoded,
  // using symbolic (multiple-valued) variable (i.e. Q = i4i5i6).
  //
  // The function:
  // i0 i1 i2 i3 i4 i5 i6 | y0 y1 y2                             i0 i1 i2 i3 (Q = i4i5i6) | y0 y1 y2
  // 0  0  0  1  0  0  0  | 0  0  0                              0  0  0  1  (S0)         | 0  0  0
  // 0  0  0  0  1  0  0  | 0  0  0                              0  0  0  0  (S3)         | 0  0  0
  // 0  0  0  0  0  1  0  | 0  0  1                              0  0  0  0  (S2)         | 0  0  1
  // 0  0  0  0  0  0  1  | 0  0  1                              0  0  0  0  (S1)         | 0  0  1
  // 0  0  1  1  0  0  0  | 0  0  1                              0  0  1  1  (S0)         | 0  0  1
  // 0  0  1  0  1  0  0  | 0  1  0                              0  0  1  0  (S3)         | 0  1  0
  // 0  0  1  0  0  1  0  | 0  0  1                              0  0  1  0  (S2)         | 0  0  1
  // 0  0  1  0  0  0  1  | 0  0  0                              0  0  1  0  (S1)         | 0  0  0
  // 0  1  0  1  0  0  0  | 0  1  0                              0  1  0  1  (S0)         | 0  1  0
  // 0  1  0  0  1  0  0  | 1  0  0       symbolic encoding      0  1  0  0  (S3)         | 1  0  0
  // 0  1  0  0  0  1  0  | 0  1  0      ------------------->    0  1  0  0  (S2)         | 0  1  0
  // 0  1  0  0  0  0  1  | 1  0  0                              0  1  0  0  (S1)         | 1  0  0
  // 0  1  1  1  0  0  0  | 0  1  1                              0  1  1  1  (S0)         | 0  1  1
  // 0  1  1  0  1  0  0  | 0  0  1                              0  1  1  0  (S3)         | 0  0  1
  // 0  1  1  0  0  1  0  | 0  1  0                              0  1  1  0  (S2)         | 0  1  0
  // 0  1  1  0  0  0  1  | 0  1  0                              0  1  1  0  (S1)         | 0  1  0
  // 1  0  0  1  0  0  0  | 1  0  0                              1  0  0  1  (S0)         | 1  0  0
  // 1  0  0  0  1  0  0  | 1  0  0                              1  0  0  0  (S3)         | 1  0  0
  // 1  0  0  0  0  1  0  | 0  0  0                              1  0  0  0  (S2)         | 0  0  0
  // 1  0  0  0  0  0  1  | 0  1  1                              1  0  0  0  (S1)         | 0  1  1

  @Test
  public void serialDecomposition_test1()
  {
    // Y = {y0y1y2}, U = {i0}, V = {i1i2}
    Blanket βIv = Blanket.create("1,2,3,4,17,18,19,20; 5,6,7,8; 9,10,11,12; 13,14,15,16;");  // Iv = i1i2
    Blanket βIu = Blanket.create("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16; 17,18,19,20;");    // Iu = i0
    Blanket βQ = Blanket.create("S0:1,5,9,13,17; S3:4,8,12,16,20; S2:3,7,11,15,19; S1:2,6,10,14,18;");
    Blanket βY = Blanket.create("1,2,8,19; 3,4,5,7,14; 6,9,11,15,16; 10,12,17,18; 13,20;");
    DecompositionBlankets decomposition = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(SERIAL_QGH);
    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();                                                                     // βIv x βQv <= βG      - true
    Assertions.assertThat(decomposition.G.BxB(decomposition.Iu != null ? decomposition.Qu.BxB(decomposition.Iu) : βQu).BleB(decomposition.H)).isTrue();  // βIu x βG x βQu <= βY - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(decomposition.Q)).isTrue();                                                        // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();                                                                     // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isTrue();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0:1,5,9,13,17; S1:2,6,10,14,18; S2:3,7,11,15,19; S3:4,8,12,16,20;]}");
    Assertions.assertThat(decomposition.QvPartial.toString()).isEqualTo("Blanket{blocks=[S3+S2+S0:1,3,4,5,7,8,9,11,12,13,15,16,17,19,20; S1:2,6,10,14,18;]}");
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0:1,5,9,13,17; S1+S3:2,4,6,8,10,12,14,16,18,20; S2:3,7,11,15,19;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.G.toString()).isEqualTo("Blanket{blocks=[B4+B14+B13:1,2,8,17,18; B8+B6+B1+B15:3,5,10,12,19; B10+B5+B3+B16:4,7,9,14,20; B12+B11+B9+B7+B2:6,11,13,15,16;]}");
    Assertions.assertThat(decomposition.H.toString()).isEqualTo("Blanket{blocks=[1,2,8,19; 3,4,5,7,14; 6,9,11,15,16; 10,12,17,18; 13,20;]}");  // Just for a case.
  }

  @Test
  public void serialDecomposition_test2()
  {
    // Y = {y0y1y2}, U = {}, V = {i0i1i2}
    Blanket βIv = Blanket.create("1,2,3,4; 5,6,7,8; 9,10,11,12; 13,14,15,16; 17,18,19,20;");  // Iv = i1i2
    Blanket βIu = null;                                                                       // Iu = i0
    Blanket βQ = Blanket.create("S0:1,5,9,13,17; S3:4,8,12,16,20; S2:3,7,11,15,19; S1:2,6,10,14,18;");
    Blanket βY = Blanket.create("1,2,8,19; 3,4,5,7,14; 6,9,11,15,16; 10,12,17,18; 13,20;");
    DecompositionBlankets decomposition = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(SERIAL_QGH);
    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();                                                                     // βIv x βQv <= βG      - true
    Assertions.assertThat(decomposition.G.BxB(decomposition.Iu != null ? decomposition.Qu.BxB(decomposition.Iu) : βQu).BleB(decomposition.H)).isTrue();  // βIu x βG x βQu <= βY - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(decomposition.Q)).isTrue();                                                        // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();                                                                     // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isFalse();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0+S1:1,2,5,6,9,10,13,14,17,18; S2+S3:3,4,7,8,11,12,15,16,19,20;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0+S2:1,3,5,7,9,11,13,15,17,19; S1:2,6,10,14,18; S3:4,8,12,16,20;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.G.toString()).isEqualTo("Blanket{blocks=[B2:3,4; B6:11,12; B7:13,14; B9:17,18; B10+B1:1,2,19,20; B4+B3:5,6,7,8; B8+B5:9,10,15,16;]}");
    Assertions.assertThat(decomposition.H.toString()).isEqualTo("Blanket{blocks=[1,2,8,19; 3,4,5,7,14; 6,9,11,15,16; 10,12,17,18; 13,20;]}");  // Just for a case.
  }
}
