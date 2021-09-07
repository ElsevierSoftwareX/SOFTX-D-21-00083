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

import java.util.List;

/**
 * Contains decomposition helper methods.
 */
public class DecompositionsHelpers
{
  /**
   * Method, using blocks from blanket B, finds the minimal blanket Q, which fulfils the relation A x Q &lt;= F.
   *
   * Algorithm:
   * 1. Block in blanket A should be sorted for synthesis process (blanket class has a proper function, which does it).
   * 2. The process starting from the last block of blanket A (it should be the greatest) - technically the largest blocks
   *    is hard to join with other ones, thus they should be processed as first.
   * 3. For selected block from blanket A (blockA) one of blocks from blanket B (blockB) was chosen (iterating through blocks from blanket B).
   *    If (blockA x blockB &lt;= F) then the next blocks from blanket B will be checked - blockB will be summed with the next blocks from blanket B.
   *    This process lasts until relation (blockA * (blockB(n) + blockB(n+1)...) &gt; F) if fulfilled. The last sum of blocks (blockB(n) + blockB(n+1)...),
   *    which fulfills the relation (blockA * (blockB(n) + blockB(n+1)...) &lt;= F) is the proper result for the given step of process.
   * 4. Algorithm ends when all blocks of blanket B was used.
   *
   * @param A blanket, the first parameter
   * @param B blanket from which blocks should be used to construct the blanket Q
   * @param F blanket, the third parameter
   * @return minimal blanket Q, which fulfills relation (A x Q &lt;= F)
   */
  public static Blanket generateMinimalBlanket(Blanket A, Blanket B, Blanket F)
  {
    Blanket Q = Blanket.empty();
    List<Block> bBlocks = Blanket.copy(B).getBlocks();
    int bBlocksCnt = bBlocks.size();
    boolean used;

    do {
      used = false;
      Block blockQ = null;

      if(bBlocksCnt > 0)
      {
        for(int i = bBlocks.size() - 1; i >= 0; i--)
        {
          if(bBlocks.get(i) != null)
          {
            Block tmpBlockQ = blockQ != null ? blockQ.getBlockPlus(bBlocks.get(i)) : Block.copyBlock(bBlocks.get(i));
            if((A != null && A.BxB(Blanket.create(tmpBlockQ), false).BleB(F)) || Blanket.create(tmpBlockQ).BleB(F))
            {
              // Execution of program in this place indicates, that blockQ fulfills relation (A x blockQ) <= F, ie. there will be no incompatible blocks between
              // product blanket of (A x blockQ) and blanket F.
              if(--bBlocksCnt == 0)
              {
                Q.addBlock(tmpBlockQ, true);  // Adda block to Q blanket, with sorting of blanket for synthesis purpose.
                return Q;
              }

              blockQ = tmpBlockQ;
              bBlocks.set(i, null);  // Setting null means that the block on given position has been used.
              used = true;
            }
          }
        }

        if(used)
          Q.addBlock(blockQ, false);  // Adda block to Q2 blanket, without sorting of blanket.
      }
    } while(used);

    return null;
  }

  /**
   * Method, using blocks from blanket B, finds the minimal blanket Q, which fulfils the relation Q &lt;= F. The method
   * is similar to "generateMinimalBlanket(A, B, F)", the difference is there is no blanket A.
   *
   * @param B blanket from which blocks should be used to construct the blanket Q
   * @param F blanket, the third parameter
   * @return minimal blanket Q, which fulfills relation (Q &lt;= F)
   */
  public static Blanket generateMinimalBlanket(Blanket B, Blanket F)
  {
    Blanket Q = Blanket.empty();
    List<Block> bBlocks = Blanket.copy(B).getBlocks();
    int bBlocksCnt = bBlocks.size();
    boolean used;

    do {
      used = false;
      Block blockQ = null;

      if(bBlocksCnt > 0)
      {
        for(int i = bBlocks.size() - 1; i >= 0; i--)
        {
          if(bBlocks.get(i) != null)
          {
            Block tmpBlockQ = blockQ != null ? blockQ.getBlockPlus(bBlocks.get(i)) : Block.copyBlock(bBlocks.get(i));
            if(Blanket.create(tmpBlockQ).BleB(F))
            {
              // Execution of program in this place indicates, that blockQ fulfills relation (B_A x blockQ) <= B_F, ie. there will be no incompatible blocks between
              // product blanket of (B_A x blockQ) and blanket B_F.
              if(--bBlocksCnt == 0)
              {
                Q.addBlock(tmpBlockQ, true);  // Adda block to Q blanket, with sorting of blanket for synthesis purpose.
                return Q;
              }

              blockQ = tmpBlockQ;
              bBlocks.set(i, null);  // Setting null means that the block, on given position, has been used.
              used = true;
            }
          }
        }

        if(used)
          Q.addBlock(blockQ, false);  // Adds block to Q2 blanket, without sorting of blanket.
      }
    } while(used);

    return null;
  }

  /**
   * Method, using blocks from blanket B, finds the minimal blanket Q2, which fulfils the relations: A x Q2 &lt;= F and Q1 x Q2 = B. The method
   * is similar to "generateMinimalBlanket(A, B, F)", the difference is there is additional condition, which should be fulfilled.
   *
   * @param A blanket, the first parameter
   * @param B blanket from which blocks should be used to construct the blanket Q
   * @param Q1 blanket, a part of the second relation, which should be fulfilled
   * @param F blanket, the forth parameter
   * @return minimal blanket Q2, which fulfills relations: (A x Q2 &lt;= F) and (Q1 x Q2 = B)
   */
  public static Blanket generateMinimalBlanket(Blanket A, Blanket B, Blanket Q1, Blanket F)
  {
    Blanket Q2 = Blanket.empty();
    List<Block> bBlocks = Blanket.copy(B).getBlocks();
    int bBlocksCnt = bBlocks.size();
    boolean used;

    do {
      used = false;
      Block blockQ2 = null;

      if(bBlocksCnt > 0)
      {
        for(int i = bBlocks.size() - 1; i >= 0; i--)
        {
          if(bBlocks.get(i) != null)
          {
            Block tmpBlockQ2 = blockQ2 != null ? blockQ2.getBlockPlus(bBlocks.get(i)) : Block.copyBlock(bBlocks.get(i));
            if(A.BxB(Blanket.create(tmpBlockQ2), false).BleB(F))
            {
              // Execution of program in this place indicates, that blockQ fulfills relation (A x blockQ) <= F, i.e. there will be no incompatible blocks between
              // the product of blanket (A x blockQ) and blanket F.
              // Now, there will be check whether relation (blockQ * Q1) = B is fulfilled. In practice it comes down to checking whether the new block (from summing),
              // after multiplication with blanket Q1 do not generate an incompatible block with blanket B. In the following algorithm there will be checked whether the
              // new block (from summing), after multiplication with blanket Q1 generates a block, which is present in blanket B and is in equality with that block from blanket B.
              boolean valid = true;
              checkBlocksQ1Q2: for(Block block : Q1.BxB(Blanket.create(tmpBlockQ2), false).getBlocks())
              {
                for(Block blockB : B.getBlocks())
                  if(block.getBlockEq(blockB)) continue checkBlocksQ1Q2;

                valid = false;
                break;
              }

              if(valid)
              {
                if(--bBlocksCnt == 0)
                {
                  Q2.addBlock(tmpBlockQ2, true);  // Adda block to Q2 blanket, with sorting of blanket for synthesis purpose.
                  return Q2;
                }

                blockQ2 = tmpBlockQ2;
                bBlocks.set(i, null);  // Setting null means that the block on given position has been used.
                used = true;
              }
            }
          }
        }

        if(used)
          Q2.addBlock(blockQ2, false);  // Adda block to Q2 blanket, without sorting of blanket.
      }
    } while(used);

    return null;
  }
}
