package ru.spbau.mit;

/**
 * Created by equi on 26.09.15.
 *
 * @author Kravchenko Dima
 */
public abstract class Function2<T1, T2, R> {
    public abstract R apply(T1 arg1, T2 arg2);

    public <R2> Function2<T1, T2, R2> compose(final Function1<? super R, ? extends R2> outer) {
        final Function2<T1, T2, R> inner = this;

        return new Function2<T1, T2, R2>() {
            @Override
            public R2 apply(T1 newArg1, T2 newArg2) {
                return outer.apply(inner.apply(newArg1, newArg2));
            }
        };
    }

    public Function1<T2, R> bind1(final T1 arg1) {
        return new Function1<T2, R>() {
            @Override
            public R apply(T2 arg2) {
                return Function2.this.apply(arg1, arg2);
            }
        };
    }

    public Function1<T1, R> bind2(final T2 arg2) {
        return new Function1<T1, R>() {
            @Override
            public R apply(T1 arg1) {
                return Function2.this.apply(arg1, arg2);
            }
        };
    }

    public Function1<T1, Function1<T2, R>> curry() {
        return new Function1<T1, Function1<T2, R>>() {
            @Override
            public Function1<T2, R> apply(T1 arg1) {
                return Function2.this.bind1(arg1);
            }
        };
    }
}
