package my.utils;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
public final class Utils {

    public static <T> T requireNotNull(T t) {
        if (t == null) throw new NullPointerException("Variable was null, though is was required to be not null");
        return t;
    }

    public static <T> T substituteIfNull(T original, T substitute) {
        return original == null ? substitute : original;
    }

    public static List<String> readFile(File file) {
        requireNotNull(file);
        return runCatching(() -> Files.lines(file.toPath()).collect(Collectors.toList()));
    }

    public static <T> void let(T t, LetHandler<T> handler) {
        handler.handle(t);
    }

    public static <T> T also(T t, LetHandler<T> handler) {
        handler.handle(t);
        return t;
    }

    @SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
    public static void discard(Object object) {
        return;
    }

    public static <T extends Closeable> void with(T t, LetHandler<T> handler) {
        handler.handle(t);
        runCatching((CatchHandler<Void>) () -> {
            t.close();
            return null;
        });
    }

    public static <T> T runIf(Condition c, ReturningRunnable<T> r) {
        if (c.check()) return r.run();
        return null;
    }

    public static <T> T runIfElse(Condition c, ReturningRunnable<T> t, ReturningRunnable<T> f) {
        if (c.check()) return t.run();
        else return f.run();
    }

    public static void repeat(int times, Runnable toRun) {
        for (int i = 0; i < times; i++) {
            toRun.run();
        }
    }

    public static void repeat(int times, IndexedRunnable toRun) {
        for (int i = 0; i < times; i++) {
            toRun.run(i);
        }
    }

    public static <T> T runCatching(CatchHandler<T> handler) {
        try {
            return handler.handle();
        } catch (Exception e) {
            return null;
        }
    }

    public static void ignoreAllExceptions() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        });
    }

    public static Object[] reduceArrayAsNeeded(Object[] arr, int maxSize, REDUCTION_METHOD method) {
        if (arr.length <= maxSize) return arr;
        Object[] reduced = new Object[maxSize];
        switch (method) {
            case FIRST:
                System.arraycopy(arr, 0, reduced, 0, maxSize);
                break;
            case LAST:
                System.arraycopy(arr, arr.length - maxSize, reduced, 0, maxSize);
                break;
            case RANDOM:
                repeat(reduced.length, (index -> reduced[index] = pickRandomFromArray(arr)));
                break;
        }
        return reduced;
    }

    public static <T> T pickRandomFromArray(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(0, array.length)];
    }

    public static <T> T[] fillArray(int size, Class<T> clazz, FillHandler<T> handler) {
        final T[] array = (T[]) java.lang.reflect.Array.newInstance(clazz, size);
        for (int i = 0; i < array.length; i++) {
            final int finalI = i;
            array[i] = handler.fill(i, runCatching(() -> array[finalI - 1]));
        }
        return array;
    }

    public static <T> T[] arrayOf(Class<T> clazz, T... ts) {
        T[] array = (T[]) Array.newInstance(clazz, ts.length);
        System.arraycopy(ts, 0, array, 0, ts.length);
        return array;
    }

    public enum REDUCTION_METHOD {
        FIRST,
        LAST,
        RANDOM
    }
}