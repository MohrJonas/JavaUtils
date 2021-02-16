package my.utils;

@FunctionalInterface
public interface CatchHandler<T> {

    T handle() throws Exception;

}