package live.connector.vertxui.client.fluent;

import static live.connector.vertxui.client.fluent.Fluent.document;

import java.util.TreeMap;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.EventListener;
import elemental.html.InputElement;
import elemental.html.SelectElement;

public class Renderer {

	protected static void syncChild(Fluent parent, Viewable newViewable, Fluent oldView) {
		// Fluent.console.log("syncChild() new=" + newViewable + " old=" +
		// oldView + " parent=" + parent);

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
		} else if (newViewable instanceof ViewOn<?>) {
			newView = ((ViewOn<?>) newViewable).generate(parent);
			// Fluent.console.log("Just generated for newView=" + newView + "
			// with dom=" + (parent.element != null));
		} else {
			newView = ((ViewOnBoth<?, ?>) newViewable).generate(parent);
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

			if (newView.tag.equals("TEXT")) {
				newView.element = document.createTextNode("");
			} else {
				newView.element = document.createElement(newView.tag);
			}
			// NOT APPENDCHILD, WE DO THAT AS LATE AS POSSIBLE

			if (newView.attrs != null) {
				for (Att name : newView.attrs.keySet()) {
					((Element) newView.element).setAttribute(name.nameValid(), newView.attrs.get(name));
				}
			}
			if (newView.styles != null) {
				for (Css name : newView.styles.keySet()) {
					((Element) newView.element).getStyle().setProperty(name.nameValid(), newView.styles.get(name));
				}
			}
			if (newView.text != null) {
				newView.element.setTextContent(newView.text);
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

			if (!equalsString(newView.text, oldView.text)) {
				newView.element.setTextContent(newView.text);
			}
			compareApply(newView.element, newView.attrs, oldView.attrs, emptyAttributes);
			compareApply(newView.element, newView.styles, oldView.styles, emptyStyles);
			compareApply(newView.element, newView.listeners, oldView.listeners, emptyListeners);
		}

		// Children
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
			if (oldChild == null) {
			} else if (oldChild instanceof Fluent) {
				oldChildAsFluent = (Fluent) oldChild;
			} else {
				oldChildAsFluent = ((ViewOnBase) oldChild).getView();
			}

			// BETA middle-child removal optimalisation, see
			// FluentInnerRendering.java
			// if number of childs/differ, and if there is an old child
			// if (nChilds != oChilds && oldChild != null && newChild != null) {
			// Fluent.console.log("Starting compare ");
			// Fluent.console.log(" for new=" + newChild + " because nChilds=" +
			// nChilds + " oChilds=" + oChilds);
			//
			// int newRef = newChild.getCrc();
			// Fluent.console.log(
			// "Comparing new=" + newChild.getCrcString() + " and old=" +
			// oldChildAsFluent.getCrcString());
			// // and if the
			// // current child is different
			// if (newRef != oldChildAsFluent.getCrc()) { // then look forward
			// // whether there
			// Fluent.console.log(" not equal for old =" +
			// oldChildAsFluent.getCrcString());
			// // is a next one similar
			// for (int y = x; y < oChilds; y++) {
			// Viewable test = oldView.childs.get(y);
			// Fluent tester = null;
			// if (test == null) {
			// continue;
			// } else if (test instanceof Fluent) {
			// tester = (Fluent) test;
			// } else {
			// tester = ((ViewOnBase) test).getView();
			// }
			// if (tester.getCrc() == newRef) {
			//
			// if (parent.element != null) {
			//
			// Fluent.console
			// .log("Switching old=" + ((Element)
			// oldChildAsFluent.element).getOuterHTML()
			// + " and new=" + ((Element) tester.element).getOuterHTML());
			// Fluent.console.log(
			// "old=" + oldChildAsFluent.getCrcString() + " oldNew=" +
			// tester.getCrcString());
			// Fluent.console.log("parent=" + parent.element + " "
			// + (parent.element == tester.element.getParentNode()));
			// Fluent.console.log("parentOld=" + parent.element + " "
			// + (oldChildAsFluent.element.getParentNode() ==
			// tester.element.getParentNode()));
			// Fluent.console.log("parentNew=" + parent.element + " "
			// + (parent.element == oldChildAsFluent.element.getParentNode()));
			// Node removed =
			// tester.element.getParentNode().replaceChild(tester.element,
			// oldChildAsFluent.element);
			// // Node intermediate = tester.element;
			// // oldChildAsFluent.element = intermediate;
			// }
			// oldChildAsFluent = tester;
			// oldView.childs.set(x, test);
			// oldView.childs.remove(oldChild);
			// oChilds--;
			// max = Math.max(nChilds, oChilds);
			// Fluent.console.log("Done");
			// break;
			// }
			// }
			// }
			// }

			syncChild(newView, newChild, oldChildAsFluent);
		}
	}

	private final static Css[] emptyStyles = new Css[0];
	private final static String[] emptyListeners = new String[0];
	private final static Att[] emptyAttributes = new Att[0];

	private static <K extends Comparable<K>, V> void compareApply(Node element, TreeMap<K, V> treeNew,
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
		// if (oldValue instanceof EventListener) { // listeners
		// // This makes sure that references to the same static Class+Method
		// // should not be replaced. However, we can not detect that because
		// // it GWT is release, the functionnames get erased, so this code
		// // only works in debug mode...
		//
		// // live.connector.vertxui.client.FluentRenderer$1methodref$b$Type@1
		// // live.connector.vertxui.client.FluentRenderer$2methodref$b$Type@2
		// String sNew = newValue.toString();
		// String sOld = oldValue.toString();
		// int dNew = sNew.indexOf("$");
		// int dOld = sOld.indexOf("$");
		// // if class (untill the first dollar) mathes)
		// if (dNew != -1 && dOld == dNew && sNew.substring(0,
		// dNew).equals(sOld.substring(0, dOld))) {
		// int aNew = sNew.indexOf("methodref$");
		// int aOld = sOld.indexOf("methodref$");
		// // if they both contain 'methodref$'
		// if (aNew != -1 && aNew == aOld) {
		// int bNew = sNew.indexOf("$", aNew + 10);
		// int bOld = sOld.indexOf("$", aOld + 10);
		// // if dollar after 'methodref$' exists and equals
		// if (bNew != -1 && bNew == bOld && sNew.substring(aNew,
		// bNew).equals(sOld.substring(aOld, bOld))) {
		// // Fluent.console.log("** same for "+sNew+" "+sOld);
		// return true;
		// }
		// }
		// }
		// return sNew.equals(sOld);
		// } else {
		return oldValue.equals(newValue);
		// }
	}

	private static <T, V> void compareApplyRemove(Node element, T name, V value) {
		// Fluent.console.log("removing " + name + " with " + value);
		if (name instanceof Att) {

			switch ((Att) name) {
			case checked:
				((InputElement) element).setChecked(false);
				break;
			case value:
				((InputElement) element).setValue(null);
				break;
			case selectedIndex:
				// nothing to do
				// ((SelectElement) element).);
				break;
			default:
				((Element) element).removeAttribute(((Att) name).nameValid());
				break;
			}

		} else if (name instanceof Css) {
			((Element) element).getStyle().removeProperty(((Css) name).nameValid());
		} else {
			element.removeEventListener((String) name, (EventListener) value);
		}
	}

	private static <T, V> void compareApplySet(Node element, T name, V value) {
		// Fluent.console.log("setting " + name + " with " + value);
		if (name instanceof Att) {

			switch ((Att) name) {
			case checked:
				((InputElement) element).setChecked(true);
				break;
			case value:
				((InputElement) element).setValue((String) value);
				break;
			case selectedIndex:
				((SelectElement) element).setSelectedIndex(Integer.parseInt((String) value));
			default:
				((Element) element).setAttribute(((Att) name).nameValid(), (String) value);
				break;
			}

		} else if (name instanceof Css) {
			((Element) element).getStyle().setProperty(((Css) name).nameValid(), (String) value);
		} else {
			element.addEventListener((String) name, (EventListener) value);
		}
	}

	public static boolean equalsString(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equals(str2));
	}

	private static boolean equalsStringIgnoreCase(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}
}
