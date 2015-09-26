package ru.spbau.mit;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
abstract public class Predicate<T> {
    public static boolean ALWAYS_TRUE = true;
    public static boolean ALWAYS_FALSE = false;

    public abstract boolean apply(T arg);

    public Predicate<T> or(final Predicate<? super T> first) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T arg) {
                return first.apply(arg) || Predicate.this.apply(arg);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> first) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T arg) {
                return first.apply(arg) && Predicate.this.apply(arg);
            }
        };
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T arg) {
                return !Predicate.this.apply(arg);
            }
        };
    }
}
