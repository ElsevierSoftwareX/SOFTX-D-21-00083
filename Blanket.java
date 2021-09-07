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

import decolib.decompositions.DecompositionsTools;
import decolib.signals.Signal;
import decolib.signals.SignalType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a blanket. Provides methods allowing for performing basic operation on blankets.
 */
public class Blanket
{
  private List<Block> blocks = new ArrayList<>();

  public static Blanket empty()
  {
    return new Blanket();
  }

  private static Pattern blanketBlockPattern = Pattern.compile("(([^;]+?):)?((\\s*\\d+\\s*,?)+);\\s*");
  private static Pattern blanketBlockTermsPattern = Pattern.compile("\\s*(\\d+)\\s*,?");

  /**
   * Method creates a blanket from the string description.
   *
   * @param str a blanket description, like: "A:1,2,3; B:1,4; ...", "1,2,3; B:1,4; ..." or "1,2,3; 1,4; ..."
   * @return a blanket
   */
  public static Blanket create(String str)
  {
    if(str != null && !str.trim().isEmpty())
    {
      Blanket result = Blanket.empty();

      Matcher blanketBlockMatcher = blanketBlockPattern.matcher(str.trim());
      while(blanketBlockMatcher.find())
      {
        Block block = new Block(blanketBlockMatcher.group(2) != null ? blanketBlockMatcher.group(2).trim() : "");
        if(blanketBlockMatcher.group(3) != null)
        {
          Matcher blanketBlockTermsMatcher = blanketBlockTermsPattern.matcher(blanketBlockMatcher.group(3));
          while(blanketBlockTermsMatcher.find())
            block.setTerm(Integer.parseInt(blanketBlockTermsMatcher.group(1)));

          if(block.rank > 0) result.addBlock(block);
        }
      }

      if(result.blocks.size() > 0)
      {
        result.sortBlocks(false);
        return result;
      }
    }

    return null;
  }

  /**
   * Method (version 1) creates a blanket with empty blocks named as in given list.
   *
   * @param blockNames names of blocks of the blanket
   * @return blanket or null
   */
  public static Blanket create(String ... blockNames)
  {
    return create(Arrays.asList(blockNames));
  }

  /**
   * Method (version 2) creates a blanket with empty blocks named as in given list.
   *
   * @param blockNames names of blocks of the blanket
   * @return blanket or null
   */
  public static Blanket create(List<String> blockNames)
  {
    if(blockNames != null && !blockNames.isEmpty())
    {
      Blanket blanket = Blanket.empty();
      for(String blockName : blockNames)
        blanket.blocks.add(new Block(blockName));

      return blanket;
    }

    return null;
  }

  /**
   * Method creates a blanket containing given block.
   *
   * @param block a block which may be stored in the blanket
   * @return blanket or null
   */
  public static Blanket create(Block block)
  {
    Blanket blanket = Blanket.empty();
    blanket.blocks.add(Block.copyBlock(block.name, block));
    return blanket;
  }

  /**
   * Method copies other blanket.
   *
   * @param other a blanket which should be copied
   * @return blanket
   */
  public static Blanket copy(Blanket other)
  {
    Blanket blanket = Blanket.empty();
    for(Block block : other.blocks) blanket.addBlock(block);
    blanket.sortBlocks(false);

    return blanket;
  }

  /**
   * Method sets names for blocks of blanket.
   *
   * @param prefix a block name prefix, like "A", "B" ...
   * @param start a starting number of block, incremented for each block, concatenated with prefix
   */
  public void setBlockNames(String prefix, int start)
  {
    if(prefix != null && !prefix.trim().isEmpty() && start >= 0 && start < Integer.MAX_VALUE - blocks.size())
    {
      prefix = prefix.trim();

      for(Block block : blocks)
        block.name = prefix + start++;
    }
  }

  /**
   * Method (version 1) adds terms to this blanket.
   *
   * @param blockName a name of block to which the terms should be added
   * @param terms a list of terms
   */
  public void addTerms(String blockName, List<Integer> terms)
  {
    for(Block block : blocks)
    {
      if(block.name.equals(blockName))
      {
        if(terms != null && !terms.isEmpty())
        {
          for(int term : terms)
            block.setTerm(term);
        }
      }
    }
  }

  /**
   * Method (version 2) adds terms to this blanket.
   *
   * @param blockName a name of block to which the terms should be added
   * @param terms a list of terms
   */
  public void addTerms(String blockName, Integer ... terms)
  {
    addTerms(blockName, Arrays.asList(terms));
  }

  /**
   * Method returns list of blocks, owned by the blanket.
   *
   * @return list of blocks of the blanket
   */
  public List<Block> getBlocks()
  {
    return blocks;
  }

  /**
   * Method returns a number of blocks, owned by the blanket.
   *
   * @return number of blocks contained in the blanket
   */
  public int getBlocksCount()
  {
    return blocks.size();
  }

  /**
   * Method returns a block with a given name, owned by the blanket.
   *
   * @param blockName a name of block
   * @return block or null
   */
  public Block getBlock(String blockName)
  {
    if(blockName != null && !blockName.isEmpty())
    {
      for(Block block : blocks)
        if(blockName.equals(block.name)) return block;
    }

    return null;
  }

  /**
   * Method creates an empty block and adds it to the blanket.
   *
   * @param blockName a name of block
   */
  public void addBlock(String blockName)
  {
    blocks.add(new Block(blockName));
  }

  /**
   * Method creates a copy of given block and adds it to the blanket.
   *
   * @param other a block which should be copied
   */
  public void addBlock(Block other)
  {
    if(other != null)
    {
      blocks.add(Block.copyBlock(other.name, other));
      sortBlocks(false);
    }
  }

  /**
   * Method creates a copy of given block and adds it to the blanket. Next the sorting, simplifying further synthesis, may be applied.
   *
   * @param other            a block which should be copied
   * @param sortForSynthesis if true, the sorting of blocks will be applies
   */
  public void addBlock(Block other, boolean sortForSynthesis)
  {
    if(other != null)
    {
      blocks.add(Block.copyBlock(other.name, other));
      if(sortForSynthesis) sortBlocks(true);
    }
  }

  /**
   * Method deletes given block from the blanket.
   *
   * @param blockName a block which should be deleted
   */
  public void deleteBlock(String blockName)
  {
    for(Block block : blocks)
      if(block.name.equals(blockName))
      {
        blocks.remove(block);
        break;
      }

    sortBlocks(false);
  }

  /**
   * Method renames blocks of the blanket, using given name prefix.
   *
   * @param namePrefix a prefix name
   */
  public void renameBlock(String namePrefix)
  {
    for(int i = 0; i < blocks.size(); i++) blocks.get(i).name = namePrefix + (i + 1);
  }

  /**
   * Method sorts blocks of the blanket
   *
   * @param sortForSynthesis if true, there will be applied sorting that may simplifying the synthesis process,
   *                         otherwise the blocks are sorted with regard to minimal value of terms
   * @return a blanket, containing sorted blocks
   */
  public Blanket sortBlocks(boolean sortForSynthesis)
  {
    if(sortForSynthesis)
      blocks.sort((b1, b2) -> b1.rank == b2.rank ? b1.minTerm - b2.minTerm : b1.rank - b2.rank);
    else
      blocks.sort(Comparator.comparingInt(b -> b.minTerm));

    return this;
  }

  // Method removes redundant blocks. Caution: method changes sorting order for data.
  private void packBlocks(List<Block> blocks)
  {
    if(blocks.size() > 0)
    {
      blocks.sort((b1, b2) -> b1.rank == b2.rank ? b1.minTerm - b2.minTerm : b1.rank - b2.rank);

      process: for(int i = 0; i < blocks.size() - 1; i++)
      {
        Block b = blocks.get(i);
        for(int j = i + 1; j < blocks.size(); j++)
        {
          if(b.getBlockLe(blocks.get(j)))
          {
            blocks.set(i, null);
            continue process;
          }
        }
      }
      blocks.removeAll(Collections.singleton(null));
    }
  }

  /**
   * Method computes the product of this blanket (A) and the other blanket (B).
   *
   * @param other the blanket (B), the second argument of the operation
   * @return R = A x B
   */
  public Blanket BxB(Blanket other)
  {
    return BxB(other, true);
  }

  /**
   * Method computes the product of this blanket (A) and the other blanket (B). There is also possible to sort blocks in resulting blanket.
   *
   * @param other the blanket (B), the second argument of the operation
   * @param sort is true, the blocks of resulting blanket will be sorted (with regard to minimal value of terms)
   * @return R = A x B
   */
  public Blanket BxB(Blanket other, boolean sort)
  {
    List<Block> resultBlocks = new ArrayList<>();
    for(Block otherBlock : other.blocks)
    {
      for(Block block : blocks)
      {
        Block b = block.getBlockMul(otherBlock);
        if(!b.isEmpty() && !resultBlocks.contains(b)) resultBlocks.add(b);
      }
    }

    if(resultBlocks.size() > 0)
    {
      packBlocks(resultBlocks);

      for(int i = 0; i < resultBlocks.size(); i++) resultBlocks.get(i).name = "B" + (i + 1);

      Blanket resultBlanket = new Blanket();
      resultBlanket.blocks = resultBlocks;

      if(sort) resultBlanket.sortBlocks(false);

      return resultBlanket;
    }

    return null;
  }

  /**
   * Method finds blocks of other blanket, which are incompatible with this blanket.
   *
   * @param other the blanket, the second argument of the operation
   * @return list of blocks that are incompatible with this blanket (result may be an empty array)
   */
  public List<Block> BicB(Blanket other)
  {
    List<Block> result = new ArrayList<>();

    for(Block otherBlock : other.blocks)
      if(!Blanket.create(otherBlock).BleB(this)) result.add(Block.copyBlock(otherBlock));

    return result;
  }

  /**
   * Method checks whether this blanket (A) is in relation "&lt;=" with the other blanket (B).
   *
   * @param other the blanket, the second argument of the operation
   * @return true, if relation A &lt;= B is fulfilled
   */
  public boolean BleB(Blanket other)
  {
    int checked = 0;
    nextBlock: for(Block block : blocks)
    {
      nextOtherBlock: for(Block otherBlock : other.blocks)
      {
        if(block.data.length > otherBlock.data.length) continue;
        for(int i = 0; i < block.data.length; i++)
          if((block.data[i] & otherBlock.data[i]) != block.data[i]) continue nextOtherBlock;

        checked++;
        continue nextBlock;
      }
    }

    return checked == blocks.size();  // The value "checked" is incremented each time, when a block from the blanket A is included in any of blocks from blanket B.
  }

  /**
   * Method checks whether this blankets are equal (have exactly the same blocks, the names of blocks are not checked).
   *
   * @param other the blanket, the second argument of the operation
   * @return true, if blankets are equal
   */
  public boolean BeqB(Blanket other)
  {
    if(blocks.size() != other.blocks.size()) return false;

    for(Block block : blocks)
    {
      boolean found = false;
      for(Block otherBlock : other.blocks)
        if(block.getBlockEq(otherBlock)) found = true;

      if(!found) return false;
    }

    return true;
  }

  /**
   * Method checks whether blocks of this blanket have common part (i.e. contain at least one the same term).
   *
   * @return true, if blocks of blanket have common parts
   */
  public boolean haveBlocksCommonPart()
  {
    if(blocks.size() > 0)
    {
      for(int i = 0; i < blocks.size(); i++)
      {
        for(int j = i + 1; j < blocks.size(); j++)
          if(blocks.get(i).hasBlockCommonPart(blocks.get(j))) return true;
      }
    }

    return false;
  }

  /**
   * Method finds terms which are common to blocks from this blanket. Corresponding terms are stored in one block.
   *
   * @return block, containing terms common to blocks
   */
  public Block getBlocksCommonPart()
  {
    if(blocks.size() > 0)
    {
      int minTerm = Integer.MAX_VALUE;
      int maxTerm = 0;
      for(Block block : blocks)
      {
        if(minTerm > block.minTerm) minTerm = block.minTerm;
        if(maxTerm < block.maxTerm) maxTerm = block.maxTerm;
      }

      Block result = new Block("");
      for(int t = minTerm; t <= maxTerm; t++)
      {
        int cnt = 0;
        for(Block block : blocks)
          if(block.containsTerm(t) && ++cnt > 1)
          {
            result.setTerm(t);
            break;
          }
      }

      return result;
    }

    return null;
  }

  /**
   * Method encodes this blanket as a list of binary Signals. At leas one signal will be stored in the resulting list.
   *
   * @return list of binary Signals
   */
  public List<Signal> toSignal()
  {
    int bits = DecompositionsTools.encodingBitsCount(blocks.size());

    int maxTerm = -1;
    char[][] blockCodes = new char[blocks.size()][];
    for(int i = 0; i < blocks.size(); i++)
    {
      if(maxTerm < blocks.get(i).maxTerm) maxTerm = blocks.get(i).maxTerm;
      blockCodes[i] = DecompositionsTools.leftPad(Integer.toString(i, 2), bits, '0').toCharArray();
    }

    List<Signal> signals = new ArrayList<>();
    for(int i = 0; i < bits; i++) signals.add(new Signal(SignalType.GENERATED, "b" + i));

    for(int term = 1; term <= maxTerm; term++)
    {
      char[] termValue = null;
      for(int i = 0; i < blockCodes.length; i++)
      {
        if(blocks.get(i).containsTerm(term))
        {
          if(termValue == null)
            termValue = blockCodes[i].clone();
          else
          {
            for(int j = 0; j < bits; j++)
              if(termValue[j] != blockCodes[i][j]) termValue[j] = '-';
          }
        }
      }

      if(termValue != null)
        for(int i = 0; i < bits; i++)
          signals.get(i).addValue(String.valueOf(termValue[i]));
    }

    for(int i = 0; i < bits; i++)
      signals.get(i).update();

    return signals;
  }

  /**
   * Method prints (into string) this blanket, discarding names of blocks.
   *
   * @return string representing a blanket, containing string representation of blocks, separated by semicolon
   */
  public String print()
  {
    StringBuilder blocksStrBuilder = new StringBuilder();
    for(Block block : blocks) blocksStrBuilder.append(block.print(false)).append(' ');
    if(blocksStrBuilder.length() > 0) blocksStrBuilder.deleteCharAt(blocksStrBuilder.length() - 1);

    return blocksStrBuilder.toString();
  }

  @Override
  public String toString()
  {
    StringBuilder blocksStrBuilder = new StringBuilder();
    for(Block block : blocks) blocksStrBuilder.append(block).append(' ');
    if(blocksStrBuilder.length() > 0) blocksStrBuilder.deleteCharAt(blocksStrBuilder.length() - 1);

    return "Blanket{" +
      "blocks=[" + blocksStrBuilder +
      "]}";
  }
}
