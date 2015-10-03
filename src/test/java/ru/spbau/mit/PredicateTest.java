package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
public class PredicateTest {
    private static Predicate<Integer> isPositive =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer x) {
                    return x > 0;
                }
            };

    private static Predicate<Integer> isEven =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer x) {
                    return x % 2 == 0;
                }
            };

    @Test
    public void testPredicateApply() {
        assertTrue(isPositive.apply(10));
        assertFalse(isEven.apply(1));
        assertFalse(isPositive.apply(10) && Predicate.ALWAYS_FALSE.apply(1));
        assertTrue(isEven.apply(1) || Predicate.ALWAYS_TRUE.apply(1));
        assertTrue(isEven.compose(Predicate.ALWAYS_TRUE).apply(1));
        assertFalse(isEven.compose(Predicate.ALWAYS_FALSE).apply(1));
    }

    @Test
    public void testPredicateAnd() {
        assertTrue(isPositive.and(isEven).apply(10));
        assertTrue(isEven.and(isPositive).apply(10));
        assertFalse(isPositive.and(isEven).apply(-2));
        assertFalse(isEven.and(isPositive).apply(-2));
        assertFalse(isEven.and(isEven).apply(3));
    }

    @Test
    public void testPredicateOr() {
        assertTrue(isPositive.or(isEven).apply(-2));
        assertTrue(isEven.or(isPositive).apply(1));
        assertFalse(isEven.or(isPositive).apply(-1));
        assertFalse(isEven.or(isEven).apply(1));
        assertTrue(isPositive.or(isEven).apply(1));
    }

    @Test
    public void testPredicateNot() {
        assertFalse(isPositive.not().apply(10));
        assertTrue(isEven.not().apply(1));
    }
}
