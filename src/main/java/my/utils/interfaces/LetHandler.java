package my.utils.interfaces;

@FunctionalInterface
public interface LetHandler<T> {

    void handle(T t);

}