package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
public class Function2Test {

    private static Function1<Integer, Integer> addTen =
            new Function1<Integer, Integer>() {
                @Override
                public Integer apply(Integer num) {
                    return num + 10;
                }
            };

    private static Function2<Integer, Integer, Integer> sum = 
            new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer a, Integer b) {
                    return a + b;
                }
            };

    @Test
    public void testFunction2Apply() {
        assertTrue(10 == sum.apply(2, 8));
    }

    @Test
    public void testFunction2Compose() {
        Function2<Integer, Integer, Integer> comp = sum.compose(addTen);
        assertTrue(20 == comp.apply(4, 6));
    }

    @Test
    public void testFunction2Bind1() {
        assertTrue(21 == sum.bind1(10).apply(11));
    }

    @Test
    public void testFunction2Bind2() {
        assertTrue(21 == sum.bind2(10).apply(11));
    }

    @Test
    public void testFunction2Curry() {
        assertTrue(21 == sum.curry().apply(10).apply(11));
    }
}
