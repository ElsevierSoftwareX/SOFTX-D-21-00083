// Copyright (c) 2021 Mariusz Wiśniewski
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

package decolib.decompositions;

import decolib.blankets.Blanket;
import decolib.blankets.Block;
import decolib.decompositions.results.DecompositionBlankets;
import decolib.decompositions.results.FunctionsDependencyType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Contains decomposition methods for LUT based devices: parallel decomposition and serial one.
 */
public class Decompositions
{
  public static int LUT_INPUTS = 4;  // Number of inputs in LUT cell.

  /**
   * Method finds the parallel decomposition of function Y = F(X), where X represents binary (I) and multiple-valued inputs (Q), i.e. X = I u Q.
   * The following results may be obtained:
   * - dis-joint decomposition: Yg = G(V), Yh = H(U), where V u U = X and Yh u Yh = Y,
   * - joint decomposition: Yg = G(V,U), Yh = H(V) or Yg = G(V), Yh = H(V,U), where V u U = X and Yh u Yh = Y.
   * Method requires to have provided the initial split of output function Y.
   *
   * @param I blanket for binary inputs (may be null)
   * @param Q blanket for input that being the object for encoding (binary/multiple-valued) through decomposition
   * @param G blanket for resulting function G, i.e. Y = G x H
   * @param H blanket for resulting function H, i.e. Y = G x H
   * @return blankets computed for decomposition
   */
  public static DecompositionBlankets parallelDecomposition(Blanket I, @NotNull Blanket Q, @NotNull Blanket G, @NotNull Blanket H)
  {
    // Checking whether the function is deterministic one and may be decomposed, i.e. the relation (I x Q) <= (G x H) is fulfilled.
    if((I == null && Q.BleB(G.BxB(H, false))) || (I != null && I.BxB(Q, false).BleB(G.BxB(H, false))))
    {
      DecompositionBlankets decomposition = DecompositionBlankets.create();

      // Decomposition variant: I <= G
      if(I != null && I.BleB(G))
        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_I;

      // Decomposition variant: Q <= G
      if(decomposition.dependencyOfG == null && Q.BleB(G))
      {
        decomposition.Qv = DecompositionsHelpers.generateMinimalBlanket(Q, Q, G);
        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_Q;
      }

      // Decomposition variant: I x Q <= G
      if(decomposition.dependencyOfG == null && I != null && I.BxB(Q, false).BleB(G))
      {
        decomposition.Qv = DecompositionsHelpers.generateMinimalBlanket(I, Q, G);
        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_I_AND_Q;
      }

      // Decomposition variant: I <= H
      if(I != null && I.BleB(H))
        decomposition.dependencyOfH = FunctionsDependencyType.DEPENDS_ON_I;

      // Decomposition variant: Q <= H
      if(decomposition.dependencyOfH == null && Q.BleB(H))
      {
        if(decomposition.dependencyOfG == FunctionsDependencyType.DEPENDS_ON_I)
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(Q, Q, H);
        else
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(Q, Q, decomposition.Qv, H);

        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_Q;
      }

      // Decomposition variant: I x Q <= H
      if(decomposition.dependencyOfH == null && I != null && I.BxB(Q, false).BleB(H))
      {
        if(decomposition.dependencyOfG == FunctionsDependencyType.DEPENDS_ON_I)
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(I, Q, H);
        else
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(I, Q, decomposition.Qv, H);

        decomposition.dependencyOfH = FunctionsDependencyType.DEPENDS_ON_I_AND_Q;
      }

      // Checking the possibility for finding joint type of decomposition.
      if(decomposition.Qv != null && decomposition.Qu != null)
      {
        if(decomposition.Qv.BleB(decomposition.Qu))
        {
          decomposition.QvPartial = DecompositionsHelpers.generateMinimalBlanket(decomposition.Qu, decomposition.Qv, decomposition.Qv);
          if(decomposition.QvPartial != null && decomposition.QvPartial.getBlocksCount() == 1) decomposition.QvPartial = null;  // It means that Qv = Qu.
          decomposition.QvJoinedWithQu = true;
        }
        else
          if(decomposition.Qu.BleB(decomposition.Qv))
          {
            decomposition.QuPartial = DecompositionsHelpers.generateMinimalBlanket(decomposition.Qv, decomposition.Qu, decomposition.Qu);
            if(decomposition.QuPartial != null && decomposition.QuPartial.getBlocksCount() == 1) decomposition.QuPartial = null;  // It means that Qv = Qu.
            decomposition.QuJoinedWithQv = true;
          }
      }

      // Post-processing.
      decomposition.type = DecompositionType.PARALLEL_Q;
      decomposition.I = I;
      decomposition.Q = Q;
      decomposition.G = G;
      decomposition.H = H;
      if(decomposition.Qv != null) decomposition.Qv.sortBlocks(false);                // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.Qu != null) decomposition.Qu.sortBlocks(false);                // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.QvPartial != null) decomposition.QvPartial.sortBlocks(false);  // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.QuPartial != null) decomposition.QuPartial.sortBlocks(false);  // false = sorting for presentation or farther decomposition/optimization.

      return decomposition;
    }

    return null;
  }

  /**
   * Method finds the parallel decomposition of function Y = F(X), where X represents binary (Iv and Iu) and multiple-valued inputs (Q), i.e. X = Iv u Iu u Q.
   * The following results may be obtained:
   * - dis-joint decomposition: Yg = G(Iv,V), Yh = H(Iu,U), where V u U = Q and Yh u Yh = Y,
   * - joint decomposition: Yg = G(Iv,V,U), Yh = H(Iu,V) or Yg = G(Iv,V), Yh = H(Iu,V,U), where V u U = Q and Yh u Yh = Y.
   * Method requires to have provided the initial split of output function Y.
   *
   * @param Iv blanket for binary inputs for function G (may be null)
   * @param Iu blanket for binary inputs for function H (may be null)
   * @param Q blanket for input that being the object for encoding (binary/multiple-valued) through decomposition
   * @param G blanket for resulting function G, i.e. Y = G x H
   * @param H blanket for resulting function H, i.e. Y = G x H
   * @return blankets computed for decomposition
   */
  public static DecompositionBlankets parallelDecomposition(Blanket Iv, Blanket Iu, @NotNull Blanket Q, @NotNull Blanket G, @NotNull Blanket H)
  {
    // Checking whether the function is deterministic one and may be decomposed, i.e. the relation (I x Q) <= (G x H) is fulfilled.
    Blanket I = Iv == null ? Iu : Iv.BxB(Iu);
    if((I == null && Q.BleB(G.BxB(H, false))) || (I != null && I.BxB(Q, false).BleB(G.BxB(H, false))))
    {
      DecompositionBlankets decomposition = DecompositionBlankets.create();

      // Decomposition variant: Iv <= G
      if(Iv != null && Iv.BleB(G))
        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_I;

      // Decomposition variant: Q <= G
      if(decomposition.dependencyOfG == null && Q.BleB(G))
      {
        decomposition.Qv = DecompositionsHelpers.generateMinimalBlanket(Q, Q, G);
        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_Q;
      }

      // Decomposition variant: Iv x Q <= G
      if(decomposition.dependencyOfG == null && Iv != null && Iv.BxB(Q, false).BleB(G))
      {
        decomposition.Qv = DecompositionsHelpers.generateMinimalBlanket(Iv, Q, G);
        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_I_AND_Q;
      }

      // Decomposition variant: Iu <= H
      if(Iu != null && Iu.BleB(H))
        decomposition.dependencyOfH = FunctionsDependencyType.DEPENDS_ON_I;

      // Decomposition variant: Q <= H
      if(decomposition.dependencyOfH == null && Q.BleB(H))
      {
        if(decomposition.dependencyOfG == FunctionsDependencyType.DEPENDS_ON_I)
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(Q, Q, H);
        else
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(Q, Q, decomposition.Qv, H);

        decomposition.dependencyOfG = FunctionsDependencyType.DEPENDS_ON_Q;
      }

      // Decomposition variant: Iu x Q <= H
      if(decomposition.dependencyOfH == null && Iu != null && Iu.BxB(Q, false).BleB(H))
      {
        if(decomposition.dependencyOfG == FunctionsDependencyType.DEPENDS_ON_I)
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(Iu, Q, H);
        else
          decomposition.Qu = DecompositionsHelpers.generateMinimalBlanket(Iu, Q, decomposition.Qv, H);

        decomposition.dependencyOfH = FunctionsDependencyType.DEPENDS_ON_I_AND_Q;
      }

      // Checking the possibility for finding joint type of decomposition.
      if(decomposition.Qv != null && decomposition.Qu != null)
      {
        if(decomposition.Qv.BleB(decomposition.Qu))
        {
          decomposition.QvPartial = DecompositionsHelpers.generateMinimalBlanket(decomposition.Qu, decomposition.Qv, decomposition.Qv);
          if(decomposition.QvPartial != null && decomposition.QvPartial.getBlocksCount() == 1) decomposition.QvPartial = null;  // It means that Qv = Qu.
          decomposition.QvJoinedWithQu = true;
        }
        else
        if(decomposition.Qu.BleB(decomposition.Qv))
        {
          decomposition.QuPartial = DecompositionsHelpers.generateMinimalBlanket(decomposition.Qv, decomposition.Qu, decomposition.Qu);
          if(decomposition.QuPartial != null && decomposition.QuPartial.getBlocksCount() == 1) decomposition.QuPartial = null;  // It means that Qv = Qu.
          decomposition.QuJoinedWithQv = true;
        }
      }

      // Post-processing.
      decomposition.type = DecompositionType.PARALLEL_Q;
      decomposition.I = null;
      decomposition.Iv = Iv;
      decomposition.Iu = Iu;
      decomposition.Q = Q;
      decomposition.G = G;
      decomposition.H = H;
      if(decomposition.Qv != null) decomposition.Qv.sortBlocks(false);                // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.Qu != null) decomposition.Qu.sortBlocks(false);                // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.QvPartial != null) decomposition.QvPartial.sortBlocks(false);  // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.QuPartial != null) decomposition.QuPartial.sortBlocks(false);  // false = sorting for presentation or farther decomposition/optimization.

      return decomposition;
    }

    return null;
  }

  /**
   * Method finds the serial decomposition of function Y = F(X), where X represents binary (I) and multiple-valued inputs (Q), i.e. X = I u Q.
   * The following results may be obtained:
   * - dis-joint decomposition: Y = H(I, U, G(I, V)), where I u V u U = X,
   * - joint decomposition: Y = H(I, U, V, G(I, V)) or Y = H(I, U, G(I, V, U)), where I u V u U = X.
   *
   * @param Iv                  blanket for binary inputs for function G (may be null)
   * @param Iu                  blanket for binary inputs for function H (may be null)
   * @param Q                   blanket for input that being the object for encoding (binary/multiple-valued) through decomposition
   * @param Y                   blanket for output function Y.
   * @param QvExpectedBlocksNum expected number of blocks in blanket Qv (final number depends on many factors, however the algorithm
   *                            will try to finds result as close to given value as is possible)
   * @param findMinimalG        if set to true the the algorithm try to find function G as minimal as possible
   * @return blankets computed for decomposition
   */
  public static DecompositionBlankets serialDecomposition(Blanket Iv, Blanket Iu, @NotNull Blanket Q, @NotNull Blanket Y, int QvExpectedBlocksNum, boolean findMinimalG)
  {
    Blanket I = Iv != null ? (Iu != null ? Iv.BxB(Iu, false) : Iv) : null;

    if((I != null && I.BxB(Q, false).BleB(Y)) || Q.BleB(Y))
    {
      // Algorithm (steps 1-5, described below in the code):
      // 1. Finding the blanket M, used as a helper blanket: According to theorem for serial decomposition it follows that Iv x Qv <= G and Iu x Qu x G <= Y. Thus, there
      //    can be apply: Iu x M <= Y, where Qu x G <= M - this is used for simplifying calculations. Next, following to relation Iv x Qv <= G, there may be assumed that
      //    blanket G will be composed using blocks from blocks of product (Iv x Q), which is also the worst case of blanket G. Next, using the simplification Iu x M <= Y
      //    there may be computed blanket M, which fulfils that relation. In this case the blocks of G = Iv x Q should be used, what also is the worst case of blanket M.
      //    The best blanket M is the one, which fulfills Iu * M <= Y relation. Blanket M simplifies finding the blanket G.
      //
      Blanket M = (Iu == null ? Blanket.copy(Y) : DecompositionsHelpers.generateMinimalBlanket(Iu, (Iv == null ? Q : Iv.BxB(Q)), Y));

      // 2. Determination of the probable number of blocks in blanket G:
      //    The procedure estimates the quality of the probable final solution for series decomposition and is crucial for the decisions made when selecting the next partial
      //    components in the calculated blankets G, Qv and Qu.
      //    probable number of blocks in blanket G is calculated according the serial decomposition theorem. For the wors case of blanket Qv, equal to blanket Q the relation
      //    Iv * Qv <= G is converted to Iv * Q <= G. In this case the worst blanket, which fulfills that relation comes as result of product for blankets Iv and Q.
      //    Next, there may be assumed the worst case of blanket Qu, also equal to blanket Q, thus, after using it in relation Qu * G <= M, the relation Q * G <= M is obtained.
      //    Moreover G = Iv x Q, so as a result the relation Q * Iv * Q <= M is created. According to the properties of the blanket algebra Q x Q = Q, while taking into account
      //    the relation G = Iv * Q, there is obtained relation G <= M - it is only proper, when blankets Qv and Qu are equal to blanket Q. The relation G <= M allows to estimate
      //    the minimum number of blocks that must be contained in the blanket of function G in order to find the decomposition of function Y, resulting with deterministic
      //    functions G and H. To compute this minimal value the function generateMinimalBlanket() will be used.
      //
      //      GProb = generateMinimalBlanket(Q, Iv.BxB(Q, false), M);
      //
      //    The number of blocks in blanket Gprob is equal to probable number of blocks in blanket G.
      //
      Blanket GProb = DecompositionsHelpers.generateMinimalBlanket(Q, Iv != null ? Iv.BxB(Q, false) : Q, M);

      List<Block> QBlocks = Blanket.copy(Q).getBlocks();

      // Initially, Qv and Qu only consist of the first block of blanket Q. The initial blanket G is also computed for these blankets.
      Blanket Qv = Blanket.create(QBlocks.get(0));
      Blanket Qu = Blanket.create(QBlocks.get(0));
      QBlocks.remove(0);

      Blanket G = (Iv != null ? Iv.BxB(Qv) : Blanket.copy(Qv));

      // The optimal number of blocks of blanket Qv should be equal to 2^(LUT_INPUTS - Log2(Iv)). If it is not possible to find such a blanket, then algorithm tries to find
      // the blanket Qv having as small number of blocks as possible - in this case the function G may have more inputs than have LUT cell and requires further decomposition.
      // When value of parameter QvExpectedBlocksNum is greater than 0, the automatic computation for number of blocks in blanket Qv is omitted. Otherwise, the value of parameter
      // QvMinimumBlockNum will be computed so as to the blanket Qv will have number blocks equal to number of unused inputs of last LUT cell used for encoding of function G.
      //
      int QvMinimumBlockNum = (QvExpectedBlocksNum > 0 ? QvExpectedBlocksNum : (int)Math.pow(2, (LUT_INPUTS - (Iv != null ? (int)Math.ceil(Math.log(Iv.getBlocksCount()) / Math.log(2)) : 0))));

      int QSumNum = (int)Math.ceil(Q.getBlocksCount() / (float)QvMinimumBlockNum);  // QSumNum = number of blocks of blanket Q, which should be summed in the next block of blanket QvTmp.
      int QSumBlkCnt = (QSumNum == 1 ? 0 : 1);                                      // QSumBlkCnt = number of blocks of blanket Q, which are already summed in last blocks of blanket QvTmp,
                                                                                    //              the 0 value means that there is not enough blocks in blanket Q, which may be summed,
                                                                                    //              the 1 value means that only one blocks is summed in blanket QvTmp.
      while(!QBlocks.isEmpty())
      {
        Blanket QvMin = null;  // In each step there are computed minimal blankets Qv, Qu and G, thus they should be nulled here.
        Blanket QuMin = null;
        Blanket GMin = null;

        // 3. In this stage of algorithm the initial value of blanket Qv is already chosen and is equal to the first blocks of blanket Q. The same value is stored in blanket Qu.
        //    Used block is removed from blanket Q.
        // 4. For the remaining blocks of blanket Q it is checked whether summing them with last block of block of blanket Qv cause that the function G should have more than one output,
        //    what also gives compatibility of function G with its input set V. If the summing is possible then it is checked whether there is any of blocks of blanket Qu, where
        //    the given block also may be summed and the operation doesn't cause to increase the number of blocks in blanket G. The summing operation is performed according to
        //    the principle that says that blocks summed in one block of blanket Qv have to be stored in different blocks of blanket Qu. The the rule Qu x Qv = Q is valid.
        // 5. Block used in step 4 is removed from blanket Q. The algorithm ends when there is no more blocks in blanket Q.
        //
        int usedQBlkNum = -1;
        for(int i = 0; i < QBlocks.size(); i++)
        {
          Blanket QvTmp = Blanket.copy(Qv);

          // Adding the block of blanket Q to the block of blanket Qv, whem the number of summed blocks is less than the assumed number (value is computed in each step of the loop).
          // Otherwise, a new block in blanket Qv is created.
          if(QSumBlkCnt == 0)
          {
            // There will be created a new block in blanket QvTmp.
            QvTmp.addBlock(QBlocks.get(i));
          }
          else
          {
            int QvTmpBlkPos = QvTmp.getBlocksCount() - 1;
            QvTmp.getBlocks().set(QvTmpBlkPos, QvTmp.getBlocks().get(QvTmpBlkPos).getBlockPlus(QBlocks.get(i)));
          }

          Blanket GTmp = (Iv != null ? Iv.BxB(QvTmp) : Blanket.copy(QvTmp));
          int minBlkCntQuG = 0;
          int minBlkCntQuM = 0;

          for(int j = 0; j < Qu.getBlocksCount(); j++)
          {
            Blanket QuTmp = Blanket.copy(Qu);

            // Checking whether selected block of blanket Qu (candidate for adding currently processed block of blanket Q) has no blocks in common with
            // the last block of blanket Qv. In this case the product of those blankets results with empty blanket.
            Block QvBlkMulQuBlk = QvTmp.getBlocks().get(QvTmp.getBlocksCount() - 1).getBlockMul(QuTmp.getBlocks().get(j));
            if(QvBlkMulQuBlk.isEmpty())
            {
              QuTmp.getBlocks().set(j, QuTmp.getBlocks().get(j).getBlockPlus(QBlocks.get(i)));
            }
            else
              QuTmp.addBlock(QBlocks.get(i));

            // Computing the measures allowing for evaluation of chosen partial result.
            // It was observed that the best result was obtained when in partial blankets G and Qu number of blocks is the smallest.
            Blanket GTmpMin = DecompositionsHelpers.generateMinimalBlanket(QuTmp, GTmp, M);
            Blanket QuG = QuTmp.BxB(GTmpMin, false);
            Blanket QuM = QuTmp.BxB(M, false);
            boolean optimalResult;

            if(findMinimalG)
            {
              if(GMin == null)
              {
                optimalResult = GTmpMin.getBlocksCount() <= GProb.getBlocksCount();
              }
              else
                optimalResult =
                  ((GMin.getBlocksCount() < GTmpMin.getBlocksCount()) && (GTmpMin.getBlocksCount() <= GProb.getBlocksCount()) && (QuMin.getBlocksCount() > QuTmp.getBlocksCount())) ||
                  ((GMin.getBlocksCount() < GTmpMin.getBlocksCount()) && (GTmpMin.getBlocksCount() <= GProb.getBlocksCount()) && (minBlkCntQuG > QuG.getBlocksCount())) ||
                  ((GMin.getBlocksCount() < GTmpMin.getBlocksCount()) && (GTmpMin.getBlocksCount() <= GProb.getBlocksCount()) && (minBlkCntQuG == QuG.getBlocksCount()) && (minBlkCntQuM > QuM.getBlocksCount())) ||
                  ((GMin.getBlocksCount() == GTmpMin.getBlocksCount()) && (QuMin.getBlocksCount() > QuTmp.getBlocksCount())) ||
                  (GMin.getBlocksCount() > GTmpMin.getBlocksCount());
            }
            else
              optimalResult =
                (GMin == null) ||
                ((GMin.getBlocksCount() <  GTmpMin.getBlocksCount()) && (GTmpMin.getBlocksCount() <= GProb.getBlocksCount()) && (QuMin.getBlocksCount() > QuTmp.getBlocksCount())) ||
                ((GMin.getBlocksCount() <= GTmpMin.getBlocksCount()) && (minBlkCntQuG > QuG.getBlocksCount())) ||
                ((GMin.getBlocksCount() <= GTmpMin.getBlocksCount()) && (minBlkCntQuG == QuG.getBlocksCount()) && (minBlkCntQuM > QuM.getBlocksCount())) ||
                ((GMin.getBlocksCount() == GTmpMin.getBlocksCount()) && (QuMin.getBlocksCount() > QuTmp.getBlocksCount())) ||
                 (GMin.getBlocksCount() >  GTmpMin.getBlocksCount());

            if(optimalResult && QuG.BleB(M))
            {
              minBlkCntQuG = QuG.getBlocksCount();
              minBlkCntQuM = QuM.getBlocksCount();

              usedQBlkNum = i;  // The "usedQBlkNum" value points to block of blanket Q, which was summed in the best partial result in this step of loop.

              QvMin = QvTmp;
              QuMin = QuTmp;
              GMin = GTmpMin;
            }
          }
        }

        if(GMin == null || GMin.getBlocksCount() > GProb.getBlocksCount())
        {
          // If program goes here it means that:
          // - (for condition GMin == null) there was not possible to add any of remaining blocks from blanket Q to blanket Qv.
          // - (for condition GMin.getBlocksCount() > GProb.getBlocksCount()) adding block from blanket Q to any of existing
          //   blocks from blanket Qu causes that the number of blocks in blanket G exceeded the allowed maximum - so block of
          //   blanket Q will be added to blanket Qv as a separate block.
          if(GMin == null)
          {
            usedQBlkNum = 0;

            QvMin = Blanket.copy(Qv);
            QvMin.addBlock(QBlocks.get(usedQBlkNum));

            QSumBlkCnt = 0;  // The value was chosen so that the algorithm will try add blocks of blanket Q to the last block of blanket Qv (it was already created).
          }

          QuMin = Blanket.copy(Qu);
          QuMin.addBlock(QBlocks.get(usedQBlkNum));

          GMin = DecompositionsHelpers.generateMinimalBlanket(QuMin, Iv != null ? Iv.BxB(QvMin) : QvMin, M);
        }

        // Resetting variables before next step of loop.
        Qv = QvMin;
        Qu = QuMin;
        G = GMin;

        // Computing a number of blocks, which should be summed with the last block of blanket Qv, or in case of creating a new block in blanket Qv
        if(++QSumBlkCnt == QSumNum)
        {
          if(QvMinimumBlockNum - Qv.getBlocksCount() > 0)
          {
            QSumNum = (int)Math.ceil((QBlocks.size() - 1) / (float)(QvMinimumBlockNum - Qv.getBlocksCount()));  // Number of blocks from blanket Q, which should be summed in next block of blanket QvTmp.
            QSumBlkCnt = 0;                                                                                     // Number of blocks from blanket Q, which are already summed in the last block of blanket QvTmp.
          }
        }

        // Removing used block from blanket Q.
        QBlocks.remove(usedQBlkNum);
      }

      DecompositionBlankets decomposition = DecompositionBlankets.create();
      decomposition.Qv = Qv;
      decomposition.Qu = Qu;

      // Checking the possibility for finding joint type of decomposition.
      if(decomposition.Qv != null && decomposition.Qu != null)
      {
        if(decomposition.Qv.BleB(decomposition.Qu))
        {
          decomposition.QvPartial = DecompositionsHelpers.generateMinimalBlanket(decomposition.Qu, decomposition.Qv, decomposition.Qv);
          if(decomposition.QvPartial != null && decomposition.QvPartial.getBlocksCount() == 1) decomposition.QvPartial = null;  // It means that Qv = Qu.
          decomposition.QvJoinedWithQu = true;
        }
        else
        if(decomposition.Qu.BleB(decomposition.Qv))
        {
          decomposition.QuPartial = DecompositionsHelpers.generateMinimalBlanket(decomposition.Qv, decomposition.Qu, decomposition.Qu);
          if(decomposition.QuPartial != null && decomposition.QuPartial.getBlocksCount() == 1) decomposition.QuPartial = null;  // It means that Qv = Qu.
          decomposition.QuJoinedWithQv = true;
        }
      }

      // Post-processing.
      decomposition.type = DecompositionType.SERIAL_QGH;
      decomposition.Iv = Iv;
      decomposition.Iu = Iu;
      decomposition.Q = Q;
      decomposition.G = G;
      decomposition.H = Y;
      if(decomposition.Qv != null) decomposition.Qv.sortBlocks(false);                // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.Qu != null) decomposition.Qu.sortBlocks(false);                // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.QvPartial != null) decomposition.QvPartial.sortBlocks(false);  // false = sorting for presentation or farther decomposition/optimization.
      if(decomposition.QuPartial != null) decomposition.QuPartial.sortBlocks(false);  // false = sorting for presentation or farther decomposition/optimization.

      return decomposition;
    }

    return null;
  }
}
