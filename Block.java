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

package decolib.blankets;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a block of the blanket. Provides methods allowing for performing basic operation on blocks.
 */
public class Block
{
  String name;
  int[] data = new int[1];
  int rank = 0;                     // rank = liczba termów w bloku.
  int minTerm = Integer.MAX_VALUE;  // minTerm = numer najmniejszego termu przechowywanego w bloku.
  int maxTerm = -1;                 // maxTerm = numer największego termu przechowywanego w bloku.

  /**
   * @param name a name of this block, may be empty
   */
  Block(String name)
  {
    this.name = name;
  }

  /**
   * @param name        name of this block, may be empty
   * @param initialTerm term value, which will be set in this block
   */
  Block(String name, int initialTerm)
  {
    this.name = name;
    setTerm(initialTerm);
  }

  public String getName()
  {
    return name;
  }

  public boolean isEmpty()
  {
    return (data.length == 1 && data[0] == 0);
  }

  /**
   * Method inserts a term info the block.
   *
   * @param term value of the term
   */
  void setTerm(int term)
  {
    if(term > 0)
    {
      if(term < minTerm) minTerm = term;
      if(term > maxTerm) maxTerm = term;

      term--;

      if(data.length < ((term / 32) + 1)) data = Arrays.copyOf(data, (term / 32) + 1);
      data[term / 32] |= (1 << (term % 32));

      rank++;
    }
  }

  /**
   * Method checks whether the block contains given term. Used internally in blankets.
   *
   * @param term value of the term
   * @return true, if term is present in the block
   */
  boolean containsTerm(int term)
  {
    if(term < minTerm) return false;
    if(term > maxTerm) return false;

    term--;

    return ((data[term / 32] & (1 << (term % 32))) != 0);
  }

  /**
   * Method copies other block.
   *
   * @param other the block which might be copied
   * @return copied block
   */
  public static Block copyBlock(Block other)
  {
    return copyBlock(other.name, other);
  }

  /**
   * Method copies other block.
   *
   * @param name new name of block
   * @param other the block which might be copied
   * @return copied block
   */
  public static Block copyBlock(String name, Block other)
  {
    Block resultBlock = new Block(name);
    resultBlock.data = new int[other.data.length];
    resultBlock.rank = other.rank;
    resultBlock.minTerm = other.minTerm;
    resultBlock.maxTerm = other.maxTerm;

    System.arraycopy(other.data, 0, resultBlock.data, 0, resultBlock.data.length);
    return resultBlock;
  }

  /**
   * Method sums this block (A) with the other one (B), resulting a new block (R). If blocks have its name,
   * the resulting block R will have its name set to A.name + B.name.
   *
   * @param other the block (B) which might be summed with this block (A)
   * @return R = A or B
   */
  public Block getBlockPlus(Block other)
  {
    Block resultBlock = new Block(!name.isEmpty() && !other.name.isEmpty() ? name + "+" + other.name : "");
    resultBlock.data = new int[data.length >= other.data.length ? data.length : other.data.length];

    if(data.length >= other.data.length)
    {
      for(int i = 0; i < data.length; i++)
        resultBlock.data[i] = data[i] | (i < other.data.length ? other.data[i] : 0);
    }
    else
    {
      for(int i = 0; i < other.data.length; i++)
        resultBlock.data[i] = other.data[i] | (i < data.length ? data[i] : 0);
    }

    resultBlock.setBlockRank();
    resultBlock.minTerm = minTerm > other.minTerm ? other.minTerm : minTerm;
    resultBlock.maxTerm = maxTerm < other.maxTerm ? other.maxTerm : maxTerm;

    return resultBlock;
  }

  /**
   * Method computes the common part of this block (A) and the other block (B), resulting a new block (R). If blocks have its name,
   * the resulting block R will have its name set to A.name + B.name.
   *
   * @param other the block (B), the second argument of the operation
   * @return common part of block A and block B
   */
  public Block getBlockMul(Block other)
  {
    int size = data.length < other.data.length ? data.length : other.data.length;
    Block resultBlock = new Block("");
    resultBlock.data = new int[size];

    for(int i = 0; i < size; i++)
      resultBlock.data[i] = data[i] & other.data[i];

    for(int i = size; i > 0; i--)
      if(resultBlock.data[i - 1] == 0) size--;
      else break;

    if(size > 0)
    {
      if(size < resultBlock.data.length) resultBlock.data = Arrays.copyOf(resultBlock.data, size);
      resultBlock.setMinTerm();
      resultBlock.setMaxTerm();
    }
    else
      resultBlock = new Block("");

    resultBlock.setBlockRank();

    return resultBlock;
  }

  /**
   * Method subtracts other block (B) from this block (A). Resulting block (R) does not have any terms from other block (B).
   *
   * @param other the block (B), the second argument of the operation
   * @return R = A - B
   */
  public Block getBlockMinus(Block other)
  {
    // Algorithm:
    // 1. Finding the end of shorter one of blocks,
    // 2. If there are more sub-blocks in the first block (A), they will be copied to resulting block. Next, resulting sub-blocks
    //    of the operation will be also stored (no reduction of empty sub-blocks will occur here).
    // 3. Starting from the end, sub-blocks of the other block (B) will be negated and the product with corresponding sub-block,
    //    of block A will be computed. If this block (A) had more sub-blocks than other block (B), or result of product is not equal
    //    to 0, thus next results of product  will be stored as a new sub-blocks in resulting block (R).
    //
    // Example:
    // 1,4,6,10 - 2,4,9,10  = 1,6
    //
    //               1001010001
    // A and (not B) 1010111100
    //               ----------
    //               1000010000 = 1,6
    //

    Block resultBlock = new Block("");
    resultBlock.data = new int[data.length];

    for(int i = 0, len = (data.length < other.data.length ? data.length : other.data.length); i < len; i++)
      resultBlock.data[i] = data[i] & (~other.data[i]);

    // Copying others sub-blocks (step 2).
    if(data.length - other.data.length >= 0)
      System.arraycopy(data, other.data.length, resultBlock.data, other.data.length, data.length - other.data.length);

    int size = resultBlock.data.length;
    for(int i = size; i > 0; i--)
      if(resultBlock.data[i - 1] == 0) size--;
      else break;

    if(size > 0)
    {
      if(size < resultBlock.data.length) resultBlock.data = Arrays.copyOf(resultBlock.data, size);
      resultBlock.setMinTerm();
      resultBlock.setMaxTerm();
    }
    else
      resultBlock = new Block("");

    resultBlock.setBlockRank();

    return resultBlock;
  }

  /**
   * Method checks whether blocks are equal.
   *
   * @param other block with which the comparison will be performed
   * @return true, if blocks is equal to "other" block
   */
  public boolean getBlockEq(Block other)
  {
    if(data.length != other.data.length) return false;
    for(int i = 0; i < data.length; i++)
      if(data[i] != other.data[i]) return false;

    return true;
  }


  /**
   * Method checks whether this block (A) is included in other block (B).
   *
   * @param other the block (B), the second argument of the operation
   * @return true, if relation A &lt;= B ois fulfilled.
   */
  public boolean getBlockLe(Block other)
  {
    // Algorithm:
    // 1. Starting from the beginning, up to the end of one of blocks, operation ((A and B) - A) is performed.
    //    If result of the operation is equal to 0, it means that relation A <= B is fulfilled.
    // 2. Process ends when all blocks were checked or then the operation result was greater than 0,
    //    what means that there is no relation A <= B.
    //
    // Examples:
    // A = 4,10     = 0001000001               A = 2,4,10   = 0101000001
    // B = 1,4,6,10 = 1001010001               B = 1,4,6,10 = 1001010001
    //
    //                0001000001                              0101000001
    //            and 1001010001                          and 1001010001
    //                ----------                              ----------
    //                0001000001                              0001000001
    //
    //      (A and B) 0001000001                    (A and B) 0001000001
    //           - A  0001000001                         - A  0101000001
    //                ----------                              ----------
    //                0000000000 A <= B                       0100000000 A > B
    //

    if(other.data.length < data.length) return false;

    for(int i = 0; i < data.length; i++)
      if((data[i] & other.data[i]) != data[i]) return false;

    return true;
  }

  /**
   * Method checks whether blocks have the common part.
   *
   * @param other the block for which the comparison will be performed
   * @return true, if blocks has common part
   */
  public boolean hasBlockCommonPart(Block other)
  {
    for(int i = 0, len = (data.length < other.data.length ? data.length : other.data.length); i < len; i++)
      if((data[i] & other.data[i]) != 0) return true;
    return false;
  }

  // Method sets the rank of the block, i.e. the number of terms stored in this block.
  private void setBlockRank()
  {
    rank = 0;
    for(int d : data)
    {
      if(d != 0)
      {
        int mask = 1;
        for(int i = 0; i < 32; i++)
        {
          if((d & mask) != 0) rank++;
          mask <<= 1;
        }
      }
    }
  }

  // Method sets the minimal term, stored in this block.
  private void setMinTerm()
  {
    int term = 1;
    for(int d : data)
    {
      if(d != 0)
      {
        int mask = 1;
        for(int i = 0; i < 32; i++)
        {
          if((d & mask) != 0)
          {
            minTerm = term + i;
            break;
          }

          mask <<= 1;
        }
      }

      term += 32;
    }
  }

  // Method sets the maximal term, stored in this block.
  private void setMaxTerm()
  {
    int term = data.length * 32;
    for(int i = data.length - 1; i >= 0; i--)
    {
      if(data[i] != 0)
      {
        int mask = 0x80000000;
        for(int j = 31; j >= 0; j--)
        {
          if((data[i] & mask) != 0)
          {
            maxTerm = term + 1 - (32 - j);
            break;
          }

          mask >>= 1;
        }
      }

      term -= 32;
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(!(o instanceof Block)) return false;
    Block block = (Block)o;
    return rank == block.rank &&
      minTerm == block.minTerm &&
      maxTerm == block.maxTerm &&
      Objects.equals(name, block.name) &&
      Arrays.equals(data, block.data);
  }

  @Override
  public int hashCode()
  {
    int result = Objects.hash(name, rank, minTerm, maxTerm);
    result = 31 * result + Arrays.hashCode(data);
    return result;
  }

  /**
   * Method prints (into string) this block.
   *
   * @param printName if true, the name of this block will be included in the result
   * @return string with the block name (optional) and the terms contained in this blocks
   */
  public String print(boolean printName)
  {
    StringBuilder termsStr = new StringBuilder();
    int term = 1;

    for(int d : data)
    {
      if(d != 0)
      {
        for(int i = 0; i < 32; i++)
          if((d & (1 << i)) != 0) termsStr.append(term + i).append(",");
      }

      term += 32;
    }

    if(termsStr.length() > 0) termsStr.deleteCharAt(termsStr.length() - 1).append(";");

    return (printName && name != null && !name.isEmpty() ? name + ":" : "") + (termsStr.length() > 0 ? termsStr.toString() : "<empty>;");
  }

  @Override
  public String toString()
  {
    return print(true);
  }
}
