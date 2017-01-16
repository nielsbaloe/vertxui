package live.connector.vertxui.client.fluent;

import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.document;

import java.util.TreeMap;

import elemental.dom.Element;

public class Renderer {

	protected static void syncChild(Fluent parent, Viewable newViewable, Fluent oldView) {
		console.log("START new=" + newViewable + " old=" + oldView + " parent=" + parent);

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
			console.log("syncCreate because old is null for newView=" + newView);
			create(parent, newView);
		} else {
			compareAndFix(parent, newView, oldView);
		}
	}

	private static void create(Fluent parent, Fluent newView) {
		console.log("create newView=" + newView.tag + " with parent=" + parent.tag);
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

		if (!compare(newView.tag, oldView.tag)) {
			console.log("syncRender: leuk maar tagname anders");
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

		// <---------------------------------------------------------------------
		// TOT HIER GEBLEVEN
		// <---------------------------------------------------------------------
		// TOT HIER GEBLEVEN
		// <---------------------------------------------------------------------

		// // Style
		// CSSStyleDeclaration elementStyles = newView.element.getStyle();
		// if (newView.styles != null) {
		// for (Style name : newView.styles.keySet()) { // add or adjust
		// String nameValid = name.nameValid();
		// String value = newView.styles.get(name);
		//
		// String elementNow = elementStyles.getPropertyValue(nameValid);
		// if (elementNow == null) {
		// elementStyles.setProperty(nameValid, value);
		// } else if (!elementNow.equals(value)) {
		// elementStyles.removeProperty(nameValid);
		// // TODO is remove necessary?
		// elementStyles.setProperty(nameValid, value);
		// }
		// }
		// }
		// for (int x = elementStyles.getLength() - 1; x != -1; x--) { // remove
		// String elementStyle = elementStyles.item(x);
		// if (newView.styles == null ||
		// !newView.styles.containsKey(Style.valueOfValid(elementStyle))) {
		// elementStyles.removeProperty(elementStyle);
		// }
		// }
		// // TODO remove all listeners!
		// // if (add.listeners!=null) {
		// // for (String listener : add.listeners.keySet()) {
		// // if (add.element.addEventListener(type, listener))
		// // }
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

	private final static Att[] emptyAttributes = new Att[0];

	// TODO perhaps keep an array of keys instead of creating it all the time
	// when comparing
	private static void compareAttributes(Element element, TreeMap<Att, String> mNew, TreeMap<Att, String> mOld) {
		Att[] kNew = (mNew == null) ? emptyAttributes : mNew.keySet().toArray(emptyAttributes);
		Att[] kOld = (mOld == null) ? emptyAttributes : mOld.keySet().toArray(emptyAttributes);

		// console.log("---START compareAttributes() kNew=" + kNew.length + "
		// kOld=" + kOld.length);
		int n = 0, o = 0;
		// avoiding creating new sets (guava, removeAll etc) and minimizing
		// lookups (.get)
		while (n < kNew.length || o < kOld.length) {
			Att nAtt = null;
			if (mNew != null && n < kNew.length) {
				nAtt = kNew[n];
				n++;
			}
			Att oAtt = null;
			if (mOld != null && o < kOld.length) {
				oAtt = kOld[o];
				o++;
			}
			if (nAtt != null && oAtt == null) {
				console.log("setting attribute: " + nAtt.nameValid() + "," + mNew.get(nAtt));
				if (element != null) {
					element.setAttribute(nAtt.nameValid(), mNew.get(nAtt));
				}
			} else if (nAtt == null && oAtt != null) {
				console.log("removing attribute: " + oAtt.nameValid());
				if (element != null) {
					element.removeAttribute(oAtt.nameValid());
				}
				// } else if (nAtt == null && oAtt == null) {
				// throw new IllegalArgumentException("both can not be null n="
				// + n + " o=" + o);
			} else { // both attributes must have a value here
				int compare = nAtt.compareTo(oAtt); // comparing keys
				if (compare == 0) { // same keys
					String oldValue = mOld.get(oAtt);
					String newValue = mNew.get(nAtt);
					console.log(
							"same keys, both " + nAtt.nameValid() + " oldValue=" + oldValue + " newValue=" + newValue);
					if (!oldValue.equals(newValue)) {
						console.log("    ... but value differs: was " + oldValue + " will be " + newValue);
						if (element != null) {
							element.removeAttribute(nAtt.nameValid());
							element.setAttribute(nAtt.nameValid(), newValue);
						}
					}
				} else if (compare < 0) {
					console.log("putting back NEW while comparing " + nAtt.name() + " to " + oAtt.name());
					n--; // TODO test
				} else { // compare>0
					console.log("putting back OLD while comparing " + nAtt.name() + " to " + oAtt.name());
					o--;
				}
			}
		}
	}

	private static boolean compare(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}

}
