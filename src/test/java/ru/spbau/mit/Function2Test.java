package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

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
            @Overrinde
            public Integer apply(Integer a, Integer b) {
                return a + b;
            }
        };

    @Test
    public void testFunction1Apply() {
        assertTrue(10 == sum.apply(2, 8));
    }

    @Test
    public void testFunction1Compose() {
    }

}
