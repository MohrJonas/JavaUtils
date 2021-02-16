package my.utils;

@FunctionalInterface
public interface ReturningRunnable<T> {

    T run();
}