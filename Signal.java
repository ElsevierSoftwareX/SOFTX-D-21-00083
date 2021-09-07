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

import static decolib.signals.SignalValueType.BINARY;
import static decolib.signals.SignalValueType.MULTIPLE_VALUED;

/**
 * Represents a signal, binary or multiple-valued one.
 */
public class Signal
{
  private SignalType type;
  private String name;

  private SignalValueType signalValueType = BINARY;
  private List<String> values;
  private Set<String> valueNames;
  private Blanket blanket;

  /**
   * @param signalType a type of signal
   * @param signalName a name of signal
   */
  public Signal(SignalType signalType, String signalName)
  {
    type = signalType;
    name = signalName;
    values = new ArrayList<>();
    blanket = null;
  }

  public SignalType getType()
  {
    return type;
  }

  public String getName()
  {
    return name;
  }

  public SignalValueType getSignalValueType()
  {
    return signalValueType;
  }

  public List<String> getValues()
  {
    return values;
  }

  public Set<String> getValueNames()
  {
    return valueNames;
  }

  public Blanket getBlanket()
  {
    return blanket;
  }

  void setBlanket(Blanket blanket)
  {
    this.blanket = blanket;
  }

  /**
   * Adds a new value to the signal. The value may be binary or multiple-valued.
   * After adding values, method "update()" should be called.
   *
   * @param value a value of the signal
   */
  public void addValue(String value)
  {
    value = value.trim();
    if(!value.isEmpty())
    {
      if(signalValueType == BINARY && (value.length() > 1 || (value.charAt(0) != '-' && value.charAt(0) != '0' && value.charAt(0) != '1'))) signalValueType = MULTIPLE_VALUED;
      values.add(value);
    }
  }

  /**
   * Updates internal data of this signal, should be called after adding one or more values to the signal.
   */
  public void update()
  {
    valueNames = (signalValueType == BINARY ? new HashSet<>(values) : new LinkedHashSet<>(values));  // For multiple-valued signal, the order of values name will be kept.
    valueNames.remove("-");                                                                          // For binary signal, the first value is a zero - influences on presentation only.

    if(signalValueType == BINARY)
    {
      blanket = Blanket.create("0", "1");
      for(int i = 0; i < values.size(); i++)
      {
        if(values.get(i).charAt(0) == '0' || values.get(i).charAt(0) == '-') blanket.addTerms("0", i + 1);
        if(values.get(i).charAt(0) == '1' || values.get(i).charAt(0) == '-') blanket.addTerms("1", i + 1);
      }
    }

    if(signalValueType == MULTIPLE_VALUED)
    {
      List<String> valueList = new ArrayList<>(valueNames);

      List<String> blockNames = new ArrayList<>();
      for(String value : valueList) blockNames.add("B(" + value + ")");

      blanket = Blanket.create(blockNames);

      for(int i = 0; i < values.size(); i++)
      {
        String value = values.get(i);
        if(value.length() > 1 || value.charAt(0) != '-')
        {
          // Multiple-values.
          blanket.addTerms("B(" + value + ")", i + 1);
        }
        else
        {
          // Don't care value (-).
          for(String valueListItem : valueList)
            blanket.addTerms("B(" + valueListItem + ")", i + 1);
        }
       }
    }
  }

  @Override
  public String toString()
  {
    return "Signal{" +
      "type=" + type +
      ", name='" + name + '\'' +
      ", signalValueType=" + signalValueType +
      ", values=" + values +
      ", valueNames=" + valueNames +
      ", blanket=" + blanket +
      '}';
  }
}
