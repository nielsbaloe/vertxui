package live.connector.vertxui.client.fluent;

import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.document;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import elemental.dom.Node;
import elemental.dom.NodeList;

public class Renderer {

	protected static void syncChildren(Fluent parent) {
		if (parent.element == null) { // not attached: no rendering
			return;
		}
		NodeList views = parent.element.getChildNodes();
		for (int x = 0; x < views.getLength() || x < parent.childs.size(); x++) {
			Element view = null;
			if (x < views.length()) {
				view = (Element) views.item(x);
			}
			Viewable child = null;
			if (x < parent.childs.size()) {
				child = parent.childs.get(x);
			}
			console.log("Syncing child=" + child + " to parent=" + parent + " view=" + view + " views="
					+ views.length() + " pChildren=" + parent.childs.size() + " or vsize="
					+ parent.element.getChildNodes().length() + " or vcount=" + parent.element.getChildElementCount());
			syncChild(parent, child, view);
		}
	}

	protected static void syncChild(Fluent parent, Viewable add, Element currentView) {
		console.log("Syncing child=" + add + " to parent=" + parent );
		if (currentView!=null) {
			console.log(" currentView=" + currentView.getInnerHTML());
		}

		if (parent.element == null) { // not attached: no rendering
			return;
		}

		// handle
		if (add == null) {
			if (currentView != null) {
				parent.element.removeChild(currentView);
			}
			return;
		}
		Fluent result = toFluent(add);
		if (currentView == null) {
			console.log("syncCreate because currentView is null for " + result);
			syncCreate(parent, result);
		} else {
			syncRender(parent, result, currentView);
		}
	}

	private static void syncCreate(Fluent parent, Fluent add) {
		console.log("syncCreate starting " + add.tag + " with parent=" + parent);
		add.element = document.createElement(add.tag);
		parent.element.appendChild(add.element);
		add.parent = parent;

		if (add.attrs != null) {
			for (Att name : add.attrs.keySet()) {
				add.element.setAttribute(name.nameValid(), add.attrs.get(name));
			}
		}
		if (add.styles != null) {
			for (Style name : add.styles.keySet()) {
				add.element.getStyle().setProperty(name.nameValid(), add.styles.get(name));
			}
		}
		if (add.inner != null) {
			add.inner(add.inner);
		}
		if (add.listeners != null) {
			for (String name : add.listeners.keySet()) {
				((Node) add.element).addEventListener(name, add.listeners.get(name));
			}
		}
		if (add.childs != null) {
			for (Viewable child : add.childs) {
				syncCreate(add, toFluent(child));
			}
		}
	}

	private static boolean compare(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}

	/**
	 * Check whether the fluenthtml and its element are in sync
	 * 
	 * @param currentView
	 */
	// TODO bijhouden wat er veranderd is, zodat niet heel deze check hoeft te
	// worden doorgevoerd: USE CURRENTVIEW!!!!!!!
	private static void syncRender(Fluent parent, Fluent add, Element currentView) {
		add.element = currentView;
		if (!compare(add.tag, add.element.getTagName())) {
			console.log("syncRender: leuk maar tagname anders");
			parent.element.removeChild(currentView);
			syncCreate(parent, add);
			return;
		}
		add.parent = parent; // this is now our parent
		
		// innerHtml
		if (!compare(add.inner, add.element.getInnerHTML())) {
			add.element.setInnerHTML(add.inner);
		}
		// Attrs
		NamedNodeMap elementAttrs = (NamedNodeMap) add.element.getAttributes();
		if (add.attrs != null) {
			for (Att name : add.attrs.keySet()) { // add or adjust
				String value = add.attrs.get(name);

				Node elementNow = elementAttrs.getNamedItem(name.nameValid());
				if (elementNow == null) {
					add.element.setAttribute(name.nameValid(), value);
				} else if (!elementNow.getNodeValue().equals(value)) {
					elementNow.setNodeValue(value);
				}
			}
		}
		for (int x = elementAttrs.getLength() - 1; x != -1; x--) { // remove
			Node elementAttr = elementAttrs.item(x);
			if (add.attrs == null || !add.attrs.containsKey(Att.valueOfValid(elementAttr.getNodeName()))) {
				elementAttrs.removeNamedItem(elementAttr.getNodeName());
			}
		}
		// Style
		CSSStyleDeclaration elementStyles = add.element.getStyle();
		if (add.styles != null) {
			for (Style name : add.styles.keySet()) { // add or adjust
				String nameValid = name.nameValid();
				String value = add.styles.get(name);

				String elementNow = elementStyles.getPropertyValue(nameValid);
				if (elementNow == null) {
					elementStyles.setProperty(nameValid, value);
				} else if (!elementNow.equals(value)) {
					elementStyles.removeProperty(nameValid);
					// TODO is remove necessary?
					elementStyles.setProperty(nameValid, value);
				}
			}
		}
		for (int x = elementStyles.getLength() - 1; x != -1; x--) { // remove
			String elementStyle = elementStyles.item(x);
			if (add.styles == null || !add.styles.containsKey(Style.valueOfValid(elementStyle))) {
				elementStyles.removeProperty(elementStyle);
			}
		}
		// TODO remove all listeners!
		// if (add.listeners!=null) {
		// for (String listener : add.listeners.keySet()) {
		// if (add.element.addEventListener(type, listener))
		// }
		// }
		//
		syncChildren(add);
	}

	// Convert from ReactC to Fluent, and ensure that the root is taken from
	// static created items.
	private static Fluent toFluent(Viewable viewy) {
		Fluent result = null;
		if (viewy instanceof ViewOf) {
			result = getRootOfStaticFluent(((ViewOf<?>) viewy).generate());
		} else {
			result = (Fluent) viewy;
		}
		return result;
	}

	protected static Fluent getRootOfStaticFluent(Fluent result) {
		// When a Fluent craeted by a static function is given, we should get
		// the most upper
		// parent, not the last item of the fluent notated item.
		while (result.parent != null) {

			// Error when mixing staticly created items and non-staticly
			// created items.
			if (result.element != null) {
				throw new IllegalArgumentException("Can not reconnect connected DOM elements");
			}
			result = result.parent;
		}
		return result;
	}

}
