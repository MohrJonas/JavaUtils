package my.utils.interfaces;

@FunctionalInterface
public interface FillHandler<T> {

    T fill(int index, T previous);

}