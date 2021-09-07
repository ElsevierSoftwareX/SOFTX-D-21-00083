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

package decolib.loaders;

import decolib.signals.Signal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static decolib.signals.SignalType.INPUT;
import static decolib.signals.SignalType.OUTPUT;

/**
 * Provides method for loading functions defined through tables of values.
 */
public class DataLoaders
{
  private static Pattern getLinePattern = Pattern.compile("(.*?)([\\r\\n]+|$)");
  private static Pattern getSignalNamePattern = Pattern.compile("[ \\t]?([0-9a-zA-Z]+)([ \\t]+|(?=\\|)|$)([|])?");
  private static Pattern getSignalValuePattern = Pattern.compile("[ \\t]?([-]|[01]|[a-zA-Z20-9]+[0-9]?)([ \\t]+|(?=\\|)|$)([|])?");

  /**
   * Method load a function's table from a text file.
   * The file should have format as follows:
   *   x1 x2 x3 ... | y1 y2 ...
   *   -  0  A1 ... | -  1
   *   0  0  1  ... | 1  1
   *   ...
   *
   *   Remark: multiple-valued values should start with a letter.
   *
   * @param path a path to the file containing a function's table
   * @param charset a charset used to encoding of a function file
   * @return signals for a function
   * @throws IOException when there is file operation issues
   */
  public static List<Signal> load(String path, Charset charset) throws IOException
  {
    return load(new String(Files.readAllBytes(Paths.get(path)), charset == null ? StandardCharsets.UTF_8 : charset));
  }

  /**
   * Method load a function's table from a string.
   * The string should have format as follows:
   *   x1 x2 x3 ... | y1 y2 ...\n
   *   -  0  A1 ... | -  1\n
   *   0  0  1  ... | 1  1\n
   *   ...
   *
   *   Remark: multiple-valued values should start with a letter.
   *
   * @param table a logical table for a function (each line should end with a newline)
   * @return signals for a function
   */
  public static List<Signal> load(String table)
  {
    List<Signal> signals = new ArrayList<>();

    if(!table.isEmpty())
    {
      Matcher matcher = getLinePattern.matcher(table);
      int processingPos = 0;

      boolean processingSignalNames = true;

      while(processingPos < table.length() && matcher.find(processingPos))
      {
        String line = matcher.group(1).trim();
        if(!line.isEmpty())
        {
          if(processingSignalNames)
          {
            // Loading of signals names.
            Matcher lineMatcher = getSignalNamePattern.matcher(line);
            int lineProcessingPos = 0;
            boolean inputs = true;

            while(lineMatcher.find(lineProcessingPos))
            {
              if(lineMatcher.group(1) != null && !lineMatcher.group(1).trim().isEmpty())
                signals.add(new Signal(inputs ? INPUT : OUTPUT, lineMatcher.group(1)));

              if(lineMatcher.group(3) != null) inputs = false;
              lineProcessingPos = lineMatcher.end();
            }

            processingSignalNames = false;
          }
          else
          {
            // Loading of signals values.
            Matcher lineMatcher = getSignalValuePattern.matcher(line);
            int lineProcessingPos = 0;
            int signalNum = 0;

            while(lineMatcher.find(lineProcessingPos))
            {
              if(lineMatcher.group(1) != null && !lineMatcher.group(1).trim().isEmpty())
              {
                signals.get(signalNum).addValue(lineMatcher.group(1));
                signalNum++;
              }

              lineProcessingPos = lineMatcher.end();
            }
          }
        }

        processingPos = matcher.end();
      }

      // Computing blankets for signals.
      if(signals.size() > 0)
        for(Signal signal : signals) signal.update();
    }

    return signals;
  }
}
