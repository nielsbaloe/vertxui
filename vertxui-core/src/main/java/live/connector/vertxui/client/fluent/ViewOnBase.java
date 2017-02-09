package live.connector.vertxui.client.fluent;

import com.google.gwt.core.shared.GWT;

/**
 * A base class for descriptive lambda-views.
 * 
 * @author Niels Gorisse
 *
 */
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

	@Override
	public String getCrcString() {
		if (view != null) {
			return view.getCrcString();
		} else {
			return "";
		}
	}

	public ViewOnBase sync() {
		// the 'if' below prevents throwing away inner viewOn's with a outer
		// state. Because, if sync is called fromout fluent when adding inside a
		// viewon, the attached dom is thrown away, which is not supposed to
		// happen.
		// However, it prevents testing against the virtual dom, so we leave it
		// on in pure java.
		if (!GWT.isClient() || (parent != null && parent.element != null)) {
			Renderer.syncChild(parent, this, view);
		}
		return this;
	}

}
