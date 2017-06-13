jfuzzylite&trade; 6.0 
====================
<img src="https://raw.githubusercontent.com/fuzzylite/jfuzzylite/master/jfuzzylite.png" align="right" alt="jfuzzylite">


A Fuzzy Logic Control Library in Java
-------------------------------------

By: [Juan Rada-Vilela](http://www.fuzzylite.com/jcrada), Ph.D.

Released: 20/March/2017

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.org/fuzzylite/jfuzzylite.svg?branch=master)](https://travis-ci.org/fuzzylite/jfuzzylite) 
[![Coverity Status](https://scan.coverity.com/projects/5760/badge.svg)](https://scan.coverity.com/projects/5760) 



***


### Table of Contents
[License](#license) <br/>
[Introduction](#introduction)<br/>
[Features](#features)<br/>
[Example](#example) <br/>
[Compile, Link, and Execute](#compile-build-execute)<br/>
[Bulding from Source](#building)<br/>
[Binaries](#binaries) <br/>
[What's new](#whatsnew)<br/>
[What's next](#whatsnext) <br/>




***

### <a name="license">License</a>
`jfuzzylite 6.0` is licensed under the [**GNU General Public License (GPL) 3.0**](https://www.gnu.org/licenses/gpl.html). You are **strongly** encouraged to support the development of the FuzzyLite Libraries by purchasing a license of [`QtFuzzyLite 6`](http://www.fuzzylite.com/downloads/). 

[`QtFuzzyLite 6`](http://www.fuzzylite.com/downloads/) is the new and (very likely) the best graphical user interface available to  easily design and directly operate fuzzy logic controllers in real time. Available for Windows, Mac, and Linux, its goal is to significantly **speed up** the design of your fuzzy logic controllers, while providing a very **useful**, **functional** and **beautiful** user interface. 
Please, download it and check it out for free at [www.fuzzylite.com/downloads/](http://www.fuzzylite.com/downloads/).

***


### <a name="introduction">Introduction</a>


`jfuzzylite` is a free and open-source fuzzy logic control library programmed in Java for multiple platforms (e.g., Windows, Linux, Mac, Android). [`fuzzylite`](https://github.com/fuzzylite/fuzzylite/) is the equivalent library written in C++ for Windows, Linux, Mac, iOS, and others. Together, they are the FuzzyLite Libraries for Fuzzy Logic Control.

<center>
```
The goal of the FuzzyLite Libraries is to easily design and efficiently operate fuzzy logic controllers following an object-oriented programming model without relying on external libraries.
``` 
</center>

#### Reference
If you are using the FuzzyLite Libraries, please cite the following reference in your article:

Juan Rada-Vilela. fuzzylite: a fuzzy logic control library, 2017. URL http://www.fuzzylite.com/.

```bibtex
 @misc{fl::fuzzylite,
 author={Juan Rada-Vilela},
 title={fuzzylite: a fuzzy logic control library},
 url={http://www.fuzzylite.com/},
 year={2017}}
```

#### Documentation
The documentation for the `fuzzylite` library is available at: [www.fuzzylite.com/documentation/](http://www.fuzzylite.com/documentation/). 

***

### <a name="features">Features</a>

**(6) Controllers**: Mamdani, Takagi-Sugeno, Larsen, Tsukamoto, Inverse Tsukamoto, Hybrids

**(21) Linguistic terms**:  (4) *Basic*: triangle, trapezoid, rectangle, discrete. 
(9) *Extended*: bell, cosine, gaussian, gaussian product, pi-shape, sigmoid difference, sigmoid product, spike. 
(5) *Edges*: binary, concave, ramp, sigmoid, s-shape, z-shape. 
(3) *Functions*: constant, linear, function. 

**(7) Activation methods**:  general, proportional, threshold, first, last, lowest, highest.

**(8) Conjunction and Implication (T-Norms)**: minimum, algebraic product, bounded difference, drastic product, einstein product, hamacher product, nilpotent minimum, function.

**(10) Disjunction and Aggregation (S-Norms)**:  maximum, algebraic sum, bounded sum, drastic sum, einstein sum, hamacher sum, nilpotent maximum, normalized sum, unbounded sum,  function.

**(7) Defuzzifiers**:  (5) *Integral*: centroid, bisector, smallest of maximum, largest of maximum, mean of maximum. 
(2) *Weighted*: weighted average, weighted sum.

**(7) Hedges**: any, not, extremely, seldom, somewhat, very, function.

**(3) Importers**: FuzzyLite Language `fll`, Fuzzy Inference System `fis`, Fuzzy Control Language `fcl`.

**(7) Exporters**: `C++`, `Java`, FuzzyLite Language `fll`, FuzzyLite Dataset `fld`, `R` script, Fuzzy Inference System `fis`, Fuzzy Control Language `fcl`.

**(30+) Examples**  of Mamdani, Takagi-Sugeno, Tsukamoto, and Hybrid controllers from `fuzzylite`, Octave, and Matlab, each included in the following formats: `C++`, `Java`, `fll`, `fld`, `R`, `fis`, and `fcl`.





***

### <a name="example">Example</a>
#### FuzzyLite Language
```yaml
#File: ObstacleAvoidance.fll
Engine: ObstacleAvoidance
InputVariable: obstacle
  enabled: true
  range: 0.000 1.000
  lock-range: false
  term: left Ramp 1.000 0.000
  term: right Ramp 0.000 1.000
OutputVariable: mSteer
  enabled: true
  range: 0.000 1.000
  lock-range: false
  aggregation: Maximum
  defuzzifier: Centroid 100
  default: nan
  lock-previous: false
  term: left Ramp 1.000 0.000
  term: right Ramp 0.000 1.000
RuleBlock: mamdani
  enabled: true
  conjunction: none
  disjunction: none
  implication: AlgebraicProduct
  activation: General
  rule: if obstacle is left then mSteer is right
  rule: if obstacle is right then mSteer is left
```
```java
//File: ObstacleAvoidance.java
import com.fuzzylite.*;

public class Example {
    
    public static void main(String[] args){
        Engine engine = new FllImporter().fromFile("ObstacleAvoidance.fll");
        
        StringBuilder status = new StringBuilder();
        if (! engine.isReady(status))
            throw new RuntimeException("[engine error] engine is not ready:\n" + status);

        InputVariable obstacle = engine.getInputVariable("obstacle");
        OutputVariable steer = engine.getOutputVariable("mSteer");

        for (int i = 0; i <= 50; ++i){
            double location = obstacle.getMinimum() + i * (obstacle.range() / 50);
            obstacle.setValue(location);
            engine.process();
            FuzzyLite.logger().info(String.format(
                    "obstacle.input = %s -> steer.output = %s",
                    Op.str(location), Op.str(steer.getValue())));
        }
    }
}
```

#### Java
```java
//File: ObstacleAvoidance.java
import com.fuzzylite.*;

public class Example {
    
    public static void main(String[] args){
        Engine engine = new Engine();
        engine.setName("ObstacleAvoidance");
        engine.setDescription("");

        InputVariable obstacle = new InputVariable();
        obstacle.setName("obstacle");
        obstacle.setDescription("");
        obstacle.setEnabled(true);
        obstacle.setRange(0.000, 1.000);
        obstacle.setLockValueInRange(false);
        obstacle.addTerm(new Ramp("left", 1.000, 0.000));
        obstacle.addTerm(new Ramp("right", 0.000, 1.000));
        engine.addInputVariable(obstacle);

        OutputVariable mSteer = new OutputVariable();
        mSteer.setName("mSteer");
        mSteer.setDescription("");
        mSteer.setEnabled(true);
        mSteer.setRange(0.000, 1.000);
        mSteer.setLockValueInRange(false);
        mSteer.setAggregation(new Maximum());
        mSteer.setDefuzzifier(new Centroid(100));
        mSteer.setDefaultValue(Double.NaN);
        mSteer.setLockPreviousValue(false);
        mSteer.addTerm(new Ramp("left", 1.000, 0.000));
        mSteer.addTerm(new Ramp("right", 0.000, 1.000));
        engine.addOutputVariable(mSteer);

        RuleBlock mamdani = new RuleBlock();
        mamdani.setName("mamdani");
        mamdani.setDescription("");
        mamdani.setEnabled(true);
        mamdani.setConjunction(null);
        mamdani.setDisjunction(null);
        mamdani.setImplication(new AlgebraicProduct());
        mamdani.setActivation(new General());
        mamdani.addRule(Rule.parse("if obstacle is left then mSteer is right", engine));
        mamdani.addRule(Rule.parse("if obstacle is right then mSteer is left", engine));
        engine.addRuleBlock(mamdani);


        StringBuilder status = new StringBuilder();
        if (! engine.isReady(status))
            throw new RuntimeException("[engine error] engine is not ready:\n" + status);

        InputVariable obstacle = engine.getInputVariable("obstacle");
        OutputVariable steer = engine.getOutputVariable("mSteer");

        for (int i = 0; i <= 50; ++i){
            double location = obstacle.getMinimum() + i * (obstacle.range() / 50);
            obstacle.setValue(location);
            engine.process();
            FuzzyLite.logger().info(String.format(
                    "obstacle.input = %s -> steer.output = %s",
                    Op.str(location), Op.str(steer.getValue())));
        }
    }
}
```



***

### <a name="building">Building from Source</a>
Building from source requires you to have either Maven or Ant installed.


#### Maven (preferred)
```bash
$ mvn install
```

The Maven script will create the library `target/jfuzzylite-6.0.jar` and the source code `target/jfuzzylite-6.0-sources.jar`.

#### Ant
```bash
$ ant -f build.xml
```

The Ant script will create the library `target/jfuzzylite-6.0.jar` and the source code `target/jfuzzylite-6.0-sources.jar`.


#### Documentation
The source code of `jfuzzylite` is very well documented using [`doxygen`](www.doxygen.org/) formatting, and the documentation is available at [fuzzylite.com/documentation](http://fuzzylite.com/documentation). If you want to generate the documentation locally, you can produce the `html` documentation from the file [Doxyfile](/Doxyfile) using the command line: `doxygen Doxyfile`. The documentation will be created in the [`documentation`](/documentation) folder.

***

### <a name="binaries">Binaries</a>

The console application of `jfuzzylite` allows you to import and export your engines. Its usage can be obtained executing the console binary. In addition, the console can be set in interactive mode. The `FuzzyLite Interactive Console`  allows you to evaluate a given controller by manually providing the input values. The interactive console is triggered by specifying an input file and an output format. For example, to interact with the `ObstacleAvoidance` controller, the interactive console is launched as follows:

```bash
java -jar jfuzzylite-6.0.jar -i ObstacleAvoidance.fll -of fld
```

***


### <a name="whatsnew">What's New?</a>
* The FuzzyLite Libraries, namely fuzzylite and jfuzzylite, both in version 6.0, are licensed under the GNU General Public License version 3.

* Important performance improvements.

* Refactored the following names for the operation of engines: from activation operator to implication operator, from accumulation operator to aggregation operator.

* Renamed the term `Accumulated` to `Aggregated`.

* New activation methods decouple the activation of rules from the rule block and provide different methods for activating rules (see Activation Methods).

* New class `ActivationFactory` provides a factory of activation methods.

* New class `Benchmark` to evaluate the performance and accuracy of engines.

* New class `RScriptExporter` to export the surfaces of an engine using the `ggplot2` library.

* New class `Binary` term for binary edges.

* New `UnboundedSum` S-Norm in `SNormFactory`.

* New classes `SNormFunction` and `TNormFunction` to create custom functions on any two values using the `Function` class.

* Added description strings to `Engine`, `Variable` and `RuleBlock`

* Privatized previously protected members of classes and subclasses of `Term`, `Variable`, `Rule`, `Defuzzifier`, `[Cloning|Construction]Factory`, `Importer`, `Exporter`, amongst others.

* Added some unit tests and support for future unit tests.

* Bug fixes.

* New example of hybrid engines.

* New example on obstacle avoidance for Mamdani, Takagi-Sugeno, and Hybrid engines.

* New R scripts for each example and its respective surfaces in `pdf` formats.

#### Bug fixes
* Fixed bug computing the `NormalizedSum` S-Norm.
* Fixed bug in `Function` term. Bug: given a formula = "tan(y)" and a map["y"] = 1.0, and executing `Function::load(formula)`, then the map of variables is reset because `load()` calls `unload()` first, causing the deregistration of variable `y`. Solution: Removed method `unload()` from `load()`, thereby causing future `load()` not to reset variables.
* Fixed bug in `Function` when enclosing variable in double parenthesis.

***


For more information, visit [www.fuzzylite.com](http://www.fuzzylite.com).

fuzzylite&reg; is a registered trademark of FuzzyLite Limited.

jfuzzylite&trade; is a trademark of FuzzyLite Limited.

Copyright &copy; 2010-2017 FuzzyLite Limited. All rights reserved.


