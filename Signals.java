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

package decolib.signals;

import decolib.blankets.Blanket;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods for handling signals.
 */
public class Signals
{
  // Processing of signals, transforming into blankets:
  // - for binary signals, which have no don't care values - the code of term will correspond to the calculated value.
  // - for binary signals, which have don't care values - it is required to check whether the signal is multiple-valued one.
  //
  //  Example: for the following function blanket for a group should be created.
  //    x1 x2 x3
  //    a  1  1
  //    -  1  -   -> terms should be converted as follows: -_1_- => a_1_-  b_1_-  d_1_- => a_1_0  a_1_1  b_1_0  b_1_1  d_1_0  d_1_1
  //    b  -  1                                            b_-_1 => b_1_1  b_2_1
  //    d  2  0
  //
  //  1. In the example the first tem is fully specified.
  //  2. The second term has two "don't case" values, so it may appear in more than one block:
  //     - Values "don't care" causing that the term is compatible with term 1 (value a_1_1 appears in both terms), so:
  //       B1: 1,2 <= (a  1  1), (-  1  -)
  //       At the same time it means that term 1 is included in the group of term 2, so term 1 will not be required anymore.
  //     - This term may be also joined with term 3 (value b_1_1 appears in both terms), so a new block should be created:
  //       B2: 2,3 <= (-  1  -), (b  -  1)
  //  3. The third term:
  //     - It may be only joined with term 2, what was performed in step 2 (the value b_1_1 was used).
  //  4. The forth term:
  //     - It cannot be joined with any of terms, so a new block in blanket will be created for it:
  //       B5: 4;
  //
  //  Verification:
  //    βx1 = Blanket{blocks=[B(a):1,2;, B(b):2,3;, B(d):2,4;]}
  //    βx2 = Blanket{blocks=[B(1):1,2,3;, B(2):3,4;]}
  //    βx3 = Blanket{blocks=[0:2,4;, 1:1,2,3;]}
  //    βx1x2x3 = Blanket{blocks=[B3:4;, B4:1,2;, B5:2,3;]}
  //    Blanket βx1x2x3 confirms the correctness of the procedure from the example.
  //
  //  Algorithm:
  //  1. Finding conversions for terms containing "don't care" values,
  //  2. matching terms and conversions, in order to joining them into groups. Used elements should be removed from the process.
  //  3. For remaining elements (terms and conversions) a new blocks should be created.

  /**
   * Method returns groups of signals, separating inputs and outputs.
   *
   * @param signals a list of signals
   * @return separated inputs and output signals
   */
  public static List<Signal> getSignalGroups(List<Signal> signals)
  {
    List<Signal> result = getSignalGroups(signals, SignalType.INPUT, new int[0]);
    result.addAll(getSignalGroups(signals, SignalType.OUTPUT, new int[0]));

    return result;
  }

  /**
   * Method returns groups of signals for given type: inputs or outputs. There is possible to select signals by name.
   *
   * @param signals a list of signals
   * @param signalType a type of signal
   * @param signalNames list of signals names
   * @return separated inputs and output signals
   */
  public static List<Signal> getSignalGroups(List<Signal> signals, SignalType signalType, String ... signalNames)
  {
    int[] signalNumbers = new int[signalNames.length];

    for(int i = 0; i < signalNames.length; i++)
      for(int j = 0; j < signals.size(); j++)
        if(signalNames[i].equals(signals.get(j).getName()))
        {
          signalNumbers[i] = j;
          break;
        }

    return getSignalGroups(signals, signalType, signalNumbers);
  }

  private static Pattern valuePattern = Pattern.compile("(.*?)([,;])");
  private static Pattern termPattern = Pattern.compile(".*?;");

  /**
   * Method returns groups of signals for given type: inputs or outputs. There is possible to select signals by ordinal numbers.
   *
   * @param signals a list of signals
   * @param signalType a type of signal
   * @param signalNumbers list of signals ordinal numbers
   * @return separated inputs and output signals
   */
  public static List<Signal> getSignalGroups(List<Signal> signals, SignalType signalType, int ... signalNumbers)
  {
    Map<Integer,String> terms = new HashMap<>();

    // Gets signals of given type.
    List<Signal> signalsInternal = new ArrayList<>();
    for(Signal signal : signals) if(signal.getType() == signalType) signalsInternal.add(signal);

    for(int i = 0; i < signals.get(0).getValues().size(); i++)
    {
      String groupTerm;
      StringBuilder groupTermBuilder = new StringBuilder();

      for(int j = 0; j < (signalNumbers.length > 0 ? signalNumbers.length : signalsInternal.size()); j++)
      {
        Signal signal = signalsInternal.get((signalNumbers.length > 0 ? signalNumbers[j] : j));

        if(signal.getType() == signalType)
        {
          groupTermBuilder.append(signal.getValues().get(i)).append(",");
        }
        else
        {
          if(signalNumbers.length > 0)
            throw new IllegalArgumentException(String.format("There is no %s with index %s", signalType.name(), i));
        }
      }
      groupTerm = groupTermBuilder.replace(groupTermBuilder.length() - 1, groupTermBuilder.length(), ";").toString();

      String groupTermExplication;

      if(!groupTerm.contains("-"))
      {
        // Typically terms do not contain "don't care" values. Thus, because of CPU performance, this be the first block of if statement.
        groupTermExplication = groupTerm;
      }
      else
      {
        List<String> partialExplication = new ArrayList<>(Collections.singletonList(groupTerm));
        boolean continueExplicationProcess = true;

        while(continueExplicationProcess)
        {
          List<String> partialExplicationResult = new ArrayList<>();

          for(String partialExplicationItem : partialExplication)
          {
            Matcher matcher = valuePattern.matcher(partialExplicationItem);
            int valuePos = 0;

            while(matcher.find())
            {
              if(matcher.group(1).equals("-"))
              {
                for(String value : signalsInternal.get((signalNumbers.length > 0 ? signalNumbers[valuePos] : valuePos)).getValueNames())
                  partialExplicationResult.add(partialExplicationItem.substring(0, matcher.start(1)) + value + partialExplicationItem.substring(matcher.end(1)));

                break;
              }

              valuePos++;
            }

            continueExplicationProcess = partialExplicationResult.get(0).contains("-");
          }

          partialExplication = new ArrayList<>(partialExplicationResult);
        }

        StringBuilder groupTermExplicationBuilder = new StringBuilder();
        for(String item : partialExplication) groupTermExplicationBuilder.append(item);

        groupTermExplication = groupTermExplicationBuilder.toString();
      }

      terms.put(i + 1, groupTermExplication);
    }

    if(terms.size() > 0)
    {
      // Prepare result signal.
      StringBuilder groupName = new StringBuilder();
      for(int i = 0; i < (signalNumbers.length > 0 ? signalNumbers.length : signalsInternal.size()); i++)
        groupName.append(signalsInternal.get((signalNumbers.length > 0 ? signalNumbers[i] : i)).getName());

      Signal resultSignal = new Signal(signalType == SignalType.INPUT ? SignalType.INPUT_GROUP : SignalType.OUTPUT_GROUP, groupName.toString());
      Blanket blanket = Blanket.empty();

      // Post-processing for terms.
      for(int i = 1; i <= terms.size(); i++) blanket.addBlock("B" + i);

      for(int i = 1; i <= terms.size(); i++)
      {
        Matcher matcher = termPattern.matcher(terms.get(i));
        while(matcher.find())
        {
          for(int j = i + 1; j <= terms.size(); j++)
            if(terms.get(j).contains(matcher.group()))
            {
              blanket.addTerms("B" + j, i);
              terms.put(i, terms.get(i).replace(matcher.group(), ""));
            }
        }
      }

      for(int i = 1; i <= terms.size(); i++)
        if(!terms.get(i).isEmpty())
        {
          blanket.addTerms("B" + i, i);
          resultSignal.addValue(terms.get(i));
        }
        else
          blanket.deleteBlock("B" + i);

      blanket.renameBlock("B");

      resultSignal.setBlanket(blanket);

      return Collections.singletonList(resultSignal);
    }

    return new ArrayList<>();
  }
}
