package live.connector.vertxui.streamy;

public interface Func<T, R> {
	R apply(T t);
}