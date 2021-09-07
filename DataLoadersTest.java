package decolib.loaders;

import decolib.signals.Signal;
import org.fest.assertions.Assertions;
import org.junit.Test;

import java.util.List;

import static decolib.signals.SignalType.INPUT;
import static decolib.signals.SignalType.OUTPUT;
import static decolib.signals.SignalValueType.BINARY;
import static decolib.signals.SignalValueType.MULTIPLE_VALUED;

public class DataLoadersTest
{
  @Test
  public void test()
  {
    List<Signal> signals;

    String tabA1 =
      "x|y\n" +
      "1|0\n" +
      "0|1\n";
    signals = DataLoaders.load(tabA1);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(2);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 0]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2; 1:1;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[0, 1]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:1; 1:2;]}");

    String tabA2 =
      "x |y\n" +
      "1|0\n" +
      "0|1\r\n\n\n\n";
    signals = DataLoaders.load(tabA2);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(2);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 0]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2; 1:1;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[0, 1]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:1; 1:2;]}");

    String tabA3 =
      "x| y \n" +
      "1|0\r\n" +
      "0|1";
    signals = DataLoaders.load(tabA3);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(2);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 0]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2; 1:1;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[0, 1]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:1; 1:2;]}");

    String tabA4 =
      " x|y  \n" +
      "1|  0\n" +
      "\n" +
      "0|1\n";
    signals = DataLoaders.load(tabA4);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(2);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 0]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2; 1:1;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[0, 1]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:1; 1:2;]}");

    String tabA5 =
      "x | y\n" +
      "1   |0\n" +
      "\r\n" +
      "0|1\n" +
      "\r\n";
    signals = DataLoaders.load(tabA5);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(2);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 0]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2; 1:1;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[0, 1]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:1; 1:2;]}");

    String tabA6 =
      "x | y z\n" +
      "1   |0 A3\n" +
      "\r\n" +
      "0|1 A\n" +
      "\r\n";
    signals = DataLoaders.load(tabA6);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(3);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 0]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2; 1:1;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[0, 1]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:1; 1:2;]}");
    Assertions.assertThat(signals.get(2).getName()).isEqualTo("z");
    Assertions.assertThat(signals.get(2).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(2).getSignalValueType()).isEqualTo(MULTIPLE_VALUED);
    Assertions.assertThat(signals.get(2).getValues().toString()).isEqualTo("[A3, A]");
    Assertions.assertThat(signals.get(2).getBlanket().toString()).isEqualTo("Blanket{blocks=[B(A3):1; B(A):2;]}");

    String tabA7 =
      "x1 x2 x3 | y z\n" +
      "1  1  1  | A 1\n" +
      " 1 1  0  | B -         \r\n\n\n" +
      "0  2    1  | c      -\r\n" +
      "-  -  -  | d -\r\n" +
      " - 3      -  |   E -\r\r" +
      " - 4      -  | e 0\r\n" +
      "\r\n";
    signals = DataLoaders.load(tabA7);
    Assertions.assertThat(signals).isNotNull();
    Assertions.assertThat(signals.size()).isEqualTo(5);
    Assertions.assertThat(signals.get(0).getName()).isEqualTo("x1");
    Assertions.assertThat(signals.get(0).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(0).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(0).getValues().toString()).isEqualTo("[1, 1, 0, -, -, -]");
    Assertions.assertThat(signals.get(0).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:3,4,5,6; 1:1,2,4,5,6;]}");
    Assertions.assertThat(signals.get(1).getName()).isEqualTo("x2");
    Assertions.assertThat(signals.get(1).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(1).getSignalValueType()).isEqualTo(MULTIPLE_VALUED);
    Assertions.assertThat(signals.get(1).getValues().toString()).isEqualTo("[1, 1, 2, -, 3, 4]");
    Assertions.assertThat(signals.get(1).getBlanket().toString()).isEqualTo("Blanket{blocks=[B(1):1,2,4; B(2):3,4; B(3):4,5; B(4):4,6;]}");
    Assertions.assertThat(signals.get(2).getName()).isEqualTo("x3");
    Assertions.assertThat(signals.get(2).getType()).isEqualTo(INPUT);
    Assertions.assertThat(signals.get(2).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(2).getValues().toString()).isEqualTo("[1, 0, 1, -, -, -]");
    Assertions.assertThat(signals.get(2).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2,4,5,6; 1:1,3,4,5,6;]}");
    Assertions.assertThat(signals.get(3).getName()).isEqualTo("y");
    Assertions.assertThat(signals.get(3).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(3).getSignalValueType()).isEqualTo(MULTIPLE_VALUED);
    Assertions.assertThat(signals.get(3).getValues().toString()).isEqualTo("[A, B, c, d, E, e]");
    Assertions.assertThat(signals.get(3).getBlanket().toString()).isEqualTo("Blanket{blocks=[B(A):1; B(B):2; B(c):3; B(d):4; B(E):5; B(e):6;]}");
    Assertions.assertThat(signals.get(4).getName()).isEqualTo("z");
    Assertions.assertThat(signals.get(4).getType()).isEqualTo(OUTPUT);
    Assertions.assertThat(signals.get(4).getSignalValueType()).isEqualTo(BINARY);
    Assertions.assertThat(signals.get(4).getValues().toString()).isEqualTo("[1, -, -, -, -, 0]");
    Assertions.assertThat(signals.get(4).getBlanket().toString()).isEqualTo("Blanket{blocks=[0:2,3,4,5,6; 1:1,2,3,4,5;]}");
  }
}
