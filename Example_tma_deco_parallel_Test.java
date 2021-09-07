package decolib.decompositions;

import decolib.blankets.Blanket;
import decolib.decompositions.results.DecompositionBlankets;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static decolib.decompositions.DecompositionType.PARALLEL_Q;
import static decolib.decompositions.results.FunctionsDependencyType.DEPENDS_ON_I_AND_Q;

public class Example_tma_deco_parallel_Test
{
  // This example shows the parallel decomposition for function derived from MCNC Benchmark "tma", obtained
  // after performing a few decompositions on original function. In this case the functions shown below
  // in test methods are a part of multiple-valued network, which was created during that process.

  @Test
  public void parallelDecomposition_test1()
  {
    // The function:
    // i0 i7 i9 Q   | y0 y1 y2 y3 y4
    // 0  1  0  G0  | 0  0  0  0  1
    // 0  1  1  G0  | 0  1  0  1  0
    // 0  1  -  G1  | 0  0  0  1  0
    // 0  0  -  G3  | 0  1  0  0  0
    // 0  1  -  G3  | 0  1  0  0  0
    // 0  1  -  G2  | 0  0  0  1  1
    // 0  0  -  G6  | 0  1  0  0  0
    // 0  1  -  G6  | 0  1  0  0  0
    // 0  1  -  G7  | 0  0  1  0  0
    // 0  1  0  G5  | 0  0  1  0  1
    // 0  1  1  G3  | 0  1  0  0  0
    // 0  1  0  G4  | 0  0  1  1  0
    // 0  1  1  G4  | 0  0  1  1  1
    // 0  0  -  G4  | 0  1  0  0  0
    // 0  -  -  G12 | 1  0  0  1  0
    // 0  1  0  G15 | 0  1  0  0  1
    // 0  0  -  G15 | 0  1  0  0  1
    // 0  -  -  G14 | 0  0  1  1  1
    // 0  1  1  G10 | 0  1  0  1  1
    // 0  0  -  G10 | 1  0  0  1  1
    // 0  1  0  G10 | 1  0  0  1  0
    // 0  1  1  G11 | 0  1  1  0  0
    // 0  0  -  G11 | 1  0  0  1  1
    // 0  1  0  G11 | 1  0  0  1  0
    // 0  1  1  G13 | 0  1  1  0  1
    // 0  1  1  G9  | 0  1  1  0  1
    // 0  1  0  G13 | 0  1  1  1  0
    // 0  1  0  G9  | 1  0  0  0  0
    // 0  0  -  G13 | 1  0  0  0  1
    // 0  0  -  G9  | 1  0  0  0  1
    // 0  1  0  G8  | 0  1  1  1  1
    // 0  1  1  G8  | 1  0  0  0  1
    // 0  0  -  G8  | 1  0  0  0  1
    // 1  -  -  G0  | 1  0  0  0  1
    // 1  1  -  G3  | 1  0  0  1  0
    // 1  0  -  G3  | 1  0  0  1  1
    // 1  -  -  G2  | 0  0  0  0  0
    // 1  -  -  G6  | 0  0  0  0  0
    // 1  1  0  G7  | 0  0  0  0  0

    Blanket βI = Blanket.create("1,3,5,6,8,9,10,12,15,16,18,21,24,27,28,31; 2,3,5,6,8,9,11,13,15,18,19,22,25,26,32; 4,7,14,15,17,18,20,23,29,30,33; 34,35,37,38,39; 34,36,37,38;");  // i0i7i9
    Blanket βQ = Blanket.create("G0:1,2,34; G1:3; G2:6,37; G3:4,5,11,35,36; G4:12,13,14; G5:10; G6:7,8,38; G7:9,39; G8:31,32,33; G9:26,28,30; G10:19,20,21; G11:22,23,24; G12:15; G13:25,27,29; G14:18; G15:16,17;");
    Blanket βG = Blanket.create("1,3,6,15,20,21,23,24,28,29,30,32,33,34,35,36,37,38,39; 2,4,5,7,8,11,14,16,17,19; 9,10,12,13,18; 22,25,26,27,31;");      // y1y2
    Blanket βH = Blanket.create("1,10,16,17,25,26; 2,3,12,27; 4,5,7,8,9,11,14,22,37,38,39; 6,13,18,19,31; 15,21,24,35; 20,23,36; 28; 29,30,32,33,34;");  // y0y3y4
    DecompositionBlankets decomposition = Decompositions.parallelDecomposition(βI, βQ, βG, βH);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(PARALLEL_Q);
    Assertions.assertThat(decomposition.I.BxB(βQv).BleB(decomposition.G)).isTrue();   // βI x βQv <= βG - true
    Assertions.assertThat(decomposition.I.BxB(βQu).BleB(decomposition.H)).isTrue();   // βI x βQu <= βH - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();  // βQv x βQu = βQ - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isFalse();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[G10+G0:1,2,19,20,21,34; G12+G2+G1:3,6,15,37; G15+G6+G3:4,5,7,8,11,16,17,35,36,38; G14+G5+G7:9,10,18,39; G4:12,13,14; G9+G11:22,23,24,26,28,30; G13:25,27,29; G8:31,32,33;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[G15+G5+G0:1,2,10,16,17,34; G1:3; G3:4,5,11,35,36; G14+G2:6,18,37; G7+G6:7,8,9,38,39; G4:12,13,14; G12:15; G10:19,20,21; G11:22,23,24; G13:25,27,29; G9:26,28,30; G8:31,32,33;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.dependencyOfG).isEqualTo(DEPENDS_ON_I_AND_Q);
    Assertions.assertThat(decomposition.dependencyOfH).isEqualTo(DEPENDS_ON_I_AND_Q);
  }

  @Test
  public void parallelDecomposition_test2()
  {
    // The function:
    // g0 g1 Q   | y0 y3 y4
    // 0  0  S0  | 0  0  1
    // 0  1  S0  | 0  1  0
    // 0  0  S1  | 0  1  0
    // 0  1  S1  | 0  1  0
    // 0  0  S5  | 0  0  0
    // 0  1  S5  | 0  0  0
    // 0  0  S4  | 0  1  1
    // 0  1  S4  | 0  1  1
    // 0  0  S3  | 0  0  0
    // 0  1  S3  | 0  0  0
    // 1  1  S9  | 0  1  0
    // 0  0  S9  | 0  1  1
    // 1  0  S9  | 0  0  0
    // 0  0  S2  | 1  1  0
    // 0  1  S2  | 1  1  0
    // 0  0  S10 | 0  1  1
    // 1  0  S10 | 1  1  1
    // 1  1  S10 | 1  1  0
    // 1  0  S5  | 1  1  1
    // 1  1  S5  | 1  1  0
    // 0  0  S6  | 0  0  1
    // 0  0  S7  | 0  0  1
    // 1  1  S6  | 0  1  0
    // 1  1  S7  | 1  0  0
    // 1  0  S6  | 1  0  1
    // 1  0  S7  | 1  0  1
    // 1  1  S8  | 0  1  1
    // 0  0  S8  | 1  0  1
    // 1  0  S8  | 1  0  1
    // 1  1  S0  | 1  0  1
    // 1  0  S0  | 1  0  1
    // 1  1  S4  | 0  0  0
    // 1  0  S4  | 0  0  0
    // 1  1  S3  | 0  0  0
    // 1  0  S3  | 0  0  0

    Blanket βI = Blanket.create("1,3,5,7,9,12,14,16,21,22,28; 2,4,6,8,10,15; 11,18,20,23,24,27,30,32,34; 13,17,19,25,26,29,31,33,35;");    // g0g1
    Blanket βQ = Blanket.create("S0:1,2,30,31; S1:3,4; S2:14,15; S3:9,10,34,35; S4:7,8,32,33; S5:5,6,19,20; S6:21,23,25; S7:22,24,26; S8:27,28,29; S9:11,12,13; S10:16,17,18;");
    Blanket βG = Blanket.create("0:1,5,6,9,10,13,21,22,24,25,26,28,29,30,31,32,33,34,35; 1:2,3,4,7,8,11,12,14,15,16,17,18,19,20,23,27;");  // y3
    Blanket βH = Blanket.create("1,7,8,12,16,21,22,27; 2,3,4,5,6,9,10,11,13,23,32,33,34,35; 14,15,18,20,24; 17,19,25,26,28,29,30,31;");    // y0y4
    DecompositionBlankets decomposition = Decompositions.parallelDecomposition(βI, βQ, βG, βH);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(PARALLEL_Q);
    Assertions.assertThat(decomposition.I.BxB(βQv).BleB(decomposition.G)).isTrue();   // βI x βQv <= βG - true
    Assertions.assertThat(decomposition.I.BxB(βQu).BleB(decomposition.H)).isTrue();   // βI x βQu <= βH - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();  // βQv x βQu = βQ - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isFalse();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0:1,2,30,31; S10+S2+S1:3,4,14,15,16,17,18; S5:5,6,19,20; S4:7,8,32,33; S7+S3:9,10,22,24,26,34,35; S9:11,12,13; S8+S6:21,23,25,27,28,29;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0:1,2,30,31; S3+S1:3,4,9,10,34,35; S5:5,6,19,20; S9+S4:7,8,11,12,13,32,33; S2:14,15; S7+S10:16,17,18,22,24,26; S6:21,23,25; S8:27,28,29;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.dependencyOfG).isEqualTo(DEPENDS_ON_I_AND_Q);
    Assertions.assertThat(decomposition.dependencyOfH).isEqualTo(DEPENDS_ON_I_AND_Q);
  }
}
