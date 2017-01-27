package live.connector.vertxui.client.fluent;

import static live.connector.vertxui.client.fluent.Fluent.document;

import java.util.TreeMap;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.EventListener;

public class Renderer {

	protected static void syncChild(Fluent parent, Viewable newViewable, Fluent oldView) {
		// Fluent.console.log("START new=" + newViewable + " old=" + oldView + "
		// parent=" + parent);

		// Nothing new, just remove
		if (newViewable == null) {
			if (parent.element != null && oldView.element != null) {
				parent.element.removeChild(oldView.element);
			}
			return;
		}

		// Convert
		Fluent newView = null;
		if (newViewable instanceof Fluent) {
			newView = (Fluent) newViewable;
			newView.parent = parent;
		} else {
			newView = ((ViewOn<?>) newViewable).generate(parent);
			// Fluent.console.log("Just generated for newView=" + newView + "
			// with dom=" + (parent.element != null));
		}

		if (oldView == null) {
			create(parent, newView, null);
		} else {
			renderChanges(parent, newView, oldView);
		}
	}

	/**
	 * Generate a new element. If oldView is given, replace it with a new
	 * element (when the tagname is different).
	 */
	private static void create(Fluent parent, Fluent newView, Fluent oldView) {
		if (newView == null) {
			return; // nothing to do
		}
		// Fluent.console.log(
		// "create newView=" + newView.tag + " with parent=" + parent.tag + "
		// dom=" + (parent.element != null));
		newView.parent = parent;

		if (parent.element != null) { // if dom-attached
			newView.element = document.createElement(newView.tag);
			// NOT APPENDCHILD, WE DO THAT AS LATE AS POSSIBLE

			if (newView.attrs != null) {
				for (Att name : newView.attrs.keySet()) {
					newView.element.setAttribute(name.nameValid(), newView.attrs.get(name));
				}
			}
			if (newView.styles != null) {
				for (Style name : newView.styles.keySet()) {
					newView.element.getStyle().setProperty(name.nameValid(), newView.styles.get(name));
				}
			}
			if (newView.inner != null) {
				newView.element.setInnerHTML(newView.inner);
			}
			if (newView.listeners != null) {
				for (String name : newView.listeners.keySet()) {
					newView.element.addEventListener(name, newView.listeners.get(name));
				}
			}
		}

		if (newView.childs != null) {
			for (Viewable child : newView.childs) {
				syncChild(newView, child, null);
			}
		}

		// DEFERRED BINDING OF THE ELEMENT
		if (parent.element != null) {

			if (oldView == null) { // default
				parent.element.appendChild(newView.element);
			} else {
				// Fluent.console.log("Replacing for new=" +
				// newView.element.getTagName());
				// Fluent.console.log("and can not be null old=" +
				// oldView.element);
				parent.element.replaceChild(newView.element, oldView.element);
			}
		}

	}

	private static void renderChanges(Fluent parent, Fluent newView, Fluent oldView) {
		if (newView == null) {
			if (parent.element != null) {
				parent.element.removeChild(oldView.element);
			}
			return; // nothing to do
		}

		if (!equalsStringIgnoreCase(newView.tag, oldView.tag)) {
			// Fluent.console.log("tags differ new=" + newView.tag + " old="
			// + oldView.tag);
			create(parent, newView, oldView);
			return;
		}
		newView.parent = parent;

		if (parent.element != null) { // if we are dom-attached
			newView.element = oldView.element;

			// TODO: idea: why not put everything in Fluent, and then
			// oldView.attr(newView.attr()); etc
			if (!equalsString(newView.inner, oldView.inner)) {
				newView.element.setInnerHTML(newView.inner);
			}
			compareApply(newView.element, newView.attrs, oldView.attrs, emptyAttributes);
			compareApply(newView.element, newView.styles, oldView.styles, emptyStyles);
			compareApply(newView.element, newView.listeners, oldView.listeners, emptyListeners);
		}

		// TODO do not assume same sequence but use hashes
		int nChilds = (newView.childs == null) ? 0 : newView.childs.size();
		int oChilds = (oldView.childs == null) ? 0 : oldView.childs.size();
		int max = Math.max(nChilds, oChilds);
		for (int x = 0; x < max; x++) {
			Viewable newChild = null;
			if (x < nChilds) {
				newChild = newView.childs.get(x);
			}
			Viewable oldChild = null;
			if (x < oChilds) {
				oldChild = oldView.childs.get(x);
			}
			Fluent oldChildAsFluent = null;
			if (oldChild instanceof Fluent) {
				oldChildAsFluent = (Fluent) oldChild;
			} else {
				oldChildAsFluent = ((ViewOn<?>) oldChild).getView();
			}
			syncChild(newView, newChild, oldChildAsFluent);
		}
	}

	private final static Style[] emptyStyles = new Style[0];
	private final static String[] emptyListeners = new String[0];
	private final static Att[] emptyAttributes = new Att[0];

	private static <K extends Comparable<K>, V> void compareApply(Element element, TreeMap<K, V> treeNew,
			TreeMap<K, V> treeOld, K[] empty) {

		K[] keysNew = (treeNew == null) ? empty : treeNew.keySet().toArray(empty);
		K[] keysOld = (treeOld == null) ? empty : treeOld.keySet().toArray(empty);

		// console.log("---START compareAttributes() kNew=" + kNew.length + "
		// kOld=" + kOld.length);
		int countNew = 0, countOld = 0;
		// avoiding creating new sets (guava, removeAll etc) and minimizing
		// lookups (.get)
		while (countNew < keysNew.length || countOld < keysOld.length) {
			K keyNew = null;
			if (treeNew != null && countNew < keysNew.length) {
				keyNew = keysNew[countNew];
				countNew++;
			}
			K keyOld = null;
			if (treeOld != null && countOld < keysOld.length) {
				keyOld = keysOld[countOld];
				countOld++;
			}
			if (keyNew != null && keyOld == null) {
				// console.log("setting attribute: " + attNew.nameValid() + ","
				// + treeNew.get(attNew));
				if (element != null) {
					compareApplySet(element, keyNew, treeNew.get(keyNew));
				}
			} else if (keyNew == null && keyOld != null) {
				// console.log("removing attribute: " + attOld.nameValid());
				if (element != null) {
					compareApplyRemove(element, keyOld, treeOld.get(keyOld));
				}
				// } else if (nAtt == null && oAtt == null) {
				// throw new IllegalArgumentException("both can not be null n="
				// + n + " o=" + o);
			} else { // both attributes must have a value here
				int compare = keyNew.compareTo(keyOld);
				// console.log("comparing "+attNew+" "+attOld);
				if (compare == 0) { // same keys
					V oldValue = treeOld.get(keyOld);
					V newValue = treeNew.get(keyNew);
					if (!compareApplyValueEquals(oldValue, newValue)) { // key
																		// same,
																		// other
																		// value
						// console.log(
						// "changing value for " + attNew.nameValid() + " old="
						// + oldValue + " new=" + newValue);
						if (element != null) {
							compareApplyRemove(element, keyNew, oldValue);
							compareApplySet(element, keyNew, newValue);
						}
						// } else {
						// console.log("no change " + attNew.nameValid() + "
						// value=" + oldValue);
					}
				} else if (compare < 0) {
					if (element != null) {
						compareApplySet(element, keyNew, treeNew.get(keyNew));
					}
					// console.log(" setting " + attNew.nameValid());
					countOld--;
				} else { // compare>0
					// console.log(" removing " + attOld.nameValid());
					if (element != null) {
						compareApplyRemove(element, keyOld, treeOld.get(keyOld));
					}
					countNew--;
				}
			}
		}
	}

	private static <V> boolean compareApplyValueEquals(V oldValue, V newValue) {
		if (oldValue instanceof EventListener) { // listeners
			// GWT: same method refs should not be replaced

			// live.connector.vertxui.client.FluentRenderer$1methodref$b$Type@1
			// live.connector.vertxui.client.FluentRenderer$2methodref$b$Type@2
			String sNew = newValue.toString();
			String sOld = oldValue.toString();
			int dNew = sNew.indexOf("$");
			int dOld = sOld.indexOf("$");
			// if class (untill the first dollar) mathes)
			if (dNew != -1 && dOld == dNew && sNew.substring(0, dNew).equals(sOld.substring(0, dOld))) {
				int aNew = sNew.indexOf("methodref$");
				int aOld = sOld.indexOf("methodref$");
				// if they both contain 'methodref$'
				if (aNew != -1 && aNew == aOld) {
					int bNew = sNew.indexOf("$", aNew + 10);
					int bOld = sOld.indexOf("$", aOld + 10);
					// if dollar after 'methodref$' exists and equals
					if (bNew != -1 && bNew == bOld && sNew.substring(aNew, bNew).equals(sOld.substring(aOld, bOld))) {
						// Fluent.console.log("** same for "+sNew+" "+sOld);
						return true;
					}
				}
			}
			return sNew.equals(sOld);
		} else {
			return oldValue.equals(newValue);
		}
	}

	private static <T, V> void compareApplyRemove(Element element, T name, V value) {
		if (name instanceof Att) {
			element.removeAttribute(((Att) name).nameValid());
		} else if (name instanceof Style) {
			element.getStyle().removeProperty(((Style) name).nameValid());
		} else {
			// Fluent.console.log("removing " + name);
			((Node) element).removeEventListener((String) name, (EventListener) value);
		}
	}

	private static <T, V> void compareApplySet(Element element, T name, V value) {
		if (name instanceof Att) {
			element.setAttribute(((Att) name).nameValid(), (String) value);
		} else if (name instanceof Style) {
			element.getStyle().setProperty(((Style) name).nameValid(), (String) value);
		} else {
			// Fluent.console.log("setting " + name + " with " + value);
			((Node) element).addEventListener((String) name, (EventListener) value);
		}
	}

	public static boolean equalsString(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equals(str2));
	}

	private static boolean equalsStringIgnoreCase(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}
}
