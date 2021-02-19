package my.utils.interfaces;

@FunctionalInterface
public interface ReturningRunnable<T> {

    T run();
}