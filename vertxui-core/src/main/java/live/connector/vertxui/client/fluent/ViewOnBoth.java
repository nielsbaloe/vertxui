package live.connector.vertxui.client.fluent;

import java.util.function.BiFunction;

/**
 * A view of two states.
 *
 */
public class ViewOnBoth<A, B> extends ViewOnBase {

	private A state1;
	private B state2;
	private BiFunction<A, B, Fluent> translate;

	public ViewOnBoth(A state1, B state2, BiFunction<A, B, Fluent> translate) {
		this.state1 = state1;
		this.state2 = state2;
		this.translate = translate;
	}

	protected Fluent generate(Fluent parent) {
		this.parent = parent;
		Fluent result = Fluent.getRootOf(translate.apply(state1, state2));
		this.view = result;
		return result;
	}

	/**
	 * Get the current state, in case it was easier not to keep a reference to
	 * it yourself.
	 */
	public A state1() {
		return state1;
	}

	public B state2() {
		return state2;
	}

	public ViewOnBase state1(A state1) {
		this.state1 = state1;
		return sync();
	}

	public ViewOnBase state2(B state2) {
		this.state2 = state2;
		return sync();
	}

	/**
	 * Set the current state and sync(). You can also keep the state yourself,
	 * change it and call sync();
	 */
	public ViewOnBase state(A state1, B state2) {
		this.state1 = state1;
		this.state2 = state2;
		return sync();
	}

	public ViewOnBoth<A, B> clone() {
		if (view != null) {
			throw new IllegalArgumentException("Can not clone if it is DOM-attached");
		}
		return new ViewOnBoth<A, B>(state1, state2, translate);
	}

}
