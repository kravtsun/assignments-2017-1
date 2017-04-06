package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean> {
    @SuppressWarnings("unchecked")
    public static final Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public Boolean apply(Object arg) {
            return true;
        }
    };

    public static final Predicate ALWAYS_FALSE = new Predicate() {
        @Override
        public Boolean apply(Object arg) {
            return false;
        }
    };
    @SuppressWarnings("checked")

    public Predicate<T> and(final Predicate<? super T> rhs) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                if (Predicate.this.apply(arg)) {
                    return rhs.apply(arg);
                } else {
                    return false;
                }
            }
        };
    }

    public Predicate<T> or(final Predicate<? super T> rhs) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                if (Predicate.this.apply(arg)) {
                    return true;
                } else {
                    return rhs.apply(arg);
                }
            }
        };
    }

    public Predicate not() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return !Predicate.this.apply(arg);
            }
        };
    }

    @Override
    public abstract Boolean apply(T arg);
}
