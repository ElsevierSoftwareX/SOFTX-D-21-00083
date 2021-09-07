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

/**
 * Contains tools methods.
 */
public class DecompositionsTools
{
  /**
   * Method returns number of bits, required for binary encoding of given value.
   *
   * @param val value for which the number of bits required for its binary encoding should be computed
   * @return number of bits
   */
  public static int encodingBitsCount(int val)
  {
    if(val > 0)
    {
      if(val > 1)
      {
        double l = Math.log(val) / Math.log(2);

        if(l - Math.floor(l) > 0) return (int)Math.floor(l) + 1;

        return (int)Math.floor(l);
      }

      return 1;
    }

    return 0;
  }

  /**
   * Method adding given character to left side of the string.
   *
   * @param str   a string that will be padded
   * @param size  a string size limit, including padded characters
   * @param fill  a character used for padding
   * @return padded string
   */
  public static String leftPad(String str, int size, char fill)
  {
    StringBuilder builder = new StringBuilder(str);
    while(builder.length() < size)  builder.insert(0, fill);

    return builder.toString();
  }
}
