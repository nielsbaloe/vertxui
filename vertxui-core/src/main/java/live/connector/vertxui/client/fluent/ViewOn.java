package live.connector.vertxui.client.fluent;

import java.util.function.Function;

/**
 * A view of a state.
 *
 */
public class ViewOn<A> extends ViewOnBase {

	private A state;
	private Function<A, Fluent> translate;

	public ViewOn(A state, Function<A, Fluent> translate) {
		this.state = state;
		this.translate = translate;
	}

	protected Fluent generate(Fluent parent) {
		this.parent = parent;
		Fluent result = Fluent.getRootOf(translate.apply(state));
		this.view = result;
		return result;
	}

	/**
	 * Get the current state, in case it was easier not to keep a reference to
	 * it yourself.
	 * 
	 * @return the state
	 */
	public A state() {
		return state;
	}

	/**
	 * Set the current state and sync(). You can also keep the state yourself,
	 * change it and call sync();
	 * 
	 * @param state
	 *            the new state
	 * @return this
	 */
	public ViewOnBase state(A state) {
		this.state = state;
		return sync();
	}

	public ViewOn<A> clone() {
		if (view != null) {
			throw new IllegalArgumentException("Can not clone if it is DOM-attached");
		}
		return new ViewOn<A>(state, translate);
	}

}
