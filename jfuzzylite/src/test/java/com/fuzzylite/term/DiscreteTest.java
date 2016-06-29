/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.term;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author juan
 */
public class DiscreteTest {

    public DiscreteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDiscreteFindsInRectangle() {
        FuzzyLite.setLogging(true);
        FuzzyLite.setDebugging(true);
        Rectangle rectangle = new Rectangle("rectangle", 0, 1);
        Discrete discrete = Discrete.discretize(rectangle, rectangle.getStart(), rectangle.getEnd(), 10);
        FuzzyLite.logger().log(Level.INFO, discrete.toString());

        Assert.assertThat(discrete.membership(.25), is(1.0));
        Assert.assertThat(discrete.membership(.0), is(1.0));
        Assert.assertThat(discrete.membership(-1), is(1.0));
        Assert.assertThat(discrete.membership(1), is(1.0));
        Assert.assertThat(discrete.membership(2), is(1.0));
        Assert.assertThat(Double.isNaN(discrete.membership(Double.NaN)), is(true));
        Assert.assertThat(discrete.membership(Double.POSITIVE_INFINITY), is(1.0));
        Assert.assertThat(discrete.membership(Double.NEGATIVE_INFINITY), is(1.0));
    }

    @Test
    public void testDiscreteFindsInTriangle() {
        FuzzyLite.setLogging(true);
        FuzzyLite.setDebugging(false);
        Triangle triangle = new Triangle("triangle", 0, 1);
        Discrete discrete = Discrete.discretize(triangle, triangle.getVertexA(), triangle.getVertexC(), 10);
        FuzzyLite.logger().log(Level.INFO, discrete.toString());

        FuzzyLite.logger().log(Level.INFO, "X=[{0}]", Op.join(discrete.x(), ","));
        for (int i = 0; i < 200; ++i) {
            double x = Op.scale(i, 0, 200, -1, 1);
            if (!Op.isEq(triangle.membership(x), discrete.membership(x))) {
                FuzzyLite.setDebugging(true);
                Assert.assertThat(triangle.membership(x), is(discrete.membership(x)));
                FuzzyLite.setDebugging(false);
            }
        }
    }

    @Test
    public void testSorting() {
        FuzzyLite.setLogging(false);
        Triangle triangle = new Triangle("triangle", -1, 1);
        Discrete discrete = Discrete.discretize(triangle,
                triangle.getVertexA(), triangle.getVertexC());

        for (int i = 0; i < discrete.size() - 1; ++i) {
            Assert.assertThat("Term is sorted ascendently",
                    discrete.get(i).x < discrete.get(i + 1).x, is(true));
        }
        List<Double> sortedX = discrete.x();
        Assert.assertThat("jUnit correctly compares instances of lists", sortedX, is(discrete.x()));

        FuzzyLite.logger().log(Level.INFO, discrete.toString());
        Collections.shuffle(discrete);
        Assert.assertThat("jUnit correctly compares instances of lists", sortedX, not(is(discrete.x())));

        FuzzyLite.logger().log(Level.INFO, "Shuffle: {0}", discrete.toString());
        Collections.sort(discrete, Discrete.ASCENDENTLY);
        FuzzyLite.logger().log(Level.INFO, "LowerBound: {0}", discrete.toString());

        Assert.assertThat("lower bound sorts correctly", sortedX, is(discrete.x()));
    }

    @Test
    public void testDiscretizationOfAllTerms() {
        double min = -1.0;
        double max = 1.0;
        double range = max - min;
        double mean = 0.5 * (max + min);
        List<Term> terms = new ArrayList<Term>();
        terms.add(new Triangle("triangle", min, mean, max));
        terms.add(new Trapezoid("trapezoid", min, min + .25 * range, min + .75 * range, max));
        terms.add(new Rectangle("rectangle", min, max));
        terms.add(new Bell("bell", mean, range / 4, 3.0));
        terms.add(new Cosine("cosine", mean, range));
        terms.add(new Gaussian("gaussian", mean, range / 4));
        terms.add(new GaussianProduct("gaussianProduct", mean, range / 4, mean, range / 4));
        terms.add(new PiShape("piShape", min, mean, mean, max));
        terms.add(new SigmoidDifference("sigmoidDifference", min + .25 * range, 20 / range, 20 / range, max - .25 * range));
        terms.add(new SigmoidProduct("sigmoidProduct", min + .25 * range, 20 / range, 20 / range, max - .25 * range));
        terms.add(new Spike("spike", mean, range));

        terms.add(new Binary("binary", min, max));
        terms.add(new Concave("concave", mean, max));
        terms.add(new Ramp("ramp", min, max));
        terms.add(new Sigmoid("sigmoid", mean, 20 / range));
        terms.add(new SShape("sshape", min, max));
        terms.add(new ZShape("zshape", min, max));

        //Random causes more inaccurate values when interpolating
        Random random = new Random(0);
        for (Term term : terms) {
            List<Discrete.Pair> pairs = new ArrayList<Discrete.Pair>();
            for (int i = 0; i < 1000; ++i) {
                double x = Op.scale(random.nextInt(100), 0, 100, -1, 1);
                pairs.add(new Discrete.Pair(x, term.membership(x)));
            }
            Discrete.sort(pairs);

            Discrete discrete = new Discrete("discrete", pairs);
            for (Discrete.Pair pair : discrete) {
                double x = pair.x;
                if (!Op.isEq(discrete.membership(x), term.membership(x))) {
                    FuzzyLite.setDebugging(true);
                    Assert.assertThat(discrete.membership(x), is(term.membership(x)));
                    FuzzyLite.setDebugging(false);
                }
            }

            for (int i = 0; i < 100; ++i) {
                double x = Op.scale(i, 0, 100, -1, 1);
                if (!Op.isEq(discrete.membership(x), term.membership(x))) {
                    FuzzyLite.setDebugging(true);
                    Assert.assertThat(discrete.membership(x), is(term.membership(x)));
                    FuzzyLite.setDebugging(false);
                }
            }
        }
    }

}
