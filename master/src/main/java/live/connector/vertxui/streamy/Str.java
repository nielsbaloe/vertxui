package live.connector.vertxui.streamy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * replaces Java 8: Stream. A fake Java 8 Stream compatible ini-mini-mini
 * stream-ish set which has no dependencies so compiles easily under TeaVM. Can
 * be replaced by class Stream when TeaVM can do it, see the javadoc.
 * 
 * @author ng
 *
 * @param <T>
 */
public class Str<S> {

	private List<S> collection;

	private Str(List<S> list) {
		collection = list;
	}

	/**
	 * replaces Java 8: yourArrayList.stream()
	 */
	public static <T> Str<T> eam(List<T> ts) {
		return new Str<T>(ts);
	}

	/**
	 * replaces Java 8: Array.stream(yourList)
	 */
	public static <T> Str<T> eam(T[] values) {
		List<T> collection = new ArrayList<>();
		for (T value : values) {
			collection.add(value);
		}
		return new Str<T>(collection);
	}

	@SafeVarargs
	public static <T> Str<T> eamd(T... values) {
		List<T> collection = new ArrayList<>();
		for (T value : values) {
			collection.add(value);
		}
		return new Str<T>(collection);
	}

	public <R> Str<R> map(Func<? super S, ? extends R> mapper) {
		List<R> result = new ArrayList<>();
		for (S t : collection) {
			result.add(mapper.apply(t));
		}
		return new Str<R>(result);
	}

	public Str<S> filter(Pred<? super S> predicate) {
		List<S> result = new ArrayList<>();
		for (S t : collection) {
			if (predicate.test(t)) {
				result.add(t);
			}
		}
		return new Str<S>(result);
	}

	public void forEach(Consu<? super S> consumer) {
		for (S t : collection) {
			consumer.accept(t);
		}
	}

	public static <T> Str<T> concat(Str<? extends T> a, Str<? extends T> b) {
		List<T> result = new ArrayList<>();
		for (T t : a.collection) {
			result.add(t);
		}
		for (T t : b.collection) {
			result.add(t);
		}
		return new Str<T>(result);
	}

	/**
	 * replaces Java 8: .collect(Collectors.toList()));
	 */
	public List<S> collectToList() {
		return collection;
	}

	public Str<S> skip(int x) {
		return new Str<S>(collection.subList(x, collection.size()));
	}

	public Str<S> take(int x) {
		return new Str<S>(collection.subList(0, Math.min(x, collection.size())));
	}

	public S reduce(S identity, BiOp<S> accumulator) {
		S result = identity;
		for (S t : collection) {
			accumulator.apply(result, t);
		}
		return result;
	}

	/**
	 * replaces Java 8: reduce(...).get();
	 */
	public S reduceGet(BiOp<S> accumulator) {
		boolean foundAny = false;
		S result = null;
		for (S element : collection) {
			if (!foundAny) {
				foundAny = true;
				result = element;
			} else {
				result = accumulator.apply(result, element);
			}
		}
		return foundAny ? result : null;

	}

	public Str<S> sorted(Comparator<? super S> comparator) {
		List<S> result = new ArrayList<>(collection);
		result.sort(comparator);
		return new Str<S>(result);
	}

	public Str<S> sorted() {
		return sorted(null);
	}

	public Str<S> peek(Consu<? super S> action) {
		forEach(action);
		return this;
	}

}
