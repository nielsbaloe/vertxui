package live.connector.vertxui.client.fluent;

public abstract class ViewOnBase implements Viewable {

	protected Fluent view;

	protected Fluent parent;

	/**
	 * Set the display parameter of the view (if any) to none.
	 */
	public void hide() {
		if (view != null) {
			view.css(Css.display, "none");
		}
	}

	/**
	 * Set the display parameter of the view (if any) to null.
	 * 
	 */
	public void unhide() {
		if (view != null) {
			view.css(Css.display, null);
		}
	}

	protected void setParent(Fluent parent) {
		this.parent = parent;
	}

	public Fluent getView() {
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

	@Override
	public int getCrc() {
		if (view != null) {
			return view.getCrc();
		} else {
			return 0;
		}
	}

	public ViewOnBase sync() {
		// the 'if' below prevents throwing away inner viewOn's with a outer
		// state. Because, if sync is called fromout fluent when adding inside a
		// viewon, the attached dom is thrown away, which is not supposed to
		// happen.
		if (parent.element != null) {
			Renderer.syncChild(parent, this, view);
		}
		return this;
	}

}
