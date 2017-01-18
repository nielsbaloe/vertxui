package live.connector.vertxui.client.fluent;

import elemental.dom.Element;

/**
 * A view of a state.
 *
 */
public class ViewOn<A> implements Viewable {
	private A state;
	private Function<A, Fluent> translate;
	private Fluent view;
	private Fluent parent;

	public ViewOn(A state, Function<A, Fluent> translate) {
		this.state = state;
		this.translate = translate;
	}

	protected Fluent generate(Fluent parent) {
		this.parent = parent;
		Fluent result = translate.handle(state);
		this.view = result;
		return result;
	}

	/**
	 * Get the current state, in case it was easier not to keep a reference to
	 * it yourself.
	 */
	public A state() {
		return state;
	}

	/**
	 * Set the current state and sync(). You can also keep the state yourself,
	 * change it and call sync();
	 */
	public ViewOn<A> state(A state) {
		this.state = state;
		return sync();
	}

	protected void setParent(Fluent parent) {
		this.parent = parent;
	}

	protected Fluent getView() {
		return view;
	}

	public ViewOn<A> sync() {
		Renderer.syncChild(parent, this, view);
		return this;
	}

	public static interface Function<I, O> {
		public O handle(I i);
	}

	public Fluent getViewForDebugPurposesOnly() {
		return view;
	}

	public Element getTheCurrentViewForDebugPurposesOnly() {
		return view.element;
	}

}
