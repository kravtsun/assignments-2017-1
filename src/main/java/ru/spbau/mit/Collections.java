package ru.spbau.mit;

import java.util.ArrayList;
import java.util.Collection;

public final class Collections {
    private Collections() {}

    public static <T, S> Collection<S> map(Function1<? super T, S> f, Collection<T> c) {
        Collection<S> newcol = new ArrayList<S>();
        for (T el : c) {
            newcol.add(f.apply(el));
        }
        return newcol;
    }

    public static <T> Collection<T> filter(Predicate<? super T> pred, Collection<T> c) {
        Collection<T> newcol = new ArrayList<T>();
        for (T el : c) {
            if (pred.apply(el)) {
                newcol.add(el);
            }
        }
        return newcol;
    }

    public static <T> Collection<T> takeWhile(Predicate<? super T> pred, Collection<T> c) {
        Collection<T> newcol = new ArrayList<T>();
        for (T el : c) {
            if (!pred.apply(el)) {
                break;
            }
            newcol.add(el);
        }
        return newcol;
    }

    public static <T> Collection<T> takeUnless(Predicate<? super T> pred, Collection<T> c) {
        return takeWhile(pred.not(), c);
    }


    public static <I, T> I foldl(Function2<I, ? super T, I> f, Collection<T> c, I init) {
        I res = init;
        for (T el : c) {
            res = f.apply2(res, el);
        }
        return res;
    }

    public static <I, T> I foldr(Function2<? super T, I, I> f, Collection<T> c, I init) {
        Function1<I, I> resfunc = new Function1<I, I>() {
            @Override
            public I apply(I arg) {
                return arg;
            }
        };

        for (T el : c) {
            resfunc = f.bind1(el).compose(resfunc);
        }
        return resfunc.apply(init);
    }
}
