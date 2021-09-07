package decolib.decompositions;

import decolib.blankets.Blanket;
import decolib.decompositions.results.DecompositionBlankets;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static decolib.decompositions.DecompositionType.PARALLEL_Q;
import static decolib.decompositions.DecompositionType.SERIAL_QGH;
import static decolib.decompositions.results.FunctionsDependencyType.DEPENDS_ON_I_AND_Q;

public class DecompositionsTest
{
  // Tests for decompositions for function (circuit "mc"):
  // x1 x2 x3 | y1 y2 y3
  // 0  -  S0 | 0  0  0
  // -  0  S0 | 0  0  0
  // 1  1  S0 | 0  1  1
  // -  -  S1 | 0  1  0
  // -  -  S2 | 1  0  1
  // 1  0  S3 | 1  0  0
  // 0  -  S3 | 1  1  1
  // -  1  S3 | 1  1  1
  // -  -  S4 | 1  1  0
  // -  -  S5 | 0  0  1

  @Test
  public void parallelDecomposition_test()
  {
    // Functions G = y2, H = y3, at the same time output y1 may be encoded in one LUT4.
    Blanket βI = Blanket.create("1,2,4,5,7,9,10; 1,4,5,7,8,9,10; 2,4,5,6,9,10; 3,4,5,8,9,10;");  // βx1x2
    Blanket βQ = Blanket.create("S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;");                 // βx3
    Blanket βG = Blanket.create("0:1,2,5,6,10; 1:3,4,7,8,9;");  // βy2
    Blanket βH = Blanket.create("0:1,2,4,6,9; 1:3,5,7,8,10;");  // βy3
    DecompositionBlankets decomposition = Decompositions.parallelDecomposition(βI, βQ, βG, βH);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(PARALLEL_Q);
    Assertions.assertThat(decomposition.I.BxB(βQv).BleB(decomposition.G)).isTrue();   // βI x βQv <= βG - true
    Assertions.assertThat(decomposition.I.BxB(βQu).BleB(decomposition.H)).isTrue();   // βI x βQu <= βH - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();  // βQv x βQu = βQ - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isFalse();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0:1,2,3; S4+S1:4,9; S5+S2:5,10; S3:6,7,8;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();
    Assertions.assertThat(decomposition.QuJoinedWithQv).isTrue();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;]}");
    Assertions.assertThat(decomposition.QuPartial.toString()).isEqualTo("Blanket{blocks=[S5+S4+S3+S0:1,2,3,6,7,8,9,10; S2+S1:4,5;]}");
    Assertions.assertThat(decomposition.dependencyOfG).isEqualTo(DEPENDS_ON_I_AND_Q);
    Assertions.assertThat(decomposition.dependencyOfH).isEqualTo(DEPENDS_ON_I_AND_Q);
  }

  @Test
  public void serialDecomposition_test1()
  {
    // F = {y2y3}, U = {}, V = {x1x2}
    Blanket βIv = Blanket.create("1,2,4,5,7,9,10; 1,4,5,7,8,9,10; 2,4,5,6,9,10; 3,4,5,8,9,10;");
    Blanket βIu = null;
    Blanket βQ = Blanket.create("S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;");
    Blanket βY = Blanket.create("1,2,6; 3,7,8; 4,9; 5,10;");
    DecompositionBlankets decomposition = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(SERIAL_QGH);
    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();                                                                     // βIv x βQv <= βG      - true
    Assertions.assertThat(decomposition.G.BxB(decomposition.Iu != null ? decomposition.Qu.BxB(decomposition.Iu) : βQu).BleB(decomposition.H)).isTrue();  // βIu x βG x βQu <= βY - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(decomposition.Q)).isTrue();                                                        // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();                                                                     // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isFalse();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0+S1:1,2,3,4; S2:5; S3+S4:6,7,8,9; S5:10;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0+S3:1,2,3,6,7,8; S1+S4:4,9; S2+S5:5,10;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.G.toString()).isEqualTo("Blanket{blocks=[B4+B5:1,2,4,6,9; B2+B6+B1+B3:3,4,5,7,8,9,10;]}");
    Assertions.assertThat(decomposition.H.toString()).isEqualTo("Blanket{blocks=[1,2,6; 3,7,8; 4,9; 5,10;]}");  // Just for a case.
  }

  @Test
  public void serialDecomposition_test2()
  {
    // F = {y1y2y3}, U = {}, V = {x1x2}
    Blanket βIv = Blanket.create("1,2,4,5,7,9,10; 1,4,5,7,8,9,10; 2,4,5,6,9,10; 3,4,5,8,9,10;");
    Blanket βIu = null;
    Blanket βQ = Blanket.create("S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;");
    Blanket βY = Blanket.create("1,2; 3; 4; 5; 6; 7,8; 9; 10;");  // βy1y2y3
    DecompositionBlankets decomposition = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(SERIAL_QGH);
    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();                                                                     // βIv x βQv <= βG      - true
    Assertions.assertThat(decomposition.G.BxB(decomposition.Iu != null ? decomposition.Qu.BxB(decomposition.Iu) : βQu).BleB(decomposition.H)).isTrue();  // βIu x βG x βQu <= βY - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(decomposition.Q)).isTrue();                                                        // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();                                                                     // βQv x βQu = βQ - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isFalse();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0+S1:1,2,3,4; S2+S3:5,6,7,8; S4:9; S5:10;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4+S5:9,10;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.G.toString()).isEqualTo("Blanket{blocks=[B1+B4+B5:1,2,4,5,6,9; B2+B6+B3:3,4,5,7,8,10;]}");
    Assertions.assertThat(decomposition.H.toString()).isEqualTo("Blanket{blocks=[1,2; 3; 4; 5; 6; 7,8; 9; 10;]}");  // Just for a case.
  }
}
