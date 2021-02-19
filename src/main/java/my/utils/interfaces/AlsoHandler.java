package my.utils.interfaces;

@FunctionalInterface
public interface AlsoHandler<T> {

    T handle(T t);
}