jfuzzylite &trade; [![Build Status](https://travis-ci.org/fuzzylite/jfuzzylite.svg?branch=master)](https://travis-ci.org/fuzzylite/jfuzzylite) <img src="https://github.com/fuzzylite/jfuzzylite/raw/master/jfuzzylite.png" align="right" alt="jfuzzylite">
==========

A Fuzzy Logic Control Library in Java
-------------------------------------

Current version: 5.0 (April, 2015)

By: [Juan Rada-Vilela](http://www.fuzzylite.com/jcrada), Ph.D.


***


### Table of Contents
[Introduction](#introduction) &nbsp;
[License](#license) &nbsp;
[Features](#features) &nbsp;
[Example](#example) &nbsp;
[Bulding from source](#building) &nbsp;
[Binaries](#binaries)
[What's next](#whatsnext) &nbsp;
[What's new](#whatsnew) : [General](#new-general),[Operation](#new-operation), [Engine](#new-engine), [Input Variables and Output Variables](#new-inoutvars), [Linguistic Terms](#new-terms), [Linear and Discrete Terms](#new-linear-discrete), [Function Term](#new-function), [[T|S]Norms and Hedges](#new-norms-hedges), [Rules](#new-rules), [Rule Blocks](#new-ruleblocks), [Weighted Defuzzifiers](#new-weighted), [Integral Defuzzifiers](#new-integral), [Importers and Exporters](#new-imex), [Examples](#new-examples),  [Console](#new-console), [Fixed Bugs](#new-fixes)


***


### <a name="introduction">Introduction</a>
`jfuzzylite` is a free and open-source fuzzy logic control library programmed in Java, thereby making it available for multiple platforms (Windows, Linux, Mac), and especially for Android.  Its goal is to allow you to easily create fuzzy logic controllers in a few steps  utilizing object-oriented programming without requiring any third-party libraries.

#### Reference
If you are using `jfuzzylite`, please cite the following reference in your article:

Juan Rada-Vilela. fuzzylite: a fuzzy logic control library, 2014. URL http://www.fuzzylite.com.

```bibtex
 @misc{fl::fuzzylite,
 author={Juan Rada-Vilela},
 title={fuzzylite: a fuzzy logic control library},
 url={http://www.fuzzylite.com},
 year={2014}}
```


### <a name="license">License</a>
`jfuzzylite` 5.0 is dual licensed under the [**GNU Lesser General Public License (LGPL) v3.0**](https://www.gnu.org/licenses/lgpl.html) and a **paid commercial license**. If your application requires a commercial license, please contact [sales@fuzzylite.com](mailto:sales@fuzzylite.com). For further information on your rights, please refer to the [GNU LGPL](https://www.gnu.org/licenses/lgpl.html).

The change of license is an attempt to raise funds in order to be able to work  part-time in the development of the `fuzzylite` family of products, namely `fuzzylite` (C++), `jfuzzylite` (Java), `pyfuzzylite` (Python), and `QtFuzzyLite` (Windows/Linux/Mac).

**There are still many things to do!**

Besides [donations](http://www.fuzzylite.com/donations/), you can significantly contribute by **purchasing a license** of the entirely new [`QtFuzzyLite`](http://www.fuzzylite.com/qt/) commercial application. In addition, if you require (paid) private support, please contact [jcrada@fuzzylite.com](mailto:jcrada@fuzzylite.com).


***

### <a name="features">Features</a>

**Controllers** *Types* (5) Mamdani, Takagi-Sugeno, Larsen, Tsukamoto, Inverse Tsukamoto

**Linguistic terms** *Basic* (4) triangle, trapezoid, rectangle, discrete. *Extended* (9) bell, cosine, gaussian, gaussian product, pi-shape, sigmoid difference, sigmoid product, spike. *Edges* (4) concave, ramp, sigmoid, s-shape, z-shape. *Functions* (3) constant, linear, function.

**Conjunction and Activation** *T-Norm* (7) minimum, algebraic product, bounded difference, drastic product, einstein product, hamacher product, nilpotent minimum.

**Disjunction and Accumulation** *S-Norm* (8) maximum, algebraic sum, bounded sum, normalized sum, drastic sum, einstein sum, hamacher sum, nilpotent maximum.

**Defuzzifiers** *Integral* (5) centroid, bisector, smallest of maximum, largest of maximum, mean of maximum, *Weighted* (2) weighted average, weighted sum.

**Hedges** *Types* (6) any, not, extremely, seldom, somewhat, very.

**Import** *Types* (3) FuzzyLite Language `fll`, Fuzzy Inference System `fis`, Fuzzy Control Language `fcl`.

**Export** *Types* (6) `C++`, `Java`, FuzzyLite Language `fll`, FuzzyLite Dataset `fld`, Fuzzy Inference System `fis`, Fuzzy Control Language `fcl`.

**Examples** (30+) of Mamdani, Takagi-Sugeno and Tsukamoto controllers from `fuzzylite`, Octave and Matlab, each included in the following formats: `C++`, `Java`, `fll`, `fld`, `fis`, and `fcl`.

***

### <a name="example">Example</a>

```java
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class Example {

    public static void main(String[] args){
        Engine engine = new Engine();
        engine.setName("simple-dimmer");

        InputVariable ambient = new InputVariable();
        ambient.setName("Ambient");
        ambient.setRange(0.000, 1.000);
        ambient.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        ambient.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        ambient.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        engine.addInputVariable(ambient);

        OutputVariable power = new OutputVariable();
        power.setName("Power");
        power.setRange(0.000, 1.000);
        power.setDefaultValue(Double.NaN);
        power.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
        power.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        power.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
        engine.addOutputVariable(power);

        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.addRule(Rule.parse("if Ambient is DARK then Power is HIGH", engine));
        ruleBlock.addRule(Rule.parse("if Ambient is MEDIUM then Power is MEDIUM", engine));
        ruleBlock.addRule(Rule.parse("if Ambient is BRIGHT then Power is LOW", engine));
        engine.addRuleBlock(ruleBlock);

        engine.configure("", "", "Minimum", "Maximum", "Centroid");

        StringBuilder status = new StringBuilder();
        if (!engine.isReady(status)) {
            throw new RuntimeException("Engine not ready. "
                    + "The following errors were encountered:\n" + status.toString());
        }

        for (int i = 0; i < 50; ++i) {
            double light = ambient.getMinimum() + i * (ambient.range() / 50);
            ambient.setInputValue(light);
            engine.process();
            FuzzyLite.logger().info(String.format(
                    "Ambient.input = %s -> Power.output = %s",
                    Op.str(light), Op.str(power.getOutputValue())));
        }
    }
}
```

***

### <a name="whatsnext">What's Next?</a>

+ Source code documentation
+ Type-2 Fuzzy Logic Controllers
+ Adaptive Neuro-Fuzzy Inference System (ANFIS)
+ Fuzzy C-means data clustering

***

### <a name="building">Building from Source</a>
Building from source requires you to have either Ant or Maven installed.

#### Ant
```bash
$ ant -f build.xml
```

The Ant script will create the library `bin/jfuzzylite.jar` and the library sources `bin/jfuzzylite-src.jar`.

#### Maven
```bash
$ mvn install
```

The Maven script will create the library `target/jfuzzylite-5.0.jar` and library sources `target/jfuzzylite-5.0-sources.jar`.


### <a name="binaries">Binaries</a>

Besides the library, the binaries contain the FuzzyLite Console application. The application can be executed using the following command.

```bash
$ java -jar jfuzzylite.jar
```

In addition, the new FuzzyLite Interactive Console is activated when exporting to `fld` without providing an output file. The interactive console allows you to evaluate controllers manually providing the input values.


***

### <a name="whatsnew">What's New?</a>
Just like the `fuzzylite` library, the entire `jfuzzylite` library has been thoroughly revised, refactored, validated, and significantly improved. The following sections detail the changes and enhancements of version 5.0. Users of version 1.0 are **strongly** encouraged to carefully read the list before migrating to version 5.0. Important changes and enhancements are marked as **(important)**, **(VERY important)** and **(EXTREMELY important)**.

#### <a name="new-general">General</a>
+ **(important)** `jfuzzylite v5.0` is dual-licensed under the [GNU LGPL v3.0](https://www.gnu.org/licenses/lgpl.html) and a paid commercial license.
+ Compilation using Ant and/or Maven
+ Refactoring of many classes to improve design and performance.
+ **(important)** Performance improvements of Takagi-Sugeno controllers by over 55% with respect to v1.0 (estimate based on the average performance on the examples included).
+ **(important)** New file [`jfuzzylite/src/compare.m`](/jfuzzylite/src/main/java/com/fuzzylite/m/compare.m) to compare the output values of your `fuzzylite` engines with the evaluation of the same engine in Octave/Matlab.
+ **(important)** There is practically no difference between the output values obtained with `fuzzylite` and those obtained with Octave/Matlab. Based on the examples, the average mean square error (MSE)  between the output values is less than `7.3e-12` (or `0.0000000000073`) due to negligible differences in floating-point arithmetic. The results and comparison can be found in [`examples/examples.mat`](/examples/examples.mat).
+ **(important)** Source code of applications based on version 1.0 will not compile with version 5.0.
+ Minor bugs fixed.


#### <a name="new-operation">Operation</a>
* **(important)** Added method  `Op::toDouble(String x, double alternative)` which returns `alternative` if `x` is not a valid `double`, and never throws an exception.
* **(important)** Added method `Op::toDouble(String x)` that returns the double value of `x` or throws a `fl::Exception` if `x` is not a valid `double`.
* Added method `Op::isFinite(x)` which returns `not (isNaN(x) or isInf(x))`.
* Changed `Op::isEq(a,b)` to return `true` if `a == b == NaN`.
* Changes to `Op::isEq` affect other comparison methods `fl::Op::is[Lt|LEq|GEq|Gt]`.
* Deleted method `Op::repeat`.
* Removed method `FuzzyLite::configuration()`.
* Changed default  `FuzzyLite::macheps = 1e-6;`.
* Renamed method `Op::makeValidId` to `Op::validName`, which now returns `"unnamed"` for empty strings.



####<a name="new-engine">Engine</a>
* **(VERY important)** Moved `Engine::hedges` (and relevant methods) to `Rule::hedges`.
* Added enumeration for the type of engine:  `enum Engine::Type{Mamdani, Larsen, TakagiSugeno, Tsukamoto, InverseTsukamoto, Hybrid, Unknown}`.
* Added method `Type Engine::type(String name, StringBuilder reason)` to infer the type of the engine based on its configuration, additionally provides the name of the type and the inference reasons for its type.
* **(important)**  Changed method `Engine::isReady(StringBuilder)` to satisfy the default operation of controllers.
* Added methods `Engine::set[Input|Output]Variables(List)` and `Engine::setRuleBlocks(List)`.
* Added methods `Engine::get[input|output]Variables()` and `Engine::getRuleBlocks()` to return mutable references.
* Added method `Engine::variables()` to retrieve a  vector (copy) containing  the `InputVariables` followed by the `OutputVariables`.
* Added method `Engine::clone()`.
* Added clone methods to most classes.



####<a name="new-inoutvars">Input Variables and Output Variables</a>
* **(important)** Added methods `OutputVariable::[get|set]OutputValue()` to [retrieve|store] value from defuzzification
* **(important)** Changed return type of `double OutputVariable::defuzzify()` to `void OutputVariable::defuzzify()` because now it automatically stores the defuzzified output value, and also stores the previous *valid* output value regardless of locks. Like in version 1.0, if  `OutputVariable::lockPreviousOutputValue=true`, and the defuzzified output value is not valid (i.e., `[NaN|Inf]`) or no rules were activated, then the defuzzified output value is replaced for the previous valid output value.
* Removed method `OutputVariable::defuzzifyNoLocks()`.
* Renamed variable `OutputVariable::lastValidOutputValue` to `OutputVariable::previousOutputValue`.
* **(important)** Renamed method `OutputVariable::[get|set]LastValidOutput()` to `OutputVariable::[get|set]PreviousOutputValue()`.
* Renamed variable `OutputVariable::lockValidOutput` to `OutputVariable::lockPreviousOutputValue`.
* **(important)** Renamed method `OutputVariable::setLockValidOutput()` to `OutputVariable::setLockPreviousOutputValue()`.
* **(important)** Renamed method `OutputVariable::isLockingValidOutput()` to `OutputVariable::isLockPreviousOutputValue()`.
* Renamed variable `OutputVariable::lockOutputRange` to `OutputVariable::lockOutputValueInRange`.
* **(important)** Renamed method `OutputVariable::setLockOutputRange()` to `OutputVariable::setLockOutputValueInRange()`.
* **(important)** Renamed method `OutputVariable::isLockingOutputRange()` to `OutputVariable::isLockOutputValueInRange()`.
* Added methods `String InputVariable::fuzzyInputValue()` and `String OutputVariable::fuzzyOutputValue()`.
* Added method `OutputVariable::clear()` to clear the fuzzy output, and set `OutputVariable::previousOutputValue = fl::nan` and set `OutputVariable::outputValue = fl::nan`.
* Added clone methods to [Input|Output]Variable.
* Added method `Variable::getTerms()` to return mutable reference.


#### <a name="new-terms">Linguistic Terms</a>
* **(VERY important)** Added Term::[get|set]Height to define the height of *integral* terms, and multiply their respective membership functions accordingly.
* Added clone methods to every `Term`.
* **(VERY important)** Parameters of all terms are set by default to `fl::nan`.
* **(important)** Renamed method `Term::copy()` to `Term::clone()` in every `Term`.
* Added method `Term::updateReference(Term, Engine)` to ensure `Linear` and `Function` terms have updated pointers to the `Engine` (useful when cloning terms).
+ **(important)** Added linguistic terms `Concave`, `Cosine` and `Spike`.
* **(important)** Changed `Accumulated` to take `Activated` terms instead of `Terms`.
* Added method `Accumulated::getTerms()` to return mutable reference.
* **(important)**  Renamed methods `Triangle::[set|get][A|B|C]` to `::[set|get]Vertex[A|B|C]`.
* **(important)** Renamed methods `Trapezoid::[set|get][A|B|C|D]` to `::[set|get]Vertex[A|B|C|D]`.
* **(important)** Renamed term `Thresholded` to `Activated`.
* **(important)**  Renamed methods `Thresholded::[set|get]Threshold()` to `Activated::[set|get]Degree()`.
* Added enumeration `[Ramp|Sigmoid]::Direction{ NEGATIVE, ZERO, POSITIVE }` to refer to the slope.
* Added methods `Ramp::direction()` and `Sigmoid::direction()` to retrieve direction of slope.
* Removed Exception Specification from methods in `Discrete`, `Linear` and `Function` terms.

#### <a name="new-linear-discrete">Linear and Discrete Terms</a>
* **(important)** Changed `Linear` from having references to the input variables to having a reference to the `Engine`.
* Changed visibility of `Linear::coefficients` to `private`.
* **(important)**  Added methods `Linear::getCoefficients()`, `Linear::setCoefficients()`.
* `Linear` term no longer throws exception when `inputVariables != |coefficients|`.
* **(important)** Removed public vector of variables `Discrete::[x|y]`.
* **(important)** Added a `class Discrete::Pair`.
* **(important)** Changed representation of `Discrete::[x|y]` from `List<Double>` to `List<Discrete::Pair>`.
* Added methods `Discrete::setXY()` and `Discrete::getXY()` to set and get the new representation of pairs.
* **(important)** Added methods `Discrete::toPairs(List<Double>)` which throws an exception if the vector is missing a value (i.e., `List<Double>.size() % 2 != 0`), and `Discrete::toPairs(List<Double>, double missingValue)` which adds `missingValue` in case `List<Double>.size() %2 == 1`, hence never throwing an exception.
* Added method `Discrete::toList(List<Discrete::Pair>)` to convert `List<Discrete::Pair>` to a `List<Double>`.
* Added method `Discrete::formatXY()` to get pairs `(x,y)` nicely formatted.

####<a name="new-function">Function Term</a>
* **(important)** Merged classes `Function::Operator` and `Function::BuiltInFunction` into a  single `class Function::Element`.
* **(EXTREMELY important)**  Changed the precedence of all built-in instances of `Function::Element` of type `Operator` starting from `100` and decreasing by `10`. The precedence of built-in operators  is the following: `(100)` Logical not `[!]` and Negation `[~]`; `(90)` Power `[^]`; `(80)` Multiplication `[*]`, Division `[/]` and Modulo `[%]`; `(70)` Addition `[+]` and Subtraction `[-]`; `(60)` Logical AND `[and]` and Logical OR `[or]`. If you have registered your own operators, please adjust their precedence as required.
* Added to `Function` built-in comparison functions `gt,lt,ge,le,eq` and operator logical not `!`.
* Changed `public Function::root` to `private Function::root`.
* Added method `Function::getRoot()` to return reference to `Function::root`.
* **(EXTREMELY important)** Moved built-in functions and operators from `Function` to a `FunctionFactory`.


#### <a name="new-norms-hedges">[T|S]Norms and Hedges</a>
+ **(important)** Fixed operation when using multiple hedges to operate from right-most to left-most, e.g. `if Ambient is not very extremely bright`, now evaluates as follows `not(very(extremely(bright)))`.
+ Added `TNorm` nilpotent minimum and `SNorm` nilpotent maximum.
* Added clone methods to every `Norm`.
* Added clone methods to every `Hedge`.
* **(VERY important)** Moved `Engine::hedges` to `Rule::hedges`.


#### <a name="new-rules">Rules</a>
* **(VERY important)** Moved `Engine::hedges` (and methods) to `Rule::hedges`.
* Added method `Rule::isLoaded()` to determine whether a rule was properly parsed and thus can be activated.
* Added method `Rule::unload()` to allow the existence of a rule in an inactive state (useful for invalid rules).
* **(important)** Removed variable `Rule::FL_ASSIGNS` and method `Rule::assignsKeyword()`, for which the symbol `=` in rules is no longer valid.
* Changed visibility of method `Rule::setText()` to `public`.
* Added method `Rule::load(const Engine*)`.
* **(important)** Renamed method `Antecedent::[get|set]Root()` to `Antecedent::[get|set]Expression()`.
* Added methods `[Antecedent|Consequent]::[get|set]Text()`.
* **(important)** Added methods `[Antecedent|Consequent]::[load|unload]()`, with the same objective as `Rule::[load|unload]()`.

#### <a name="new-ruleblocks">Rule Blocks</a>
* Added method `RuleBlock::reloadRules()`.
* Added method `RuleBlock::setRules(List)`.
* Added method `RuleBlock::getRules()` to return mutable reference.
* **(VERY important)** Added basic rule chaining such that an `OutputVariable` can be utilized in the `Antecedent` of a `Rule`. For example, considering the rule `if Power is high then InversePower is low`, where `Power` and `InversePower` are both output variables, the activation degree of the `Antecedent` will correspond to the accumulated  activation degree of the term `high` in the fuzzy output of `Power`. If `Power::accumulation = none`, the accumulated activation degree of the term `high` will be computed as the regular sum of the activation degrees of term `high` in the fuzzy output of `Power`. Otherwise, the accumulated activation degree is computed utilizing the `Power::accumulation` operator.


#### <a name="new-weighted">Weighted Defuzzifiers</a>
+ **(VERY important)** Performance improvements of Takagi-Sugeno controllers by over 55% (with respect to v4.0) based on the average performance on the examples included.
* **(important)** Created class `WeightedDefuzzifier` from which classes `Weighted[Average|Sum]` are derived.
* **(important)** Added enumeration `WeightedDefuzzifier::Type{Automatic, TakagiSugeno, Tsukamoto}` and respective methods `WeightedDefuzzifier::[get|set]Type()` and `WeightedDefuzzifer::getTypeName()`.
* Added method `WeightedDefuzzifier::inferType(Term)` to automatically determine the `WeightedDefuzzifier::Type` based on the class of `Term`.
* **(important)** By default, `WeightedDefuzzifier::type = Automatic`, which automatically infers the type based on the `WeightedDefuzzifier::inferType()`.
* **(important)** There is a small performance penalty when using `WeightedDefuzzifier::type = Automatic` because `WeightedDefuzzifier::inferType()` performs three `object instaceof Class`.
* **(important)** Deleted class `Tsukamoto`. Its method `static tsukamoto()` was moved to `WeightedDefuzzifier::tsukamoto()`, which allows overriding it
* Added support for `Tsukamoto` with `Concave` terms.
+ **(EXTREMELY important)** In version 5.0, the traditional operation of Takagi-Sugeno and Tsukamoto controllers is achieved by setting `OutputVariable::accumulation = none`. Unlike version 1.0, the `RuleBlock::activation` will *not* have any effect on Takagi-Sugeno nor Tsukamoto controllers, for which `RuleBlock::activation` should also be set to `none`. More information about the roles of the  `OutputVariable::accumulation` and `RuleBlock::activation` operators are detailed as follows. Refer to [sciweavers](http://www.sciweavers.org/free-online-latex-equation-editor) to convert LaTeX equations.
+ **(VERY important)** In version 5.0, the role of the  `RuleBlock::activation` `TNorm` on the `Weighted[Average|Sum]` always performs a regular multiplication of the weights and the values (i.e., $w_i \times z_j$) regardless of the `TNorm` chosen. In other words, selecting any `RuleBlock::activation` for `Weighted[Average|Sum]` is irrelevant, and should be set to `none` as every `TNorm` will have the same multiplication effect. This operation is different from `jfuzzylite` version 1.0, where the `RuleBlock::activation` operator was utilized to multiply the weights and values (i.e. $w_i \otimes z_j$), and therefore the traditional operation of the `Weighted[Average|Sum]` was achieved when `RuleBlock::activation =  AlgebraicProduct;`.
+ **(VERY important)** In version 5.0, the role of the `OutputVariable::accumulation = none` on the `Weighted[Average|Sum]` results in a regular sum of the multiplied weights and values, i.e., $\dfrac{\sum_i^n w_i \times z_j}{\sum_i^n w_i}$. However, if the `OutputVariable::accumulation != none`, the role of the `SNorm` will be to accumulate the activation degrees of the *repeated* terms in the fuzzy output of the variable. For example, considering the rules `if Ambient is dark then Power is high` and `if Ambient is medium then Power is high`, for any  input value of `Ambient` that activates both rules, the fuzzy output of `Power` will have the term `high` activated with the degree from `Rule 1`, and the term `high` activated with the degree from `Rule 2`. Since the term `high` appears twice in the fuzzy output, the role of the accumulation operator will be to accumulate the activation degree of `high` resulting in $\dfrac{(w_1 \oplus w_2) \times z_{high}}{(w_1 \oplus w_2)}$. If another term were activated, the result would be $\dfrac{(w_1 \oplus w_2) \times z_{high} + w_i \times z_j}{(w_1 \oplus w_2) + w_i}$. In version 1.0, the accumulation operator had no effect on the `Weighted[Average|Sum]`.



#### <a name="new-integral">Integral Defuzzifiers</a>
* **(important)** Proper handling of indefinite integral defuzzification, that is, returning `fl::nan` when `[minimum|maximum]=[NaN|Inf]`.
* Default resolution of integration is defined as `static int IntegralDefuzzifier::defaultResolution=200`, and can be changed via `static IntegralDefuzzifier::setDefaultResolution()`.
+ **(important)** In `fuzzylite`, the accumulation operator has been for several versions associated with the output variables and **not** with the rule blocks, despite that the FCL format and other fuzzy logic control libraries associate the accumulation operator with the rule blocks. The argument for such a decision is that `fuzzylite` provides **coherent** support for multiple rule blocks operating on the same engine and on the same output variables. For example, if multiple rule blocks operate on the same output variables, it  only makes sense to have a single accumulation operator associated with each output variable such that the defuzzifier can naturally operate over the accumulated fuzzy output. Differently, if the accumulation operator were associated with the rule block, the possibility of having different accumulation operators in different rule blocks questions (1) **the possibility of having multiple rule blocks operating over the same output variables**; and (2) **the usage of different  accumulation operators over the accumulation and defuzzification processes**. Certainly, if (1) is not possible, i.e,  different rule blocks only operate on different output variables, then (2) is not a problem because the accumulation process and defuzzification of each variable will only have a single accumulation operator. It is therefore that the association of the accumulation operator with the output variable in `fuzzylite` provides a **better design** and an additional feature that allows having multiple rule blocks operating over the same output variables.
* Added method `Defuzzifier::clone()`.

####<a name="new-imex">Importers and Exporters</a>
* **(EXTREMELY important)** Since terms have a new `height` property, `[Fll|Fis|Fcl]Exporter` exports terms with an additional `double` at the end, which indicates the `height` of the term. However, if `height=1.0`, the additional scalar is not exported.
* **(EXTREMELY important)** In `[Fll|Fis|Fcl]Importer`,  when importing terms, if there is an additional `double` it will be assumed as the `height` of the term. For example, `term: high Gaussian 1.0 0.5 0.75` will create a `Gaussian` term with mean `1.0`, standard deviation `0.5` and height `0.75`. This is **extremely important** because there are some examples from Matlab in `fis` format that append a useless `0.0` to some terms.
* **(EXTREMELY important)** In `FisExporter`, if the Takagi-Sugeno controller has no `activation` or `accumulation` operators (as it should generally be the case), Octave and Matlab will not be able to import the `fis` file. To overcome this issue, you will have to set `ImpMethod="min"` and `AggMethod="max"`, where `ImpMethod` and `AggMethod` are just dummy operators that can be set to any `TNorm` and `SNorm`, respectively.
+ **(important)** Improved compatibility of the exported code obtained with `[Fis|Fcl]Exporter` by exporting the additional features of `jfuzzylite` only when these are different from the default operation. For example, the following features will not be exported given their values: `[Input|Output]Variable::enabled = true;`, `OutputVariable::lock-previous = false;`, `OutputVariable::lock-range = false;`, amongst others.
* **(important)** Renamed FLL property `'lock-valid'` to `'lock-previous'`.
* **(important)** Renamed FIS property `'LockValid'` to `'LockPrevious'`.
* **(important)** Renamed FCL property `'LOCK: VALID'` to `'LOCK: PREVIOUS'`.
+ **(important)** Export your controllers to files using `[Fll|Fld|Fis|Fcl]Exporter::toFile()`.
+ **(important)** Import your controllers from files using `[Fll|Fis|Fcl]Importer::fromFile()`.
+ **(important)** `FldExporter` exports the FuzzyLite Dataset of an engine utilizing the input values of another FuzzyLite Dataset.
* **(important)** Renamed method `FldExporter::toWriter()` to `FldExporter::write()`.
* Removed variable and methods for property `int FldExporter::maximum`.
* Added option in `CppExporter` to prepend the  namespace prefix `fl::` to the classes, and by default it does not prepend prefix.
* Improvement accuracy of `FisImporter` when importing `fis` files whose double values have more than  three decimal numbers.
* Renamed methods in `[Fis|Fcl]Importer::extract*` to `[Fis|Fcl]Importer::parse*`.



#### <a name="new-factories">Factories</a>
* Created a generic `CloningFactory<T>` to create clones of objects.
* **(important)** Created `FunctionFactory` based on `CloningFactory<Function::Element>` where function operators and methods are stored to be cloned as necessary by `Function`. Additional functions and operators can be easily registered.
* **(VERY important)** Moved built-in functions and operators from `Function` to `FunctionFactory`.
* Renamed methods `Factory<T>::[register|deregister]Class()` to `Factory<T>::[register|deregister]Constructor()`.
* **(important)** Renamed `Factory<T>` to `ConstructionFactory<T>`.



#### <a name="new-examples">Examples</a>
* **(important)** Added two examples for basic rule chaining: `mamdani/SimpleDimmerInverse.fll` and `mamdani/Laundry.fll`.
* Included the `original` example files in `fis` format.
* Added conversion of `examples/original/*.fis` to `examples/original/*.fll`.
* Modified `original/takagi-sugeno` examples to reflect  `activation: none; accumulation: none;`.
* Updated FLD examples produced from the `original` examples.
+ **(important)** Added file [`fuzzylite/src/m/compare.m`](/jfuzzylite/src/main/java/com/fuzzylite/m/compare.m) to compare the output values of your `fuzzylite` engines with the evaluation of the same engine in Octave/Matlab.
+ **(important)** Added file [`examples/examples.mat`](/examples/examples.mat) containing the comparison of the output values between `fuzzylite` and Matlab's Fuzzy Logic Toolbox.
* Added code to perform benchmarks.


#### <a name="new-console">Console</a>
* **(important)** Console includes option to import custom input dataset from file and export its respective output values.
* **(important)** Created the FuzzyLite Interactive Console, which can be started by specifying an input file and the output format, e.g., `fuzzylite -i SimpleDimmer.fll -of fld`.
* Console provides more information about its usage.



####<a name="new-fixes"> Fixes Bugs and Leaks</a>
+ **(important)** Fixed operation when using multiple hedges to operate from right-most to left-most, e.g. `if Ambient is not very extremely bright` evaluates as follows `not(very(extremely(bright)))`.
* **(important)** Fixed membership functions of specific cases of `Triangle` when `a=b` or `b=c`, and `Trapezoid` when `a=b` or `c=d`.


***


For more information, visit [www.fuzzylite.com](http://www.fuzzylite.com).

fuzzylite&reg; is a registered trademark of FuzzyLite Limited.

jfuzzylite&trade; is a trademark of FuzzyLite Limited.

Copyright &copy; 2010-2015 FuzzyLite Limited. All rights reserved.


