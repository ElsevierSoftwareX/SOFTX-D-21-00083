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

package decolib.decompositions.results;

import decolib.blankets.Blanket;
import decolib.decompositions.DecompositionType;

/**
 * A container for result for functional decomposition, when blankets are used for representing the result.
 */
public class DecompositionBlankets
{
  public DecompositionType type;                // Decomposition type, contained in this object.

  // Input data for decomposition.
  public Blanket I;                             // The blanket for binary input, may be null.
  public Blanket Iv;                            // The blanket for binary input - may be null, if it doesn't then there is I = Iv x Iu relation.
  public Blanket Iu;                            // The blanket for binary input - may be null, if it doesn't then there is I = Iv x Iu relation.
  public Blanket Q;                             // The blanket for input that being the object for encoding (binary/multiple-valued) through decomposition.

  // Decomposition results, Q = Qv x Qu
  public boolean QvJoinedWithQu;                // Value "true" means that as a result an joint decomposition was found and there is Qv <= Qu relation.
  public Blanket Qv;
  public Blanket QvPartial;                     // If there is joint decomposition result then Qv = QvPartial x Qu.

  public boolean QuJoinedWithQv;                // Value "true" means that as a result an joint decomposition was found and there is Qu <= Qv relation.
  public Blanket Qu;
  public Blanket QuPartial;                     // If there is joint decomposition result then Qu = QuPartial x Qv.

  public FunctionsDependencyType dependencyOfG;
  public Blanket G;                             // The blanket for resulting function G, i.e. Y = G x H

  public FunctionsDependencyType dependencyOfH;
  public Blanket H;                             // The blanket for resulting function H, i.e. Y = G x H

  private DecompositionBlankets()
  {
    Qv = null;
    Qu = null;

    QvJoinedWithQu = false;
    QuJoinedWithQv = false;

    dependencyOfG = null;
    dependencyOfH = null;
  }

  public static DecompositionBlankets create()
  {
    return new DecompositionBlankets();
  }

  @Override
  public String toString()
  {
    return "DecompositionBlankets{" +
      "type=" + type +
      ", I=" + I +
      ", Iv=" + Iv +
      ", Iu=" + Iu +
      ", Q=" + Q +
      ", QvJoinedWithQu=" + QvJoinedWithQu +
      ", Qv=" + Qv +
      ", QvPartial=" + QvPartial +
      ", QuJoinedWithQv=" + QuJoinedWithQv +
      ", Qu=" + Qu +
      ", QuPartial=" + QuPartial +
      ", dependencyOfG=" + dependencyOfG +
      ", G=" + G +
      ", dependencyOfH=" + dependencyOfH +
      ", H=" + H +
      '}';
  }
}
