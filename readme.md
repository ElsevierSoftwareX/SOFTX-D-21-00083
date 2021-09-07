# DECOlib

DECOLib ia a Java library, which uses blanket algebra and providing data structures and methods applicable
for decompositions of logical functions, binary or multiple-valued ones. The decomposition is a process
used during optimization of logical functions, such minimization, state encoding, mapping and re-synthesis
processes. Some of these method has been used for optimisation of binary and multiple-valued functions,
applicable for LUT based FPGA devices. However the scope of usage might be wider, when multiple-valued
functions are considered. For instance the applications for data mining might use decompositions for finding
relations between items, which might not be exposed without breakdown of data, where large data set might be
modelled using multiple-valued variables. The software provides a low level functions – components which
allows for building the decomposition algorithms for binary and/or multiple-valued logical functions. There
are also presented examples for basics of those algorithms as well as suggestions for the further research
directions in this scope. DECOLib is free and open-source library.

## Table of contents

- [Application Programming Interface (API)](#api)
- [Examples](#examples)
- [References](#references)
- [License](#license)

## Application Programming Interface (API)

### Dependency

To add a dependency in your project's pom.xml use the following:

```
<dependency>
    <groupId>decolib</groupId>
    <artifactId>decolib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage

DECOLib is designed for handling the blanket algebra[1] and provides for it the methods and data structures.

The main element is Blanket class, providing sort of static method, used to handling blankets, also blanket
related computational functions are here exposed. Because blankets are a sets, it is crucial to allow easy
creations and manipulation of them, thus suitable methods were also defined. As was mentioned before,
blankets consist of blocks, what is provide in Block class. Also block operations are provided in this class.

The Decomposition class contains two sample algorithms for decompositions of logic functions, the parallel
decomposition[2] and serial one[2]. Those algorithms use helper functions, defined in DecompositionsHelpers
and implemented with respect to theorem and definitions presented in [3].

Because originally the methods were intended for logic functions optimisation, the software provides tool
methods, allowing for loading of data from logical table. These method are provided in Signals and
DaraLoaders classes.

Together with the library the examples are provided, allowing also for further processing or performing
a sort of research. For instance, in case of decompositions the various method of arguments/function splitting
might be investigated. Therefore the software allows for customisation on functionality level or through
modification of provided algorithms.

Along with the library, some examples for decompositions are also provided, which may be reviewed at [5].
User may refer also to provided API[6]

### Possible extensions

The library might be easily extended, allowing stating a new research:
* for the parallel decomposition the methods of preparing a preliminary division of the function may be
  also developed,
* provided methods of decomposition may be improved, especially the method of choosing the partial result
  in serial decomposition,
* for the serial decomposition more optimization parameters (required for various optimisations goals)
  may be provided, allowing for creation more specific multiple-valued network, during the optimization
  process,
* basic methods may be used for state encoding and re-synthesis process[4] of logical functions,
* the field for multiple-valued network optimization is still open for a new research.

## Examples

=== Basic blankets operations
The blankets consist of blocks and blocks consists of terms. The term is an element of the set over which
the blanket is defined. In general, for logical functions the terms corresponds to one row of this function
logical table. For instance for function:

```
    x1 x2 x3 | y1 y2 y3
    ---------+---------
 1  0  -  S0 | 0  0  0  <-----+
 2  -  0  S0 | 0  0  0        |
 3  1  1  S0 | 0  1  1        |
 4  -  -  S1 | 0  1  0        |
 5  -  -  S2 | 1  0  1        |
 6  1  0  S3 | 1  0  0        |
 7  0  -  S3 | 1  1  1        |
 8  -  1  S3 | 1  1  1        |
 9  -  -  S4 | 1  1  0        |
10  -  -  S5 | 0  0  1        |
                              |
                       βx1 = {1,2,4,5,7,8,9,10; 2,3,4,5,6,8,9,10;}
```

the blanket for column 1 is equal to βx1. The value "1" from this blanket (indicated above) corresponds to
the term 1 from the function.

For this function the following blankets may be created:

```
Blanket βx1 = Blanket.create("0:1,2,4,5,7,8,9,10; 1:2,3,4,5,6,8,9,10;");
Blanket βx2 = Blanket.create("0:1,2,4,5,6,7,9,10; 1:1,3,4,5,7,8,9,10;");
Blanket βx3 = Blanket.create("S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;");
Blanket βy1 = Blanket.create("0:1,2,3,4,10; 1:5,6,7,8,9;");
Blanket βy2 = Blanket.create("0:1,2,5,6,10; 1:3,4,7,8,9;");
Blanket βy3 = Blanket.create("0:1,2,4,6,9; 1:3,5,7,8,10;");
```

When the blankets are created, there may be used methods related to blanket algebra, for instance the relation
between blankets may be computed:

```
result = βx1.BleB(βx1);  // true
result = βx1.BleB(βx2);  // false
result = βx1.BleB(βy1);  // false
result = βx2.BleB(βy1);  // false
result = βx3.BleB(βy1);  // true
result = βx1.BleB(βy2);  // false
result = βx2.BleB(βy2);  // false
result = βx3.BleB(βy2);  // false
result = βx1.BleB(βy3);  // false
result = βx2.BleB(βy3);  // false
result = βx3.BleB(βy3);  // false
```

Also more complex operation, like blanket product may be computed (blanket βx1x2x3).

```
Blanket βx1x2x3 = βx1.BxB(βx2.BxB(βx3));
```

The methods which computing relations between blankets are also provided.

```
result = βx1x2x3.BleB(βy2); // true
result = βx1x2x3.BleB(βy3); // true
```

=== Parallel decomposition

The parallel decomposition will be illustrated using function shown above.

```
Blanket βI = Blanket.create("1,2,4,5,7,9,10; 1,4,5,7,8,9,10; 2,4,5,6,9,10; 3,4,5,8,9,10;");  // x1x2
Blanket βQ = Blanket.create("S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;");                 // x3
Blanket βG = Blanket.create("1,2,10; 3,4; 5,6; 7,8,9;");    // y2
Blanket βH = Blanket.create("0:1,2,4,6,9; 1:3,5,7,8,10;");  // y3
DecompositionBlankets deco = Decompositions.parallelDecomposition(βI, βQ, βG, βH);
```

As a result the above decomposition, blankets βQv and βQu are produced, respectively:

```
βQv = {S5+S4+S3+S0:1,2,3,6,7,8,9,10; S2+S1:4,5;},
βQu = {S0:1,2,3; S4+S1:4,9; S5+S2:5,10; S3:6,7,8;}.
```

The result can be checked by making calculations.

```
deco.I.BxB(deco.Qv).BleB(deco.G));   // βI  x βQv <= βG - true
deco.I.BxB(deco.Qu).BleB(deco.H));   // βI  x βQu <= βH - true
deco.Qv.BxB(deco.Qu).BeqB(deco.Q));  // βQv x βQu =  βQ - true
```

=== Serial decomposition

The function, used as an illustration for parallel decomposition, will be used also as the example for
the serial decomposition. The setup is the following:

```
Blanket βIv = Blanket.create("1,2,4,5,7,9,10; 1,4,5,7,8,9,10; 2,4,5,6,9,10; 3,4,5,8,9,10;"); // x1x2
Blanket βIu = null;
Blanket βQ = Blanket.create("S0:1,2,3; S1:4; S2:5; S3:6,7,8; S4:9; S5:10;");  // x3
Blanket βY = Blanket.create("1,2; 3; 4; 5; 6; 7,8; 9; 10;");                  // y1y2y3
DecompositionBlankets deco = Decompositions.serialDecomposition(βIv, βIu, βQ, βY, 0, false);
```

As a result the above decomposition, blankets βG, βQv and βQu are produced, respectively:

```
βQv = {S5+S4+S3+S0:1,2,3,6,7,8,9,10; S2+S1:4,5;},
βQu = {S0:1,2,3; S4+S1:4,9; S5+S2:5,10; S3:6,7,8;}
βG = {1,2,4,5,6,9; 3,4,5,7,8,10;}
```

The result can be checked by making calculations.

```
deco.Iv.BxB(deco.Qv).BleB(deco.G));  // βIv x βQv <= βG     - true
deco.G.BxB(deco.Qu).BleB(deco.H));   // βIu x βG x βQu <= βY – true
deco.Qv.BxB(deco.Qu).BeqB(deco.Q));  // βQv x βQu = βQ      - true
```

=== DataLoaders and Signal

Please refer to library tests to refer how DataLoaders and Signal may be used.

## References

[1] J. Brzozowski, T. Łuba, _Decomposition of boolean functions specified by cubes_,
J. Mult.-Valued Logic Soft Computing (2003), pp. 377–417

[2] S. Deniziak, M. Wiśniewski, _An Symbolic Decomposition of Functions with Multivalued Inputs and Outputs
for FPGA-based Implementation_, IEEE International Conference on Field Programmable Logic and Applications (FPL2008),
Heidelberg, pp. 397–402

[3] S. Deniziak, M. Wiśniewski, _Symbolic functional decomposition of multivalued functions_,
J. Mult.-Valued Logic Soft Comput. (5–6) (2015) pp. 425–452

[4] S. Deniziak, M. Wiśniewski, _FPGA-based state encoding using symbolic functional decomposition_,
Electronics Letters (2010), vol. 46, no. 19, pp. 1316–1318

[5] API documentation. [click to visit](https://SoftwareX-link/decolib/docs/1.0.0/api)

[6] Decomposition examples codes [click to visit](https://SoftwareX-link/decolib/test/decompositions)

## License

The MIT License (MIT)

Copyright (c) 2021 Mariusz Wiśniewski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
