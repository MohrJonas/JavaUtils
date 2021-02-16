package my.utils;

@FunctionalInterface
public interface FillHandler<T> {

    T fill(int index, T previous);

}