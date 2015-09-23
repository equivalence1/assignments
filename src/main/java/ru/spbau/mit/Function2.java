package ru.spbau.mit;

abstract class Function2<T1, T2, R> {
    public abstract R apply(T1 arg1, T2 arg2);

    public <R2> Function1<T extends R, R2> compose(final Function1<T extends R, R2> outer) {
        final Function2<T1, T2, R> inner = this;

        return new Function1<T extends R, R2>() {
            @Override
            public R2 apply(T1 newArg1, T2 newArg2) {
                return outer.apply(inner.apply(newArg1, newArg2));
            }
        };
    }
}
