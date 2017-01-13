package live.connector.vertxui.client.fluent;

/**
 * A view of a state.
 *
 */
public class ViewOf<A> implements Viewable {
	private A state;
	private Function<A, Fluent> m;
	private Fluent parent;

	public ViewOf(A state, Function<A, Fluent> m) {
		this.state = state;
		this.m = m;
	}

	protected Fluent generate() {
		return m.handle(state);
	}

	protected void setParent(Fluent parent) {
		this.parent = parent;
	}

	/**
	 * Get the current state, in case it was easier not to keep a reference to
	 * it yourself.
	 */
	public A state() {
		return state;
	}

	/**
	 * Set the current state and sync(). You can also keep the state yourself
	 * and call sync();
	 */
	public ViewOf<A> state(A state) {
		this.state = state;
		return sync();
	}

	public ViewOf<A> sync() {
		Renderer.syncChildren(parent);
		// TODO if we know that there are 0 previous children:
		// Fluent.syncChild(parent, this, null);
		return this;
	}

	public static interface Function<I, O> {
		public O handle(I i);
	}
	
}
