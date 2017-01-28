package live.connector.vertxui.client.fluent;

import java.util.function.Function;

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

	/**
	 * Set the display parameter of the view (if any) to none.
	 */
	public void hide() {
		if (view != null) {
			view.css(Style.display, "none");
		}
	}

	/**
	 * Set the display parameter of the view (if any) to null.
	 * 
	 */
	public void unhide() {
		if (view != null) {
			view.css(Style.display, null);
		}
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
		// the 'if' below prevents throwing away inner viewOn's with a outer
		// state. Because, if sync is called fromout fluent when adding inside a
		// viewon, the attached dom is thrown away, which is not supposed to
		// happen.
		if (parent.element != null) {
			Renderer.syncChild(parent, this, view);
		}
		return this;
	}

	public Fluent getCurrentViewForDebugPurposesOnly() {
		return view;
	}

	@Override
	public String toString() {
		String result = "ViewOn{";
		if (view != null) {
			result += view.tag;
		}
		result += "}";
		return result;
	}

	public ViewOn<A> clone() {
		if (view != null) {
			throw new IllegalArgumentException("Can not clone if it is DOM-attached");
		}
		return new ViewOn<A>(state, translate);
	}

}
