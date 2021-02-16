package my.utils;

@FunctionalInterface
public interface LetHandler<T> {

    void handle(T t);

}