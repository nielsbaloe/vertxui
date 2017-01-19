package live.connector.vertxui.client.fluent;

import static live.connector.vertxui.client.fluent.Fluent.document;

import java.util.TreeMap;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;

public class Renderer {

	protected static void syncChild(Fluent parent, Viewable newViewable, Fluent oldView) {
		// console.log("START new=" + newViewable + " old=" + oldView + "
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
		}

		if (oldView == null) {
			// console.log("syncCreate because old is null for newView=" +
			// newView);
			create(parent, newView);
		} else {
			compareAndFix(parent, newView, oldView);
		}
	}

	private static void create(Fluent parent, Fluent newView) {
		// console.log("create newView=" + newView.tag + " with parent=" +
		// parent.tag);
		newView.parent = parent;
		if (parent.element != null) {
			newView.element = document.createElement(newView.tag);
			parent.element.appendChild(newView.element);

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
	}

	private static void compareAndFix(Fluent parent, Fluent newView, Fluent oldView) {
		// if ("bladiebla" instanceof Object) {
		// create(parent, newView);
		// return;
		// }

		if (!compareIgnoreCase(newView.tag, oldView.tag)) {
			// console.log("syncRender: leuk maar tagname anders");
			if (parent.element != null) {
				parent.element.removeChild(oldView.element);
			}
			create(parent, newView);
			return;
		}
		newView.parent = parent;

		if (parent.element != null) {
			newView.element = oldView.element;
		}

		// // innerHtml
		if (!compare(newView.inner, oldView.inner)) {

			if (parent.element != null) {
				newView.inner(newView.inner);
			}
		}
		compareAttributes(newView.element, newView.attrs, oldView.attrs);
		compareStyles(newView.element, newView.styles, oldView.styles);

		// <---------------------------------------------------------------------
		// TOT HIER GEBLEVEN
		// <---------------------------------------------------------------------
		// TOT HIER GEBLEVEN
		// <---------------------------------------------------------------------
		// TODO TODO TODO TODO
		// Listeners
		// // if (add.listeners!=null) { ...
		// // }
		// //

		// TODO do not assume same sequence but use hashing
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

	private static void compareStyles(Element source, TreeMap<Style, String> treeNew, TreeMap<Style, String> treeOld) {
		CSSStyleDeclaration style = null;
		if (source != null) {
			style = source.getStyle();
		}
		Style[] keysNew = (treeNew == null) ? emptyStyles : treeNew.keySet().toArray(emptyStyles);
		Style[] keysOld = (treeOld == null) ? emptyStyles : treeOld.keySet().toArray(emptyStyles);

		// console.log("---START compareAttributes() kNew=" + kNew.length + "
		// kOld=" + kOld.length);
		int countNew = 0, countOld = 0;
		// avoiding creating new sets (guava, removeAll etc) and minimizing
		// lookups (.get)
		while (countNew < keysNew.length || countOld < keysOld.length) {
			Style attNew = null;
			if (treeNew != null && countNew < keysNew.length) {
				attNew = keysNew[countNew];
				countNew++;
			}
			Style attOld = null;
			if (treeOld != null && countOld < keysOld.length) {
				attOld = keysOld[countOld];
				countOld++;
			}
			if (attNew != null && attOld == null) {
				// console.log("setting attribute: " + attNew.nameValid() + ","
				// + treeNew.get(attNew));
				if (style != null) {
					style.setProperty(attNew.nameValid(), treeNew.get(attNew));
				}
			} else if (attNew == null && attOld != null) {
				// console.log("removing attribute: " + attOld.nameValid());
				if (style != null) {
					style.removeProperty(attOld.nameValid());
				}
				// } else if (nAtt == null && oAtt == null) {
				// throw new IllegalArgumentException("both can not be null n="
				// + n + " o=" + o);
			} else { // both attributes must have a value here
				int compare = attNew.compareTo(attOld); // comparing keys
				// console.log("comparing "+attNew+" "+attOld);
				if (compare == 0) { // same keys
					String oldValue = treeOld.get(attOld);
					String newValue = treeNew.get(attNew);
					if (!oldValue.equals(newValue)) {
						// console.log(
						// "changing value for " + attNew.nameValid() + " old="
						// + oldValue + " new=" + newValue);
						if (style != null) {
							style.removeProperty(attNew.nameValid());
							style.setProperty(attNew.nameValid(), newValue);
						}
						// } else {
						// console.log("no change " + attNew.nameValid() + "
						// value=" + oldValue);
					}
				} else if (compare < 0) {
					if (style != null) {
						style.setProperty(attNew.nameValid(), treeNew.get(attNew));
					}
					// console.log(" setting " + attNew.nameValid());
					countOld--;
				} else { // compare>0
					// console.log(" removing " + attOld.nameValid());
					if (style != null) {
						style.removeProperty(attOld.nameValid());
					}
					countNew--;
				}
			}
		}
	}

	private final static Att[] emptyAttributes = new Att[0];

	private static void compareAttributes(Element element, TreeMap<Att, String> treeNew, TreeMap<Att, String> treeOld) {
		Att[] keysNew = (treeNew == null) ? emptyAttributes : treeNew.keySet().toArray(emptyAttributes);
		Att[] keysOld = (treeOld == null) ? emptyAttributes : treeOld.keySet().toArray(emptyAttributes);

		// console.log("---START compareAttributes() kNew=" + kNew.length + "
		// kOld=" + kOld.length);
		int countNew = 0, countOld = 0;
		// avoiding creating new sets (guava, removeAll etc) and minimizing
		// lookups (.get)
		while (countNew < keysNew.length || countOld < keysOld.length) {
			Att attNew = null;
			if (treeNew != null && countNew < keysNew.length) {
				attNew = keysNew[countNew];
				countNew++;
			}
			Att attOld = null;
			if (treeOld != null && countOld < keysOld.length) {
				attOld = keysOld[countOld];
				countOld++;
			}
			if (attNew != null && attOld == null) {
				// console.log("setting attribute: " + attNew.nameValid() + ","
				// + treeNew.get(attNew));
				if (element != null) {
					element.setAttribute(attNew.nameValid(), treeNew.get(attNew));
				}
			} else if (attNew == null && attOld != null) {
				// console.log("removing attribute: " + attOld.nameValid());
				if (element != null) {
					element.removeAttribute(attOld.nameValid());
				}
				// } else if (nAtt == null && oAtt == null) {
				// throw new IllegalArgumentException("both can not be null n="
				// + n + " o=" + o);
			} else { // both attributes must have a value here
				int compare = attNew.compareTo(attOld); // comparing keys
				// console.log("comparing "+attNew+" "+attOld);
				if (compare == 0) { // same keys
					String oldValue = treeOld.get(attOld);
					String newValue = treeNew.get(attNew);
					if (!oldValue.equals(newValue)) {
						// console.log(
						// "changing value for " + attNew.nameValid() + " old="
						// + oldValue + " new=" + newValue);
						if (element != null) {
							element.removeAttribute(attNew.nameValid());
							element.setAttribute(attNew.nameValid(), newValue);
						}
						// } else {
						// console.log("no change " + attNew.nameValid() + "
						// value=" + oldValue);
					}
				} else if (compare < 0) {
					if (element != null) {
						element.setAttribute(attNew.nameValid(), treeNew.get(attNew));
					}
					// console.log(" setting " + attNew.nameValid());
					countOld--;
				} else { // compare>0
					// console.log(" removing " + attOld.nameValid());
					if (element != null) {
						element.removeAttribute(attOld.nameValid());
					}
					countNew--;
				}
			}
		}
	}

	private static boolean compare(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equals(str2));
	}

	private static boolean compareIgnoreCase(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}
}
