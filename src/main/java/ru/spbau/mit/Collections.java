package ru.spbau.mit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
public class Collections {
    public static <T, R> void map(Function1<? super T, R> f,
                                  final Iterable<T> collection,
                                  Collection<? super R> result) {
        for (T element : collection) {
            result.add(f.apply(element));
        }
    }

    public static <T> void filter(Predicate<? super T> p,
                                  final Iterable<T> collection,
                                  Collection<? super T> result) {
        for (T element : collection) {
            if (p.apply(element)) {
                result.add(element);
            }
        }
    }

    private static <T> void take(boolean b,
                                 Predicate<? super T> p,
                                 final Iterable<T> collection,
                                 Collection<? super T> result) {
        for (T element : collection) {
            if (p.apply(element) == b) {
                result.add(element);
            } else {
                break;
            }
        }
    }

    public static <T> void takeWhile(Predicate<? super T> p,
                                     final Iterable<T> collection,
                                     Collection<? super T> result) {
        take(true, p, collection, result);
    }

    public static <T> void takeUnless(Predicate<? super T> p,
                                     final Iterable<T> collection,
                                     Collection<? super T> result) {
        take(false, p, collection, result);
    }

    public static <T1, T2> T1 foldl(Function2<? super T1, ? super T2, ? extends T1> f,
                           T1 value,
                           final Iterable<T2> collection) {
        for (T2 element : collection) {
            value = f.apply(value, element);
        }
        return value;
    }

    public static <T1, T2> T2 foldr(Function2<? super T1, ? super T2, ? extends T2> f,
                                    T2 value,
                                    final Iterable<T1> collection) {
        ArrayList<T1> arr = new ArrayList<>();
        for (T1 element : collection) {
            arr.add(element);
        }
        for (int i = arr.size() - 1; i >= 0; i--) {
            value = f.apply(arr.get(i), value);
        }
        return value;
    }
}
