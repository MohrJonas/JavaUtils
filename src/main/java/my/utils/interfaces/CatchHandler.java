package my.utils.interfaces;

@FunctionalInterface
public interface CatchHandler<T> {

    T handle() throws Exception;

}