package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by equi on 29.09.15.
 *
 * @author Kravchenko Dima
 */
public class CollectionsTest {
    private static final ArrayList<Integer> ARR =
            new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

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
                public boolean apply(Integer arg) {
                    return arg % 2 == 0;
                }
            };

    private static final Collections COLLECT_INSTANCE = new Collections();

    @Test
    public void testCollectionsMap() {
        ArrayList<Integer> res = new ArrayList<>();
        COLLECT_INSTANCE.map(multByTwo, ARR, res);
        for (int i = 0; i < res.size(); i++) {
            assertTrue(res.get(i) == 2 * ARR.get(i));
        }
    }

    @Test
    public void testCollectionsFilter() {
        ArrayList<Integer> res = new ArrayList<>();
        COLLECT_INSTANCE.filter(isEven, ARR, res);
        assertTrue(res.size() == 2);
        assertTrue(res.get(0) == 2);
        assertTrue(res.get(1) == 4);
    }

    @Test
    public void testCollectionsTakeWhile() {
        ArrayList<Integer> res = new ArrayList<>();
        COLLECT_INSTANCE.takeWhile(isEven.not(), ARR, res);
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == 1);
    }

    @Test
    public void testCollectionsTakeUnless() {
        ArrayList<Integer> res = new ArrayList<>();
        COLLECT_INSTANCE.takeUnless(isEven, ARR, res);
        assertTrue(res.size() == 1);
        assertTrue(res.get(0) == 1);
    }

    @Test
    public void testCollectionsFoldl() {
        Function2<String, Integer, String> f =
                new Function2<String, Integer, String>() {
                    @Override
                    public String apply(String arg1, Integer arg2) {
                        return arg1 + arg2;
                    }
                };
        String res = COLLECT_INSTANCE.foldl(f, "", ARR);
        assertTrue(res.equals("12345"));
    }

    @Test
    public void testCollectionsFoldr() {
        Function2<Integer, String, String> f =
                new Function2<Integer, String, String>() {
                    @Override
                    public String apply(Integer arg1, String arg2) {
                        return arg1 + arg2;
                    }
                };
        String res = COLLECT_INSTANCE.foldr(f, "", ARR);
        assertTrue(res.equals("12345"));
    }
}
