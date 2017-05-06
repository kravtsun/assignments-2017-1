package ru.spbau.mit;

public abstract class Function1<T, S> {
    public <G> Function1<T, G> compose(final Function1<? super S, G> g) {
        return new Function1<T, G>() {
            @Override
            public G apply(T arg) {
                return g.apply(Function1.this.apply(arg));
            }
        };
    }

    public abstract S apply(T arg);
}
