package decolib.decompositions;

import decolib.blankets.Blanket;
import decolib.decompositions.results.DecompositionBlankets;
import org.fest.assertions.Assertions;
import org.junit.Test;

public class Example_tms_deco_serial_Test
{
  // This example shows the parallel decomposition for function derived from MCNC Benchmark "tms", obtained
  // after performing a few decompositions on original function. In this case the function shown below
  // is a part of multiple-valued network, which was created during that process.

  @Test
  public void serialDecomposition_test1()
  {
    // The function:
    // i2 i4 i7 Q  | o0 o2 o3
    // 1  1  0  G0 | 1  -  -
    // 1  1  -  G6 | -  -  1
    // 0  0  0  G7 | -  1  -
    // 1  0  1  G0 | -  1  1
    // 1  1  1  G2 | -  1  1
    // 1  1  1  G5 | -  1  1
    // 0  1  0  G0 | -  1  1
    // 1  1  0  G2 | -  1  -
    // 1  -  -  G1 | -  -  1
    // 1  0  0  G5 | 1  1  -
    // 0  1  1  G5 | -  -  1
    // 0  -  -  G1 | -  1  -
    // 0  0  1  G0 | -  -  1
    // 0  0  1  G2 | -  -  1
    // 1  1  1  G7 | -  1  -
    // 0  0  0  G0 | -  -  1
    // 1  1  0  G7 | -  1  1
    // 1  0  1  G2 | -  1  1
    // 0  1  0  G5 | -  -  1
    // 1  1  0  G5 | -  1  1
    // 1  0  0  G2 | -  1  1
    // 1  0  0  G0 | -  1  1
    // 1  0  0  G7 | -  1  1
    // 1  0  1  G7 | -  1  -
    // 0  0  1  G5 | -  -  1
    // 1  0  1  G5 | -  1  1
    // 0  0  1  G7 | 1  1  1
    // 1  1  -  G0 | -  1  1
    // 0  0  0  G2 | 1  1  1
    // 1  -  -  G3 | -  1  -
    // 0  -  -  G3 | -  -  1
    // 0  -  -  G5 | 0  -  -
    // 0  -  0  G7 | 0  -  -
    // 0  -  -  G0 | 0  -  -
    // -  -  1  G0 | 0  -  -
    // -  -  1  G2 | 0  -  -
    // 1  0  -  G0 | 0  -  -
    // 1  0  -  G2 | 0  -  -
    // 1  0  -  G7 | 0  -  -
    // -  1  -  G7 | 0  -  -
    // -  1  -  G5 | 0  -  -
    // -  1  -  G2 | 0  -  -
    // 1  -  1  G5 | 0  -  -
    // -  -  -  G1 | 0  -  -
    // -  -  -  G3 | 0  -  -
    // -  -  -  G4 | 0  -  -
    // -  -  -  G0 | -  -  -
    // -  -  -  G2 | -  -  -
    // -  -  -  G6 | -  -  -
    // -  -  -  G7 | -  -  -
    // -  -  -  G5 | -  -  -
    // -  -  -  G4 | -  -  -
    // -  -  -  G6 | 0  -  -
    // 0  0  0  G5 | 0  0  0
    // 0  -  1  G2 | -  0  -
    // 0  -  1  G5 | -  0  -
    // 0  1  -  G2 | 0  0  0
    // 0  1  -  G4 | 0  0  0
    // 0  1  -  G2 | 0  0  -
    // 0  1  -  G5 | 0  0  -
    // 0  1  -  G4 | 0  0  -
    // 0  -  0  G7 | 0  -  0
    // 0  1  -  G7 | 0  0  0
    // 0  0  -  G0 | 0  0  -
    // 0  0  -  G4 | 0  0  -
    // 0  1  1  G0 | 0  0  0
    // 0  -  -  G4 | 0  0  0
    // 0  -  -  G3 | -  0  -
    // 0  -  -  G4 | -  0  -
    // 0  -  -  G1 | 0  -  0
    // 0  -  -  G4 | 0  -  0
    // -  0  0  G6 | -  -  0
    // -  0  0  G5 | -  -  0
    // -  0  0  G4 | -  -  0
    // 1  0  1  G6 | -  -  0
    // -  0  -  G6 | 0  0  0
    // 1  -  1  G7 | -  -  0
    // 1  -  -  G1 | 0  0  -
    // 1  -  -  G6 | 0  0  -
    // 1  -  -  G4 | 0  0  -
    // -  1  0  G2 | -  -  0
    // -  1  0  G4 | -  -  0
    // -  -  -  G4 | 0  0  0
    // 1  -  -  G3 | -  -  0
    // 1  -  -  G4 | -  -  0
    // -  -  -  G3 | -  -  -
    // -  -  -  G1 | -  -  -

    // Y = {o0o2o3}, U = {i7}, V = {i2i4}
    // Iv = i2i4
    Blanket βIv = Blanket.create("1,2,5,6,8,9,15,17,20,28,30,35,36,40,41,42,43,44,45,46,47,48,49,50,51,52,53,77,78,79,80,81,82,83,84,85,86,87; 3,12,13,14,16,25,27,29,31,32,33,34,35,36,44,45,46,47,48,49,50,51,52,53,54,55,56,62,64,65,67,68,69,70,71,72,73,74,76,83,86,87; 4,9,10,18,21,22,23,24,26,30,35,36,37,38,39,43,44,45,46,47,48,49,50,51,52,53,72,73,74,75,76,77,78,79,80,83,84,85,86,87; 7,11,12,19,31,32,33,34,35,36,40,41,42,44,45,46,47,48,49,50,51,52,53,55,56,57,58,59,60,61,62,63,66,67,68,69,70,71,81,82,83,86,87;");
    // Iu = i7
    Blanket βIu = Blanket.create("1,2,3,7,8,9,10,12,16,17,19,20,21,22,23,28,29,30,31,32,33,34,37,38,39,40,41,42,44,45,46,47,48,49,50,51,52,53,54,57,58,59,60,61,62,63,64,65,67,68,69,70,71,72,73,74,76,78,79,80,81,82,83,84,85,86,87; 2,4,5,6,9,11,12,13,14,15,18,24,25,26,27,28,30,31,32,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,55,56,57,58,59,60,61,63,64,65,66,67,68,69,70,71,75,76,77,78,79,80,83,84,85,86,87;");
    Blanket βQ = Blanket.create("G0:1,4,7,13,16,22,28,34,35,37,47,64,66; G1:9,12,44,70,78,87; G2:5,8,14,18,21,29,36,38,42,48,55,57,59,81; G3:30,31,45,68,84,86; G4:46,52,58,61,65,67,69,71,74,80,82,83,85; G5:6,10,11,19,20,25,26,32,41,43,51,54,56,60,73; G6:2,49,53,72,75,76,79; G7:3,15,17,23,24,27,33,39,40,50,62,63,77;");
    Blanket βY = Blanket.create("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,47,48,49,50,51,52,86,87; 1,2,9,11,13,14,16,19,25,31,47,48,49,50,51,52,55,56,68,69,86,87; 1,3,8,10,12,15,24,30,47,48,49,50,51,52,72,73,74,75,77,81,82,84,85,86,87; 1,47,48,49,50,51,52,55,56,68,69,72,73,74,75,77,81,82,84,85,86,87; 2,3,4,5,6,7,8,9,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,28,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,86,87; 2,9,11,13,14,16,19,25,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,55,56,59,60,61,64,65,68,69,78,79,80,86,87; 3,8,12,15,24,30,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,62,70,71,72,73,74,75,77,81,82,84,85,86,87; 32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87;");
    DecompositionBlankets decomposition = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();                                                                     // βIv x βQv <= βG      - true
    Assertions.assertThat(decomposition.G.BxB(decomposition.Iu != null ? decomposition.Qu.BxB(decomposition.Iu) : βQu).BleB(decomposition.H)).isTrue();  // βIu x βG x βQu <= βY - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(decomposition.Q)).isTrue();                                                        // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();                                                                     // βQv x βQu = βQ       - true
  }

  @Test
  public void serialDecomposition_test2()
  {
    // The function:
    // i2 i7 G  Q  | o7 o8 o9                                      i2 i7 Q   | o7 o8 o9
    // 1  -  G0 S2 | 1  1  -                                       1  -  G02 | 1  1  -
    // 1  1  G0 S0 | 1  1  1                                       1  1  G00 | 1  1  1
    // 0  0  G3 S0 | 1  1  1                                       0  0  G30 | 1  1  1
    // 1  0  G0 S0 | 1  -  1                                       1  0  G00 | 1  -  1
    // 1  -  G0 S1 | 1  1  1                                       1  -  G01 | 1  1  1
    // 1  -  G1 S1 | 1  1  1                                       1  -  G11 | 1  1  1
    // 1  -  G3 S1 | 1  1  1                                       1  -  G31 | 1  1  1
    // 1  0  G2 S0 | 1  1  1                                       1  0  G20 | 1  1  1
    // 0  -  G0 S1 | 1  1  -                                       0  -  G01 | 1  1  -
    // 0  -  G1 S1 | 1  1  -                                       0  -  G11 | 1  1  -
    // 0  -  G3 S1 | 1  1  -                                       0  -  G31 | 1  1  -
    // 0  1  G2 S0 | 1  1  1                                       0  1  G20 | 1  1  1
    // 1  1  G2 S0 | 1  1  1                                       1  1  G20 | 1  1  1
    // 1  -  G3 S0 | 1  1  1                                       1  -  G30 | 1  1  1
    // 1  -  G1 S0 | 1  1  1                                       1  -  G10 | 1  1  1
    // 0  -  G1 S0 | 1  1  1                                       0  -  G10 | 1  1  1
    // -  -  G0 S1 | -  -  -      collapsing symbolic inputs       -  -  G01 | -  -  -
    // -  -  G1 S1 | -  -  -     ---------------------------->     -  -  G11 | -  -  -
    // -  -  G3 S1 | -  -  -                                       -  -  G31 | -  -  -
    // -  -  G1 S0 | -  -  -                                       -  -  G10 | -  -  -
    // -  -  G1 S2 | -  -  -                                       -  -  G12 | -  -  -
    // -  -  G0 S0 | -  -  -                                       -  -  G00 | -  -  -
    // -  -  G3 S0 | -  -  -                                       -  -  G30 | -  -  -
    // -  -  G0 S2 | -  -  -                                       -  -  G02 | -  -  -
    // -  -  G2 S0 | -  -  -                                       -  -  G20 | -  -  -
    // 0  0  G2 S0 | 0  0  0                                       0  0  G20 | 0  0  0
    // 0  -  G0 S0 | 0  0  0                                       0  -  G00 | 0  0  0
    // 0  1  G3 S0 | 0  0  0                                       0  1  G30 | 0  0  0
    // 0  -  G0 S1 | -  -  0                                       0  -  G01 | -  -  0
    // 0  -  G1 S1 | -  -  0                                       0  -  G11 | -  -  0
    // 0  -  G3 S1 | -  -  0                                       0  -  G31 | -  -  0
    // -  -  G1 S2 | 0  0  0                                       -  -  G12 | 0  0  0
    // -  0  G1 S2 | -  0  -                                       -  0  G12 | -  0  -
    // -  0  G0 S0 | -  0  -                                       -  0  G00 | -  0  -
    // -  -  G0 S2 | -  -  0                                       -  -  G02 | -  -  0
    // -  -  G1 S2 | -  -  0                                       -  -  G12 | -  -  0

    // Y = {o7o8o9}, U = {}, V = {i2i7}
    // Iv = i2i7
    Blanket βIv = Blanket.create("1,2,5,6,7,13,14,15,17,18,19,20,21,22,23,24,25,32,35,36; 1,4,5,6,7,8,14,15,17,18,19,20,21,22,23,24,25,32,33,34,35,36; 3,9,10,11,16,17,18,19,20,21,22,23,24,25,26,27,29,30,31,32,33,34,35,36; 9,10,11,12,16,17,18,19,20,21,22,23,24,25,27,28,29,30,31,32,35,36;");
    // Iu = null
    Blanket βIu = null;
    Blanket βQ = Blanket.create("G00:2,4,22,27,34; G01:5,9,17,29; G02:1,24,35; G10:15,16,20; G11:6,10,18,30; G12:21,32,33,36; G20:8,12,13,25,26; G30:3,14,23,28; G31:7,11,19,31;");
    Blanket βY = Blanket.create("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25; 1,9,10,11,17,18,19,20,21,22,23,24,25,29,30,31,35,36; 4,17,18,19,20,21,22,23,24,25,33,34; 17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36;");
    DecompositionBlankets decomposition = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
    Assertions.assertThat(decomposition).isNotNull();

    Blanket βQv = decomposition.QvJoinedWithQu ? decomposition.Qu.BxB(decomposition.QvPartial) : decomposition.Qv;
    Blanket βQu = decomposition.QuJoinedWithQv ? decomposition.Qv.BxB(decomposition.QuPartial) : decomposition.Qu;

    Assertions.assertThat(decomposition.Iv.BxB(βQv).BleB(decomposition.G)).isTrue();                                                                     // βIv x βQv <= βG      - true
    Assertions.assertThat(decomposition.G.BxB(decomposition.Iu != null ? decomposition.Qu.BxB(decomposition.Iu) : βQu).BleB(decomposition.H)).isTrue();  // βIu x βG x βQu <= βY - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(decomposition.Q)).isTrue();                                                        // βQv x βQu = βQ       - true
    Assertions.assertThat(decomposition.Qv.BxB(decomposition.Qu).BeqB(βQ)).isTrue();                                                                     // βQv x βQu = βQ       - true
  }
}
