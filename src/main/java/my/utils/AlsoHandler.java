package my.utils;

@FunctionalInterface
public interface AlsoHandler<T> {

    T handle(T t);
}