package ru.spbau.mit;

import java.util.Objects;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
public abstract class Predicate<T> extends Function1<T, Boolean> {
    public static final Predicate<Object> ALWAYS_TRUE =
            new Predicate<Object>() {
                @Override
                public Boolean apply(Object arg) {
                    return true;
                }
            };
    public static final Predicate<Object> ALWAYS_FALSE =
            new Predicate<Object>() {
                @Override
                public Boolean apply(Object arg) {
                    return false;
                }
            };

    public Predicate<T> or(final Predicate<? super T> second) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return Predicate.this.apply(arg) || second.apply(arg);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> second) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return Predicate.this.apply(arg) && second.apply(arg);
            }
        };
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return !Predicate.this.apply(arg);
            }
        };
    }
}
