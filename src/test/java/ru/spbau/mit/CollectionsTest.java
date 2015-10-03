package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by equi on 29.09.15.
 *
 * @author Kravchenko Dima
 */
public class CollectionsTest {
    private static final List<Integer> ARR = Arrays.asList(1, 2, 3, 4, 5);

    private static Function1<Integer, Integer> multByTwo =
            new Function1<Integer, Integer>() {
                @Override
                public Integer apply(Integer arg) {
                    return arg * 2;
                }
            };

    private static Predicate<Integer> isEven =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer arg) {
                    return arg % 2 == 0;
                }
            };

    private static Function2<Integer, Integer, Integer> pow =
            new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer arg1, Integer arg2) {
                    int x = 1;
                    for (int i = 0; i < arg2; i++) {
                        x *= arg1;
                    }
                    return x;
                }
            };

    @Test
    public void testCollectionsMap() {
        ArrayList<Integer> res = new ArrayList<>();
        Collections.map(multByTwo, ARR, res);
        for (int i = 0; i < res.size(); i++) {
            assertEquals((long) res.get(i), 2 * ARR.get(i));
        }
    }

    @Test
    public void testCollectionsFilter() {
        ArrayList<Integer> res = new ArrayList<>();
        Collections.filter(isEven, ARR, res);
        assertTrue(res.size() == 2);
        assertTrue(res.get(0) == 2);
        assertTrue(res.get(1) == 4);
    }

    @Test
    public void testCollectionsTakeWhile() {
        ArrayList<Integer> res = new ArrayList<>();
        Collections.takeWhile(isEven.not(), ARR, res);
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == 1);
    }

    @Test
    public void testCollectionsTakeUnless() {
        ArrayList<Integer> res = new ArrayList<>();
        Collections.takeUnless(isEven, ARR, res);
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == 1);
    }

    @Test
    public void testCollectionsFoldl() {
        final List<Integer> ARR = Arrays.asList(1, 2, 3);
        int res = Collections.foldl(pow, 2, ARR);
        assertTrue(res == 64); // ((2^1)^2)^3 = 2^(1 * 2 * 3) = 2^6
    }

    @Test
    public void testCollectionsFoldr() {
        final List<Integer> ARR = Arrays.asList(1, 2, 3);
        int res = Collections.foldr(pow , 2, ARR);
        assertTrue(res == 1); // 1^(2^(3^2)) = 1^512 = 1
    }
}
