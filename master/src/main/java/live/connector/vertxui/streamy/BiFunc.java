package live.connector.vertxui.streamy;

public interface BiFunc<T, U, R> {

	R apply(T t, U u);

	default <V> BiFunc<T, U, V> andThen(Func<? super R, ? extends V> after) {
		if (after == null) {
			throw new NullPointerException();
		}
		return (T t, U u) -> after.apply(apply(t, u));
	}

}