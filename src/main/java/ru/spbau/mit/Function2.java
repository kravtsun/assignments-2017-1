package ru.spbau.mit;

// extends Function1<T1, Function1<T2, S>>
public abstract class Function2<T1, T2, S> {
    public Function1<T1, Function1<T2, S>> curry() {
        return new Function1<T1, Function1<T2, S>>() {
            @Override
            public Function1<T2, S> apply(T1 arg1) {
                return Function2.this.bind1(arg1);
            }
        };
    }

    // because of compose can't extend from Function1
    public <S2> Function2<T1, T2, S2> compose(final Function1<? super S, S2> g) {
        return new Function2<T1, T2, S2>() {
              public S2 apply2(T1 arg1, T2 arg2) {
                  return g.apply(Function2.this.apply2(arg1, arg2));
              }
        };
    }

    public Function1<T2, S> bind1(final T1 arg1) {
        return new Function1<T2, S>() {
            @Override
            public S apply(T2 arg2) {
                return apply2(arg1, arg2);
            }
        };
    }

    public Function1<T1, S> bind2(final T2 arg2) {
        return new Function1<T1, S>() {
            @Override
            public S apply(T1 arg1) {
                return apply2(arg1, arg2);
            }
        };
    }

    public abstract S apply2(T1 arg1, T2 arg2);
}
