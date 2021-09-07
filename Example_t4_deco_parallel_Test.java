package decolib.decompositions;

import decolib.blankets.Blanket;
import decolib.decompositions.results.DecompositionBlankets;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static decolib.decompositions.DecompositionType.PARALLEL_Q;
import static decolib.decompositions.results.FunctionsDependencyType.DEPENDS_ON_I_AND_Q;

public class Example_t4_deco_parallel_Test
{
  // This example shows the parallel decomposition for function derived from MCNC Benchmark "t4", obtained
  // after performing a few decompositions on original function. In this case the function shown below
  // is a part of multiple-valued network, which was created during that process.
  //
  // The function:
  // i1 i3 i7 i8 Q  | g1 g2
  // -  0  0  0  S0 | 0  0
  // 0  -  0  0  S1 | 0  0
  // 1  -  0  0  S1 | 0  1
  // -  -  0  0  S2 | 0  0
  // -  -  0  0  S3 | 0  0
  // -  -  0  1  S3 | 0  1
  // -  -  0  1  S2 | 0  0
  // -  0  0  1  S0 | 0  1
  // -  0  0  1  S1 | 0  1
  // -  1  0  1  S0 | 1  1
  // -  1  0  1  S1 | 1  1
  // -  -  1  1  S3 | 1  1
  // -  -  1  1  S2 | 0  0
  // -  -  1  0  S3 | 1  0
  // -  -  1  0  S2 | 0  0
  // -  -  1  0  S0 | 1  1
  // -  -  1  0  S1 | 0  0
  // -  -  1  1  S0 | 1  0
  // -  -  1  1  S1 | 0  0
  //
  // This function is quite interesting, because G depends on i3i7i8Q and H depends on i1i7i8Q, thus inputs for function G an H are different.
  // To handle this problem, the decomposition will be performed using slightly modified algorithm, that takes into account that functions may
  // have different inputs - see parallelDecomposition_test2() method for details.

  @Test
  public void parallelDecomposition_test1()
  {
    Blanket βI = Blanket.create("1,2,4,5; 1,3,4,5; 6,7,8,9; 6,7,10,11; 12,13,18,19; 14,15,16,17;");  // I = i1i3i7i8
    Blanket βQ = Blanket.create("S0:1,8,10,16,18; S1:2,3,9,11,17,19; S2:4,7,13,15; S3:5,6,12,14;");
    Blanket βG = Blanket.create("0:1,2,3,4,5,6,7,8,9,13,15,17,19; 1:10,11,12,14,16,18;");            // G = g1
    Blanket βH = Blanket.create("0:1,2,4,5,7,13,14,15,17,18,19; 1:3,6,8,9,10,11,12,16;");            // H = g2
    DecompositionBlankets decomposition = Decompositions.parallelDecomposition(βI, βQ, βG, βH);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(PARALLEL_Q);
    Assertions.assertThat(decomposition.I.BxB(βQv).BleB(decomposition.G)).isTrue();   // βI x βQv <= βG - true
    Assertions.assertThat(decomposition.I.BxB(βQu).BleB(decomposition.H)).isTrue();   // βI x βQu <= βH - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();  // βQv x βQu = βQ - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isTrue();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0:1,8,10,16,18; S1:2,3,9,11,17,19; S2:4,7,13,15; S3:5,6,12,14;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();  // There is possible to point out the joint decomposition however it is not profitable.
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0:1,8,10,16,18; S1:2,3,9,11,17,19; S2:4,7,13,15; S3:5,6,12,14;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.dependencyOfG).isEqualTo(DEPENDS_ON_I_AND_Q);
    Assertions.assertThat(decomposition.dependencyOfH).isEqualTo(DEPENDS_ON_I_AND_Q);
  }

  @Test
  public void parallelDecomposition_test2()
  {
    Blanket βIv = Blanket.create("1,2,3,4,5; 6,7,8,9; 6,7,10,11; 12,13,18,19; 14,15,16,17;");    // Iv = i3i7i8
    Blanket βIu = Blanket.create("1,2,4,5; 1,3,4,5; 6,7,8,9,10,11; 12,13,18,19; 14,15,16,17;");  // Iu = i1i7i8
    Blanket βQ = Blanket.create("S0:1,8,10,16,18; S1:2,3,9,11,17,19; S2:4,7,13,15; S3:5,6,12,14;");
    Blanket βG = Blanket.create("0:1,2,3,4,5,6,7,8,9,13,15,17,19; 1:10,11,12,14,16,18;");            // G = g1
    Blanket βH = Blanket.create("0:1,2,4,5,7,13,14,15,17,18,19; 1:3,6,8,9,10,11,12,16;");            // H = g2
    DecompositionBlankets decomposition = Decompositions.parallelDecomposition(βIv, βIu, βQ, βG, βH);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu && decomposition.QvPartial != null ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv && decomposition.QuPartial != null ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.type).isEqualTo(PARALLEL_Q);
    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();  // βIv x βQv <= βG - true
    Assertions.assertThat(decomposition.Iu.BxB(βQu).BleB(decomposition.H)).isTrue();  // βIu x βQu <= βH - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();  // βQv x βQu = βQ  - true
    Assertions.assertThat(decomposition.QvJoinedWithQu).isTrue();
    Assertions.assertThat(decomposition.Qv.toString()).isEqualTo("Blanket{blocks=[S0:1,8,10,16,18; S1:2,3,9,11,17,19; S2:4,7,13,15; S3:5,6,12,14;]}");
    Assertions.assertThat(decomposition.QvPartial).isNull();  // There is possible to point out the joint decomposition however it is not profitable.
    Assertions.assertThat(decomposition.QuJoinedWithQv).isFalse();
    Assertions.assertThat(decomposition.Qu.toString()).isEqualTo("Blanket{blocks=[S0:1,8,10,16,18; S1:2,3,9,11,17,19; S2:4,7,13,15; S3:5,6,12,14;]}");
    Assertions.assertThat(decomposition.QuPartial).isNull();
    Assertions.assertThat(decomposition.dependencyOfG).isEqualTo(DEPENDS_ON_I_AND_Q);
    Assertions.assertThat(decomposition.dependencyOfH).isEqualTo(DEPENDS_ON_I_AND_Q);
  }
}
