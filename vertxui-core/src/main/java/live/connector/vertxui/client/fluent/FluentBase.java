package live.connector.vertxui.client.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.gwt.core.client.GWT;

import elemental.dom.Document;
import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.events.UIEvent;
import elemental.html.Console;
import elemental.html.HTMLOptionsCollection;
import elemental.html.InputElement;
import elemental.html.SelectElement;
import elemental.html.Window;
import elemental.js.dom.JsDocument;
import elemental.js.html.JsWindow;
import live.connector.vertxui.client.test.ConsoleTester;

public class FluentBase implements Viewable {

	static {
		if (!GWT.isClient()) {
			document = null;
			window = null;
		} else {
			document = getDocument();// Browser.getDocument();
			window = getWindow(); // Browser.getWindow();
		}
	}
	public static Document document;
	public final static Window window;
	public final static Console console;
	public static Fluent body;
	public final static Fluent head;
	static {
		if (!GWT.isClient()) {
			console = new ConsoleTester();
			body = new Fluent(null);
			head = new Fluent(null);
		} else {
			console = window.getConsole();
			body = new Fluent(document.getBody());
			head = new Fluent(document.getHead());
		}
	}

	private static native JsWindow getWindow() /*-{
												return window.top;
												}-*/;

	private static native JsDocument getDocument() /*-{
													return window.top.document;
														}-*/;

	public native static String eval(String code) /*-{
													return window.top.eval(code);
													}-*/;

	/**
	 * If we are attached to DOM , 'element' exists, otherwise this is null.
	 */
	protected Node element;
	protected Fluent parent;

	/**
	 * Attached or detached: these are tag, attrs and children. If tag null, we
	 * are a non-API tag.
	 */
	protected String tag;
	protected TreeMap<Att, String> attrs;
	protected TreeMap<Css, String> styles;
	protected TreeMap<String, EventListener> listeners;
	protected List<Viewable> childs;
	protected String text;

	/**
	 * API call for normal HTML elements. Without a parent: detached.
	 * 
	 * @param tag
	 *            the tag
	 * @param parent
	 *            the parent, if any.
	 */
	protected FluentBase(String tag, Fluent parent) {
		this.tag = tag;
		this.parent = parent;

		// Update parent
		if (parent != null) {
			if (parent.childs == null) {
				parent.childs = new ArrayList<>();
			}
			parent.childs.add(this);
		}

		// Add to DOM if connected
		if (tag != null && parent != null && parent.element != null) {

			if (tag.equals("TEXT")) {
				element = document.createTextNode("");
			} else {
				element = document.createElement(tag);
			}
			if (parent != null) {
				// console.log("appending " + tag + " to parentTag: " +
				// parent.tag);
				parent.element.appendChild(element);
			}
		}
	}

	/**
	 * For when this represents an existing object: getBody() getHead() and
	 * dom().
	 * 
	 * @param parent
	 *            the existing object
	 */
	protected FluentBase(Node parent) {
		element = parent;
	}

	/**
	 * GET the dom object, null if it doesn't exist.
	 * 
	 * @return the corresponding node if DOM-attached, otherwise null.
	 */
	public Node dom() {
		return element;
	}

	/**
	 * Create a Fluent object by starting out with an existing node with an id,
	 * usefull for existing HTML pages.
	 * 
	 * @param id
	 *            the id.
	 * @return a Fluent object with the give id, or null.
	 */
	public static Fluent getElementById(String id) {
		Node node = document.getElementById(id);
		if (node == null) {
			return null;
		}
		return new Fluent(node);
	}

	/**
	 * Set the textContent (HTML) for this element, avoiding the less stable
	 * innerHtml and innerTxt. Set to null (or empty string) to clear.
	 * 
	 * Note that it will also set the text of all children, so prevent combining
	 * text and children together. If you really really mean to have children
	 * and text together (which is opiniated bad practice), use textNode() to
	 * create a text node.
	 * 
	 * @param text
	 *            the new text inside this node.
	 * @return this
	 */
	public Fluent txt(String text) {
		if (!Renderer.equalsString(this.text, text)) {
			this.text = text;
			if (element != null) {
				// console.log("setting text to "+text);
				element.setTextContent(text);
			}
		}
		return (Fluent) this;
	}

	/**
	 * Gives the text that has been set; note that the real DOM text is "" if
	 * after you set it to null.
	 * 
	 * @return the current text.
	 */
	public String txt() {
		return this.text;
	}

	/**
	 * A convenient helper method for general even listeners when you want the
	 * Fluent object too - also executes event.stopPropagation().
	 * 
	 * @param type
	 *            the listener type
	 * @param listener
	 *            the async callback method as bifunction (including the Fluent
	 *            object)
	 * @return this
	 */
	public Fluent listen(String type, BiConsumer<Fluent, Event> listener) {
		return listen(type, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, event);
		});
	}

	/**
	 * Add or remove (by value null) an eventlistener.
	 * 
	 * @param name
	 *            the name that should be listening too
	 * @param value
	 *            the low level eventlistener
	 * @return this
	 */
	public Fluent listen(String name, EventListener value) {
		if (listeners == null) {
			listeners = new TreeMap<>();
		}
		EventListener oldValue = listeners.get(name);

		if (value != null) { // set it

			// if does not exist yet or has a different value
			if (oldValue == null || !oldValue.equals(value)) {
				listeners.put(name, value);
				if (element != null) { // if visual
					element.addEventListener(name, value);
				}
			}

		} else { // remove it

			// if old value exists
			if (oldValue != null) {
				listeners.remove(name);
				if (element != null) { // if visual
					element.removeEventListener(name, oldValue);
				}
			}
		}
		return (Fluent) this;
	}

	/**
	 * Get the eventlistener for the given eventname.
	 * 
	 * @param event
	 *            event name
	 * @return the event listener
	 */
	public EventListener listen(String event) {
		if (listeners == null) {
			return null;
		}
		return listeners.get(event);
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent keyup(BiConsumer<Fluent, KeyboardEvent> listener) {
		return listen(Event.KEYUP, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (KeyboardEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent click(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.CLICK, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener.
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent load(EventListener listener) {
		return listen("LOAD", listener);
	}

	/**
	 * A convenient helper method for this event listener.
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent focus(EventListener listener) {
		return listen(Event.FOCUS, listener);
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent blur(BiConsumer<Fluent, UIEvent> listener) {
		return listen(Event.BLUR, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (UIEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent keydown(BiConsumer<Fluent, KeyboardEvent> listener) {
		return listen(Event.KEYDOWN, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (KeyboardEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent changed(BiConsumer<Fluent, UIEvent> listener) {
		return listen(Event.CHANGE, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (UIEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent keypress(BiConsumer<Fluent, KeyboardEvent> listener) {
		return listen(Event.KEYPRESS, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (KeyboardEvent) event);

		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent dblclick(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.DBLCLICK, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mousedown(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.MOUSEDOWN, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mouseup(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.MOUSEUP, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mouseover(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.MOUSEOVER, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mouseenter(BiConsumer<Fluent, MouseEvent> listener) {
		return listen("mouseenter", event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mouseleave(BiConsumer<Fluent, MouseEvent> listener) {
		return listen("mouseleave", event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mousemove(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.MOUSEMOVE, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * A convenient helper method for this event listener - also executes
	 * event.stopPropagation().
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public Fluent mouseout(BiConsumer<Fluent, MouseEvent> listener) {
		return listen(Event.MOUSEOUT, event -> {
			event.stopPropagation();
			listener.accept((Fluent) this, (MouseEvent) event);
		});
	}

	/**
	 * Set multiple css parameters in one call.
	 * 
	 * @param name
	 *            name of first parameter
	 * @param value
	 *            value of first parameter
	 * @param name2
	 *            name of second parameter
	 * @param value2
	 *            value of second parameter
	 * @param name3
	 *            name of third parameter
	 * @param value3
	 *            value of third parameter
	 * @return this
	 */
	public Fluent css(Css name, String value, Css name2, String value2, Css name3, String value3) {
		css(name, value);
		css(name2, value2);
		css(name3, value3);
		return (Fluent) this;
	}

	/**
	 * Set multiple css parameters in one call.
	 * 
	 * @param name
	 *            name of first parameter
	 * @param value
	 *            value of first parameter
	 * @param name2
	 *            name of second parameter
	 * @param value2
	 *            value of second parameter
	 * @return this
	 */
	public Fluent css(Css name, String value, Css name2, String value2) {
		css(name, value);
		css(name2, value2);
		return (Fluent) this;
	}

	/**
	 * Set a css style which needs a number.
	 * 
	 * @param name
	 *            name of css item
	 * @param x
	 *            number-value of css item
	 * @return this
	 */
	public Fluent css(Css name, int x) {
		return css(name, x + "");
	}

	/**
	 * Set or remove (by value null) a css style.
	 * 
	 * @param name
	 *            name of css item
	 * @param value
	 *            value of css item
	 * @return this
	 */
	public Fluent css(Css name, String value) {
		if (styles == null) {
			styles = new TreeMap<>();
		}
		String oldValue = styles.get(name);

		if (value != null) { // set it

			// if does not exist yet or has a different value
			if (oldValue == null || !oldValue.equals(value)) {
				styles.put(name, value);
				if (element != null) { // if visual
					((Element) element).getStyle().setProperty(name.nameValid(), value);
				}
			}

		} else { // remove it

			// if old value exists
			if (oldValue != null) {
				styles.remove(name);
				if (element != null) { // if visual
					((Element) element).getStyle().removeProperty(name.nameValid());
				}
			}

		}
		return (Fluent) this;
	}

	/**
	 * Get the css property, returns null if it doesn't exist.
	 * 
	 * @param name
	 *            the name of the requested css
	 * @return the value of the current css, or null if none was given.
	 */
	public String css(Css name) {
		if (styles == null) {
			return null;
		}
		return styles.get(name);
	}

	/**
	 * Set (or remove by value null) an attribute, or the property 'checked' or
	 * 'value' or 'selectedIndex'. Note that with the properties you only set
	 * the default value, these can change in runtime, use dom*() methods to get
	 * the live value.
	 * 
	 * @param name
	 *            the name to be set
	 * @param value
	 *            the value to be set
	 * @return this
	 */
	public Fluent att(Att name, String value) {
		if (attrs == null) {
			attrs = new TreeMap<>();
		}
		String oldValue = attrs.get(name);

		if (value != null) { // set it

			// if does not exist yet or has a different value
			if (oldValue == null || !oldValue.equals(value)) {
				attrs.put(name, value);
				if (element != null) { // if visual

					switch (name) {
					// case selected:
					// ((OptionElement) element).setSelected(true);
					// break;
					case checked:
						((InputElement) element).setChecked(true);
						break;
					case value:
						((InputElement) element).setValue(value);
						break;
					case selectedIndex:
						((SelectElement) element).setSelectedIndex(Integer.parseInt(value));
					default:
						((Element) element).setAttribute(name.nameValid(), value);
						break;
					}

				}
			}

		} else { // remove it

			// if old value exists
			if (oldValue != null) {
				attrs.remove(name);
				if (element != null) { // if visual

					switch (name) {
					// case selected:
					// ((OptionElement) element).setSelected(false);
					// break;
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
						((Element) element).removeAttribute(name.nameValid());
						break;
					}

				}
			}
		}
		return (Fluent) this;
	}

	/**
	 * Set two attributes.
	 * 
	 * @param name
	 *            name of attribute 1
	 * @param value
	 *            value of attrube 1
	 * @param name2
	 *            name of attribute 2
	 * @param value2
	 *            value of attribute 2
	 * @return this
	 */
	public Fluent att(Att name, String value, Att name2, String value2) {
		return att(name, value).att(name2, value2);
	}

	/**
	 * Set three attributes.
	 * 
	 * @param name
	 *            name of attribute 1
	 * @param value
	 *            value of attrube 1
	 * @param name2
	 *            name of attribute 2
	 * @param value2
	 *            value of attribute 2
	 * @param name3
	 *            name of attribute 3
	 * @param value3
	 *            value of attribute 3
	 * @return this
	 */
	public Fluent att(Att name, String value, Att name2, String value2, Att name3, String value3) {
		return att(name, value).att(name2, value2).att(name3, value3);
	}

	/**
	 * Get the value of an input field if it has been changed by the DOM. Also
	 * synchronizes with the virtual DOM, which is why you should prefer this
	 * above reading out the event.targetEvent()... .
	 * 
	 * @return the .value or: what has been entered in this input field
	 */
	public String domValue() {
		String result = ((InputElement) element).getValue();
		if (result != null) {
			attrs.put(Att.value, result);
		} else {
			attrs.remove(Att.value);
		}
		return result;
	}

	/**
	 * Get the value of a checkbox if it has been changed by the DOM. Also
	 * synchronizes with the virtual DOM, which is why you should prefer this
	 * above reading out the event.targetEvent()... .
	 * 
	 * @return the .value property or: whether the visual value has been
	 *         checked.
	 */
	public boolean domChecked() {
		boolean result = ((InputElement) element).isChecked();
		if (result) {
			attrs.put(Att.checked, "1");
		} else {
			attrs.remove(Att.checked);
		}
		return result;
	}

	/**
	 * Get the selected items if it has been changed by the DOM. Also
	 * synchronizes with the virtual DOM, which is why you should prefer this
	 * above reading out the event.targetEvent()... .
	 * 
	 * @return the values of options that are selected
	 */
	public String[] domSelectedOptions() {
		HTMLOptionsCollection elements = (HTMLOptionsCollection) ((SelectElement) element).getSelectedOptions();
		String[] result = new String[elements.length()];

		// Reset virtual DOM
		for (Viewable child : childs) {
			if (child instanceof Fluent) {
				Fluent fluent = (Fluent) child;
				fluent.attrs.remove(Att.selected);
			}
		}

		// Set virtual DOM and collect
		for (int x = 0; x < elements.length(); x++) {
			Element select = (Element) elements.at(x);

			// writing in virtual DOM
			for (Viewable child : childs) {
				if (child instanceof Fluent) {
					Fluent fluent = (Fluent) child;
					if (select == fluent.element) {
						fluent.attrs.put(Att.selected, "true");
					}
				}
			}

			// collect
			result[x] = select.getAttribute("value");
		}
		return result;
	}

	/**
	 * Get the selected index if it has been changed by the DOM. Also
	 * synchronizes with the virtual DOM, which is why you should prefer this
	 * above reading out the event.targetEvent()... .
	 * 
	 * @return the .selectedIndex property or: which option in this select has
	 *         been selected.
	 */
	public int domSelectedIndex() {
		int result = ((SelectElement) element).getSelectedIndex();
		if (result != -1) {
			attrs.put(Att.selectedIndex, result + "");
		} else {
			attrs.put(Att.selectedIndex, null);
		}
		return result;
	}

	/**
	 * Get the attribute value that was set before.
	 * 
	 * @param name
	 *            the name of the attribute
	 * 
	 * @return the string value of this attribute if it has been set, otherwise
	 *         null.
	 */
	public String att(Att name) {
		if (attrs == null) {
			return null;
		}
		return attrs.get(name);
	}

	/**
	 * Convenient method to get the id of this node; note that this is quite
	 * irrelevant vwhen working with Fluent, because in Fluent you keep the
	 * references to the objects, rather then to do anything with ids.
	 * 
	 * @return the id attribute of this item if it has been set, otherwise null.
	 */
	public String id() {
		return att(Att.id);
	}

	/**
	 * Set the id for this method; note that this is quite irrelevant vwhen
	 * working with Fluent, because in Fluent you keep the references to the
	 * objects, rather then to do anything with ids.
	 * 
	 * @param string
	 *            the requested id
	 * @return this
	 */
	public Fluent id(String string) {
		return att(Att.id, string);
	}

	/**
	 * Convenient method to set the class attribute.
	 * 
	 * @return the class attribute of this item if it has been set, otherwise
	 *         null.
	 */
	public String classs() {
		return att(Att.class_);
	}

	/**
	 * Convenient method to get the given CSS-class attribute.
	 * 
	 * @param string
	 *            the CSS-class attribute.
	 * @return this
	 */
	public Fluent classs(String string) {
		return att(Att.class_, string);
	}

	/**
	 * @return the tag name
	 */
	public String tag() {
		return tag;
	}

	private void addNew(Viewable item) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		if (item instanceof ViewOnBase) {
			((ViewOnBase) item).setParent((Fluent) this);
			((ViewOnBase) item).sync(); // needs to render!
		} else {
			item = getRootOf((Fluent) item);

			// This line connects staticly created Fluents to the DOM.
			// see for comments for the if-statement below in ViewOnBase::sync()
			if (!GWT.isClient() || element != null) {
				Renderer.syncChild((Fluent) this, item, null);
				isRendered(true);
			}
		}
		childs.add(item);
	}

	protected static Fluent getRootOf(Fluent item) {
		// When a Fluent created by a static function is given, we should
		// get the most upper parent, not the last item of the fluent
		// notated item.
		if (item == null) {
			return null;
		}
		while (item.parent != null) {
			if (item.element != null) {
				throw new IllegalArgumentException("Can not reconnect connected DOM elements");
			}
			item = item.parent;
		}
		return item;
	}

	/**
	 * Addd items; try not to use this method, use the fluent methods or the
	 * constructors instead.
	 * 
	 * @param items
	 *            the items to be added
	 * @return this
	 */
	public Fluent add(Viewable... items) {
		for (Viewable item : items) {
			addNew(item);
		}
		return (Fluent) this;
	}

	/**
	 * Addd items; try not to use this method, use the fluent methods or the
	 * cosntructors instead.
	 * 
	 * @param stream
	 *            the stream of items to be added
	 * @return this
	 */
	public Fluent add(Stream<Fluent> stream) {
		stream.forEach(item -> addNew(item));
		return (Fluent) this;
	}

	/**
	 * Get a list of children; do not change or modify this set, it will mesh up
	 * your GUI.
	 * 
	 * @return a list of children.
	 */
	public List<Viewable> getChildren() {
		return childs;
	}

	/**
	 * Add a view-on-model to describe a view on a model. If your model changes,
	 * use .sync() or .state(newState) to notify viewOn and VertxUI will adjust
	 * the differences in the view.
	 * 
	 * @param <T>
	 *            the model
	 * @param initialState
	 *            a pointer to a wrapper-object in which the real DTO is, or the
	 *            initial state which you will replace with state(...) when it
	 *            has changed.
	 * @param method
	 *            a view on the model description
	 * @return the created ViewOn object, added as child.
	 */
	public <T> ViewOn<T> add(T initialState, Function<T, Fluent> method) {
		ViewOn<T> result = new ViewOn<T>(initialState, method);
		addNew(result);
		return result;
	}

	/**
	 * Add a view-on-both-model2 to describe a view on two models. If your
	 * models changes, use .sync() or .state(newState) to notify viewOnBoth and
	 * VertxUI will adjust the differences in the view.
	 * 
	 * @param <A>
	 *            the first model
	 * @param <B>
	 *            the second model
	 * @param initialState1
	 *            a pointer to a wrapper-object in which the real DTO is, or the
	 *            initial state which you will replace with state(...) when it
	 *            has changed.
	 * @param initialState2
	 *            a second pointer to a wrapper-object in which the real DTO is,
	 *            or the initial state which you will replace with state(...)
	 *            when it has changed.
	 * @param method
	 *            a view on the model description
	 * @return a ViewOnBoth object, added as child to this object
	 */
	public <A, B> ViewOnBoth<A, B> add(A initialState1, B initialState2, BiFunction<A, B, Fluent> method) {
		ViewOnBoth<A, B> result = new ViewOnBoth<A, B>(initialState1, initialState2, method);
		addNew(result);
		return result;
	}

	@Override
	public String toString() {
		String result = "Fluent{<";
		if (tag == null) {
			result += "null";
		} else {
			result += tag;
		}
		if (attrs != null) {
			for (Att attr : attrs.keySet()) {
				result += " " + attr.nameValid() + "=" + attrs.get(attr);
			}
		}
		result += "/> el=";
		// if (element == null || ((element != null && !(element instanceof
		// Element)))) {
		// result += "null";
		// } else {
		// result += ((Element) element).getOuterHTML();
		// }
		result += ", parent.tag=";
		if (parent != null) {
			result += parent.tag;
		}
		result += "}";
		return result;
	}

	/**
	 * Convenient method to disable an object. The attribute 'disable' is set or
	 * unset.
	 * 
	 * @param disabled
	 *            should this object be disabled
	 * @return this;
	 */
	public Fluent disabled(boolean disabled) {
		if (disabled) {
			att(Att.disable, "");
		} else {
			att(Att.disable, null);
		}
		return (Fluent) this;
	}

	/**
	 * Convenient method to hide this visual object. The css style 'display' is
	 * set to none or nothing.
	 * 
	 * @param doit
	 *            hide this object
	 * @return this
	 */
	@Override
	public Fluent hide(boolean doit) {
		if (doit) {
			css(Css.display, "none");
		} else {
			css(Css.display, null);
		}
		return (Fluent) this;
	}

	// UNIT TESTING
	// UNIT TESTING
	// UNIT TESTING

	/**
	 * Clean the DOM manually before the next junit test.
	 */
	public static void clearVirtualDOM() {
		if (!GWT.isClient()) {
			body = new Fluent(null);
		} else {
			throw new IllegalArgumentException(
					"Calling this method has zero meaning inside your browser, reload the page in your browser for a clean start.");
		}
	}

	public Fluent clone() {
		if (parent != null || element != null) {
			throw new IllegalArgumentException(
					"You can only clone objects created with a static method (which start with a capital letter like Div or Span) and which are not DOM-connected yet.");
		}
		Fluent result = new Fluent(tag, null);
		if (text != null) {
			result.txt(text);
		}
		if (attrs != null) {
			for (Att att : attrs.keySet()) {
				result.att(att, attrs.get(att));
			}
		}
		if (styles != null) {
			for (Css name : styles.keySet()) {
				result.css(name, styles.get(name));
			}
		}
		if (listeners != null) {
			for (String name : listeners.keySet()) {
				result.listen(name, listeners.get(name));
			}
		}
		if (childs != null) {
			for (Viewable child : childs) {
				if (child instanceof Fluent) {
					result.add(((Fluent) child).clone());
				} else if (child instanceof ViewOn<?>) {
					result.add(((ViewOn<?>) child).clone());
				} else {
					result.add(((ViewOnBoth<?, ?>) child).clone());
				}
			}
		}
		return result;
	}

	/*
	 * A not-exact string representation of this node and a few children. This
	 * does not cover listeners (which is not possible in GWT production mode),
	 * but does cover the rest, which should be enough to identify which child
	 * is which for most render optimalisation issues. For example, when one
	 * child between other childs in a list is deleted.
	 */
	@Override
	public String getCrcString() {
		StringBuilder result = new StringBuilder();
		if (tag != null) {
			result.append(tag);
		}
		if (text != null) {
			result.append(text);
		}
		if (attrs != null) {
			for (Att att : attrs.keySet()) {
				result.append(att.name());
			}
		}
		if (childs != null) {
			// only three deep
			for (int x = 0; x < 3 && x < childs.size(); x++) {
				result.append(childs.get(x).getCrcString());
			}
		}
		return result.toString();
	}

	@Override
	public int getCrc() {
		return getCrcString().hashCode();
	}

	/**
	 * Is called when the element is attached to the DOM. Currently the 'shown'
	 * parameter is always true, the unattach version is not implemented yet.
	 * 
	 * @param state
	 *            whether the object is rendered or removed from the DOM.
	 */
	@Override
	public void isRendered(boolean state) {
		if (childs != null) {
			for (Viewable child : childs) {
				child.isRendered(state);
			}
		}
	}

}
