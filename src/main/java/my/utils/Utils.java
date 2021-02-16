package my.utils;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked", "UnnecessaryReturnStatement"})
public final class Utils {

    /**
     * Returns the given parameter t if not null,
     * otherwise throw an Exception
     *
     * @param t the object to check
     * @return t
     * @throws NullPointerException if the given value is null
     */
    public static <T> T requireNotNull(T t) {
        if (t == null) throw new NullPointerException("Variable was null, though is was required to be not null");
        return t;
    }

    /**
     * Returns the original if not null,
     * otherwise returns the substitute
     *
     * @param original   The original value that is return if not null
     * @param substitute The value original is substituted for if null
     * @return original or substitute
     */
    public static <T> T substituteIfNull(T original, T substitute) {
        return original == null ? substitute : original;
    }

    /**
     * Reads the given file as lines of text
     *
     * @param file the file to read
     * @return A list of lines
     * @throws NullPointerException if the given file is null
     */
    public static List<String> readFile(File file) {
        requireNotNull(file);
        return runCatching(() -> Files.lines(file.toPath()).collect(Collectors.toList()));
    }

    /**
     * Like the Kotlin let
     * Docs: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/let.html
     */
    public static <T> void let(T t, LetHandler<T> handler) {
        handler.handle(t);
    }

    /**
     * Like the Kotlin also
     * Docs: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/also.html
     */
    public static <T> T also(T t, LetHandler<T> handler) {
        handler.handle(t);
        return t;
    }

    /**
     * Discards the given object.
     * Can be used to discard unwanted return-values
     *
     * @param object the object to discard
     */
    public static void discard(Object object) {
        return;
    }

    /**
     * Runs the  given {@link LetHandler} with the closable @param t as argument
     * and closes the {@link Closeable} automatically
     *
     * @param t       the {@link Closeable} to close
     * @param handler the {@link LetHandler} to invoke
     */
    public static <T extends Closeable> void with(T t, LetHandler<T> handler) {
        handler.handle(t);
        runCatching((CatchHandler<Void>) () -> {
            t.close();
            return null;
        });
    }

    /**
     * Runs the given {@link ReturningRunnable} if the {@link Condition} is true and returns its output.
     * Otherwise return null
     *
     * @param c the condition that will be checked
     * @param r the {@link ReturningRunnable} that will be run
     * @return the return of r if c is true, otherwise null
     */
    public static <T> T runIf(Condition c, ReturningRunnable<T> r) {
        if (c.check()) return r.run();
        return null;
    }

    /**
     * Runs {@param t} is {@param c} is true, otherwise {@param f}
     *
     * @param c the condition to check
     * @param t the {@link ReturningRunnable} to run if c is true
     * @param f the {@link ReturningRunnable} to run if c is false
     * @return t's return if c is true, otherwise f's return
     */
    public static <T> T runIfElse(Condition c, ReturningRunnable<T> t, ReturningRunnable<T> f) {
        if (c.check()) return t.run();
        else return f.run();
    }

    /**
     * Repeats the given {@link Runnable} {@param times} times
     *
     * @param times the times to run the given command
     * @param toRun the runnable to run
     */
    public static void repeat(int times, Runnable toRun) {
        for (int i = 0; i < times; i++) {
            toRun.run();
        }
    }

    /**
     * Repeats the given {@link IndexedRunnable} {@param times} times with the current cycle-number as index
     *
     * @param times the times to run the given command
     * @param toRun the runnable to run
     */
    public static void repeat(int times, IndexedRunnable toRun) {
        for (int i = 0; i < times; i++) {
            toRun.run(i);
        }
    }

    /**
     * Run the given {@link CatchHandler}, returning the {@link Exception} if one was thrown, otherwise null
     *
     * @param handler the {@link CatchHandler}
     * @return the {@link Exception} if one was thrown, otherwise null
     */
    public static <T> T runCatching(CatchHandler<T> handler) {
        try {
            return handler.handle();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Ignore all {@link Exception} that are thrown after calling this command.
     */
    public static void ignoreAllExceptions() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        });
    }

    /**
     * Reduce the given Array to a new size, using one of tree methods
     * If the array is smaller than the new max-size, just return it
     *
     * @param arr     the array to reduce
     * @param maxSize the size of the new array
     * @param method  the {@link REDUCTION_METHOD} to use
     * @return the new Array
     */
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

    /**
     * Return a random element from the given Array
     *
     * @param array the array to pick from
     * @return the picked element
     * @throws IllegalArgumentException if the given Array is empty
     */
    public static <T> T pickRandomFromArray(T[] array) {
        if (array.length == 0) throw new IllegalArgumentException("Array can't be null");
        return array[ThreadLocalRandom.current().nextInt(0, array.length)];
    }

    /**
     * Create a new Array of specific size by calling the given {@link FillHandler} on each element
     *
     * @param size    the size of the array
     * @param clazz   the class of the elements in the array
     * @param handler the handler to call on each element
     * @return the built array
     */
    public static <T> T[] fillArray(int size, Class<T> clazz, FillHandler<T> handler) {
        final T[] array = (T[]) java.lang.reflect.Array.newInstance(clazz, size);
        for (int i = 0; i < array.length; i++) {
            final int finalI = i;
            array[i] = handler.fill(i, runCatching(() -> array[finalI - 1]));
        }
        return array;
    }

    /**
     * Create a new Array of the given elements
     *
     * @param clazz the class of the elements in the array
     * @param ts    the object to build the array from
     * @return the build array
     */
    public static <T> T[] arrayOf(Class<T> clazz, T... ts) {
        T[] array = (T[]) Array.newInstance(clazz, ts.length);
        System.arraycopy(ts, 0, array, 0, ts.length);
        return array;
    }

    /**
     * Methods to use on the reduceArrayAsNeeded methode
     * FIRST -> take the first n elements from the array
     * LAST -> take the last n elements from the array
     * RANDOM -> take n random elements frm the array
     */
    public enum REDUCTION_METHOD {
        FIRST,
        LAST,
        RANDOM
    }
}