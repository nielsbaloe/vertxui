package live.connector.vertxui.streamy;

public interface Pred<T> {

	boolean test(T t);

}