package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
public class Function1Test {

    private static Function1<Integer, Integer> addTen =
            new Function1<Integer, Integer>() {
                @Override
                public Integer apply(Integer num) {
                    return num + 10;
                }
            };

    @Test
    public void testFunction1Apply() {
        assertTrue(10 == addTen.apply(0));
        assertTrue(20 == addTen.apply(10));
    }

    @Test
    public void testFunction1Compose() {
        Function1<Integer, Integer> addTwenty =
                new Function1<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer num) {
                        return num + 20;
                    }
                };
        assertTrue(50 == addTen.compose(addTwenty).apply(20));
        assertTrue(50 == addTwenty.compose(addTen).apply(20));
    }

}
