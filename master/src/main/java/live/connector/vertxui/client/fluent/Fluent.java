package live.connector.vertxui.client.fluent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Document;
import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import elemental.dom.Node;
import elemental.dom.NodeList;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.js.dom.JsDocument;

/**
 * Fluent HTML. Use getDocument() getBody() and getHead() to start building your
 * interface. Adding childs is done by using the methods (like .li() ) or by
 * some constructors that can handle multiple arguments (like .div(li[]) ).
 * Attributes are set by attr(), styles by style(), and listeners by listen() or
 * their appropriate methods.
 * 
 * @author ng
 *
 */
public class Fluent {

	protected final static Document document = getDocument();// Browser.getDocument();

	public static native JsDocument getDocument() /*-{
													// var child = window.parent.document.getElementById("a");
													//	child.parentNode.removeChild(child);
													 // return $doc;
													return window.parent.document;
														}-*/;

	/**
	 * If we are attached, this element exists, otherwise this is null (or we
	 * must be synced).
	 */
	protected Element element;

	/**
	 * Attached or detached: these are tag, attrs and children. If tag null, we
	 * are a non-API tag.
	 */
	private String tag;
	private Map<Att, String> attrs;
	private Map<Style, String> styles;
	private Map<String, EventListener> listeners;
	private List<Fluent> childs;
	private String inner;

	/**
	 * API call for normal HTML elements. Without a parent: detached.
	 */
	protected Fluent(String tag, Fluent parent) {
		this.tag = tag;
		if (tag != null && parent != null) {
			element = document.createElement(tag);
			if (parent != null) {
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
	protected Fluent(Element parent) {
		element = parent;
	}

	/**
	 * Do not create but GET the body.
	 * 
	 */
	public static Fluent getBody() {
		return new Fluent(document.getBody());
	}

	/**
	 * Do not create but GET the head.
	 * 
	 */
	public static Fluent getHead() {
		return new Fluent(document.getHead());
	}

	/**
	 * Do not create but GET the dom object.
	 */
	public Element dom() {
		return element;
	}

	/**
	 * Get an existing object from the dom by 'id'. Please don't use this, it's
	 * slow.
	 * 
	 * @param id
	 *            the id
	 * @return a fluent html object
	 */

	@SuppressWarnings("unchecked")
	protected static <T extends Fluent> T dom(String id, Class<? extends Fluent> classs) {
		Element found = document.getElementById("id");
		// System.out.println(found.getTagName());
		// if (!found.getTagName().equals(classs.getSimpleName().toLowerCase()))
		// {
		// throw new IllegalArgumentException("Requested non-existing dom with
		// id=" + id + ": tagname="
		// + found.getTagName() + " requesting tagname=" +
		// classs.getSimpleName().toLowerCase());
		// }
		return (T) new Fluent(found);
	}

	public Fluent inner(String innerHtml) {
		this.inner = innerHtml;
		if (element != null) {
			element.setInnerHTML(innerHtml);
		}
		return this;
	}

	public String inner() {
		return this.inner;
	}

	/**
	 * Add or remove (by value null) an eventlistener.
	 * 
	 */

	public Fluent listen(String name, EventListener value) {
		if (listeners == null) {
			listeners = new HashMap<>();
		}
		if (value != null) {
			listeners.put(name, value);
			if (element != null) {
				((Node) element).addEventListener(name, value);
			}
		} else { // remove
			EventListener oldValue = listeners.get(name);
			listeners.remove(name);
			if (element != null) {
				((Node) element).removeEventListener(name, oldValue);
			}
		}
		return this;
	}

	public EventListener listen(String event) {
		if (listeners == null) {
			return null;
		}
		return listeners.get(event);
	}

	public Fluent keyup(EventListener listener) {
		return listen(Event.KEYUP, listener);
	}

	public Fluent click(EventListener listener) {
		return listen(Event.CLICK, listener);
	}

	public Fluent load(EventListener listener) {
		return listen("load", listener); // TODO mail GWT missing event
	}

	public Fluent focus(EventListener listener) {
		return listen(Event.FOCUS, listener);
	}

	public Fluent blur(EventListener listener) {
		return listen(Event.BLUR, listener);
	}

	public Fluent keydown(EventListener listener) {
		return listen(Event.KEYDOWN, listener);
	}

	public Fluent keypress(EventListener listener) {
		return listen(Event.KEYPRESS, listener);
	}

	public Fluent dblclick(EventListener listener) {
		return listen(Event.DBLCLICK, listener);
	}

	public Fluent mousedown(EventListener listener) {
		return listen(Event.MOUSEDOWN, listener);
	}

	public Fluent mouseup(EventListener listener) {
		return listen(Event.MOUSEUP, listener);
	}

	public Fluent mouseover(EventListener listener) {
		return listen(Event.MOUSEOVER, listener);
	}

	public Fluent mouseenter(EventListener listener) {
		return listen("mouseenter", listener); // TODO mail GWT missing event
	}

	public Fluent mouseleave(EventListener listener) {
		return listen("mouseleave", listener); // TODO mail GWT missing Event
	}

	public Fluent mousemove(EventListener listener) {
		return listen(Event.MOUSEMOVE, listener);
	}

	public Fluent mouseout(EventListener listener) {
		return listen(Event.MOUSEOUT, listener);
	}

	/**
	 * Set or remove (by value null) a css style.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public Fluent css(Style name, String value) {
		if (styles == null) {
			styles = new HashMap<>();
		}
		if (value == null) {
			styles.remove(name);
		} else {
			styles.put(name, value);
		}
		if (element != null) {
			if (value == null) {
				element.getStyle().removeProperty(name.nameValid());
			} else {
				element.getStyle().setProperty(name.nameValid(), value);
			}
		}
		return this;
	}

	public String css(Style name) {
		if (styles == null) {
			return null;
		}
		return styles.get(name);
	}

	/**
	 * Set or remove (by value null) an attribute.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public Fluent attr(Att name, String value) {
		if (attrs == null) {
			attrs = new HashMap<>();
		}

		if (value == null) {
			attrs.remove(name);
		} else {
			attrs.put(name, value);
		}
		if (element != null) {
			if (value == null) {
				element.removeAttribute(name.nameValid());
			} else {
				element.setAttribute(name.nameValid(), value);
			}
		}
		return this;
	}

	public String attr(Att name) {
		if (attrs == null) {
			return null;
		}
		return attrs.get(name);
	}

	public String id() {
		return attr(Att.id);
	}

	public Fluent id(String string) {
		return attr(Att.id, string);
	}

	public String classs() {
		return attr(Att.class_);
	}

	public Fluent classs(String string) {
		return attr(Att.class_, string);
	}

	public String tag() {
		return tag;
	}

	// Add

	public Fluent add(List<? extends Fluent> items) {
		for (Fluent item : items) {
			add(item);
		}
		return this;
	}

	public Fluent add(Fluent[] items) {
		for (Fluent item : items) {
			add(item);
		}
		return this;
	}

	public Fluent addd(Fluent... items) {
		for (Fluent item : items) {
			add(item);
		}
		return this;
	}

	public Fluent add(Stream<Fluent> streamy) {
		add(streamy.collect(Collectors.toList()));
		return this;
	}

	public <T> Fluent add(T model, ReactM<T> method) {
		add(new ReactMC<T>(model, method));
		return this;
	}

	public Fluent add(Fluent add) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		childs.add(add);
		sync(this, add);
		return this;
	}

	public void sync() {
		// TODO: hmm, how to sync ourselves? not necessary?
		for (Fluent child : childs) {
			sync(this, child);
		}
	}

	private void sync(Fluent parent, Fluent add) {
		if (element == null) { // not attached: no rendering
			return;
		}
		while (add.tag == null) { // skip ReactC objects
			add = ((ReactC) add).generate();
		}
		if (add.element != null) { // add is already attached
			console.log("already attached " + add.tag);
			syncRender(parent, add);
		} else {
			syncCreate(parent, add);
		}
	}

	private static void syncCreate(Fluent parent, Fluent add) {
		console.log("creating " + add.tag);
		add.element = document.createElement(add.tag);
		parent.element.appendChild(add.element);

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
			for (Fluent child : add.childs) {
				while (child.tag == null) { // skip ReactC objects
					child = ((ReactC) child).generate();
				}
				if (child.element == null) {
					syncCreate(add, child);
				} else { // recycling of the old element!
					syncRender(add, child);
				}
			}
		}
	}

	private static boolean compare(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}

	/**
	 * Check whether the fluenthtml and its element are in sync
	 */
	// TODO bijhouden wat er veranderd is, zodat niet heel deze check hoeft te
	// worden doorgevoerd
	private static void syncRender(Fluent parent, Fluent add) {
		if (!compare(add.tag, add.element.getTagName())) {
			syncCreate(parent, add);
			return;
		}
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
		// for (NameListen name : add.listeners.keySet()) { // add or adjust
		// EventListener<?> value = add.listeners.get(name);
		// .............?
		// }
		NodeList existChildren = add.element.getChildNodes();
		if (add.childs != null) {
			for (int x = 0; x < add.childs.size(); x++) {
				Fluent child = add.childs.get(x);
				while (child.tag == null) { // skip ReactC objects
					child = ((ReactC) child).generate();
				}
				child.element = (Element) existChildren.item(x);
				if (child.element == null) {
					syncCreate(parent, child);
				} else {
					syncRender(parent, child);
				}
			}
		}

		// Just remove remaining children
		int size = 0;
		if (add.childs != null) {
			size = add.childs.size();
		}
		for (int x = size; x < existChildren.getLength(); x++) {
			add.element.removeChild(existChildren.item(x));
		}
	}

	public Fluent hidden(boolean b) {
		element.setHidden(b);
		// if (hidden) {
		// css(Style.visibility, "hidden");
		// } else {
		// css(Style.visibility, "visible");
		// }
		return this;
	}

	public void disabled(boolean disabled) {
		if (disabled) {
			attr(Att.disable, "");
		} else {
			attr(Att.disable, null);
		}
	}

	public String value() {
		return valueGetter(element);
	}

	public Fluent value(String value) {
		valueSetter(element, value);
		return this;
	}

	private final native String valueGetter(Element element) /*-{
																return element.value;
																}-*/;

	private final native void valueSetter(Element element, String value) /*-{
																			element.value=value;
																			}-*/;

	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:

	public Fluent input(String type, String name) {
		Fluent result = new Fluent("input", this);
		result.attr(Att.type, type);
		result.attr(Att.name_, name);
		return result;
	}

	public Fluent button(String text) {
		Fluent result = new Fluent("button", this);
		result.inner(text);
		return result;
	}

	public Fluent li(String text) {
		return li().inner(text);
	}

	private Fluent li() {
		return new Fluent("li", this);
	}

	/**
	 * Create an unattached li element
	 */
	public static Fluent Li(String text) {
		return Li().inner(text);
	}

	public static Fluent Li() {
		return new Fluent("li", null);
	}

	public Fluent div() {
		return new Fluent("div", this);
	}

	/**
	 * Create an unattached div element
	 */
	public static Fluent Div() {
		return new Fluent("div", null);
	}

	public Fluent div(List<? extends Fluent> list) {
		return div().add(list);
	}

	public static Fluent Div(List<? extends Fluent> list) {
		return Div().add(list);
	}

	public Fluent div(Fluent[] list) {
		return div().add(list);
	}

	public static Fluent Div(Fluent[] list) {
		return Div().add(list);
	}

	public Fluent divd(Fluent... list) {
		return div().add(list);
	}

	public Fluent Divd(Fluent... list) {
		return Div().add(list);
	}

	// REST

	public Fluent area() {
		return new Fluent("area", this);
	}

	public Fluent base() {
		return new Fluent("base", this);
	}

	public Fluent br() {
		return new Fluent("br", this);
	}

	public Fluent col() {
		return new Fluent("col", this);
	}

	public Fluent embed() {
		return new Fluent("embed", this);
	}

	public Fluent hr() {
		return new Fluent("hr", this);
	}

	public Fluent img(String src) {
		Fluent result = new Fluent("img", this);
		result.attr(Att.src, src);
		return result;
	}

	public Fluent keygen() {
		return new Fluent("keygen", this);
	}

	public Fluent meta() {
		return new Fluent("meta", this);
	}

	public Fluent param() {
		return new Fluent("param", this);
	}

	public Fluent source() {
		return new Fluent("source", this);
	}

	public Fluent track() {
		return new Fluent("track", this);
	}

	public Fluent wbr() {
		return new Fluent("wbr", this);
	}

	public Fluent a(String href) {
		return new Fluent("a", this).attr(Att.href, href);
	}

	public Fluent abbr() {
		return new Fluent("abbr", this);
	}

	public Fluent address() {
		return new Fluent("address", this);
	}

	public Fluent article() {
		return new Fluent("article", this);
	}

	public Fluent aside() {
		return new Fluent("aside", this);
	}

	public Fluent audio() {
		return new Fluent("audio", this);
	}

	public Fluent b(String text) {
		return new Fluent("b", this).inner(text);
	}

	public Fluent bdi(String text) {
		return new Fluent("bdi", this).inner(text);
	}

	public Fluent bdo() {
		return new Fluent("bdo", this);
	}

	public Fluent bdo(String text) {
		return new Fluent("bdo", this).inner(text);
	}

	public Fluent blockquote() {
		return new Fluent("blockquote", this);
	}

	public Fluent blockquote(String text) {
		return new Fluent("blockquote", this).inner(text);
	}

	public Fluent body() {
		return new Fluent("body", this);
	}

	public Fluent canvas() {
		return new Fluent("canvas", this);
	}

	public Fluent caption() {
		return new Fluent("caption", this);
	}

	public Fluent caption(String text) {
		return new Fluent("caption", this).inner(text);
	}

	public Fluent cite() {
		return new Fluent("cite", this);
	}

	public Fluent cite(String text) {
		return new Fluent("cite", this).inner(text);
	}

	public Fluent code() {
		return new Fluent("code", this);
	}

	public Fluent colgroup() {
		return new Fluent("colgroup", this);
	}

	public Fluent datalist() {
		return new Fluent("datalist", this);
	}

	public Fluent dd(String text) {
		return new Fluent("dd", this).inner(text);
	}

	public Fluent del(String text) {
		return new Fluent("del", this).inner(text);
	}

	public Fluent details() {
		return new Fluent("details", this);
	}

	public Fluent dfn(String text) {
		return new Fluent("dfn", this).inner(text);
	}

	public Fluent dialog() {
		return new Fluent("dialog", this);
	}

	public Fluent dialog(String text) {
		return new Fluent("dialog", this).inner(text);
	}

	public Fluent dl() {
		return new Fluent("dl", this);
	}

	public Fluent dt() {
		return new Fluent("dt", this);
	}

	public Fluent dt(String text) {
		return new Fluent("dt", this).inner(text);
	}

	public Fluent em() {
		return new Fluent("em", this);
	}

	public Fluent em(String text) {
		return new Fluent("em", this).inner(text);
	}

	public Fluent fieldset() {
		return new Fluent("fieldset", this);
	}

	public Fluent figcaption() {
		return new Fluent("figcaption", this);
	}

	public Fluent figcaption(String text) {
		return new Fluent("figcaption", this).inner(text);
	}

	public Fluent figure() {
		return new Fluent("figure", this);
	}

	public Fluent footer() {
		return new Fluent("footer", this);
	}

	public Fluent form() {
		return new Fluent("form", this);
	}

	public Fluent h1(String text) {
		return new Fluent("h1", this).inner(text);
	}

	public Fluent h2(String text) {
		return new Fluent("h2", this).inner(text);
	}

	public Fluent h3(String text) {
		return new Fluent("h3", this).inner(text);
	}

	public Fluent h4(String text) {
		return new Fluent("h4", this).inner(text);
	}

	public Fluent h5(String text) {
		return new Fluent("h5", this).inner(text);
	}

	public Fluent h6(String text) {
		return new Fluent("h6", this).inner(text);
	}

	public Fluent header() {
		return new Fluent("header", this);
	}

	public Fluent i() {
		return new Fluent("i", this);
	}

	public Fluent i(String text) {
		return new Fluent("i", this).inner(text);
	}

	public Fluent iframe() {
		return new Fluent("iframe", this);
	}

	public Fluent ins(String text) {
		return new Fluent("ins", this).inner(text);
	}

	public Fluent kbd() {
		return new Fluent("kbd", this);
	}

	public Fluent label(String text) {
		return new Fluent("label", this).inner(text);
	}

	public Fluent legend(String text) {
		return new Fluent("legend", this).inner(text);
	}

	public Fluent main() {
		return new Fluent("main", this);
	}

	public Fluent map() {
		return new Fluent("map", this);
	}

	public Fluent mark() {
		return new Fluent("mark", this);
	}

	public Fluent menu() {
		return new Fluent("menu", this);
	}

	public Fluent menuitem() {
		return new Fluent("menuitem", this);
	}

	public Fluent meter() {
		return new Fluent("meter", this);
	}

	public Fluent nav() {
		return new Fluent("nav", this);
	}

	public Fluent noscript() {
		return new Fluent("noscript", this);
	}

	public Fluent object() {
		return new Fluent("object", this);
	}

	public Fluent ol() {
		return new Fluent("ol", this);
	}

	public Fluent optgroup() {
		return new Fluent("optgroup", this);
	}

	public Fluent option(String text) {
		return new Fluent("option", this).inner(text);
	}

	public Fluent output() {
		return new Fluent("output", this);
	}

	public Fluent p() {
		return new Fluent("p", this);
	}

	public Fluent p(String text) {
		return new Fluent("p", this).inner(text);
	}

	public Fluent pre(String text) {
		return new Fluent("pre", this).inner(text);
	}

	public Fluent progress() {
		return new Fluent("progress", this);
	}

	public Fluent q(String text) {
		return new Fluent("q", this).inner(text);
	}

	public Fluent rp() {
		return new Fluent("rp", this);
	}

	public Fluent rt() {
		return new Fluent("rt", this);
	}

	public Fluent ruby() {
		return new Fluent("ruby", this);
	}

	public Fluent s(String text) {
		return new Fluent("s", this).inner(text);
	}

	public Fluent samp() {
		return new Fluent("samp", this);
	}

	private static class XMLHttpRequestSyc extends XMLHttpRequest {
		protected XMLHttpRequestSyc() {
		}

		public final native void open(String httpMethod, String url, boolean sync) /*-{
																						this.open(httpMethod, url, sync);
																						}-*/;
	}

	private native static void eval(String code) /*-{
													eval(code);
													}-*/;

	public static void scriptSyncEval(String... jss) {
		for (String js : jss) {
			XMLHttpRequestSyc xhr = (XMLHttpRequestSyc) XMLHttpRequestSyc.create();
			xhr.setOnReadyStateChange(a -> {
				if (a.getReadyState() == XMLHttpRequest.DONE && a.getStatus() == 200) {
					eval(xhr.getResponseText());
				}
			});
			xhr.open("GET", js, false);
			xhr.send();
		}
	}

	/**
	 * Load one or more javascript files, asynchronous as normal. You can't use
	 * these libraries in your code directly, for that, use scriptAsyncEval().
	 * 
	 * @param jss
	 * @return
	 */
	public static void script(String... jss) {
		for (String js : jss) {
			new Fluent("script", getHead()).attr(Att.type, "text/javascript").attr(Att.src, js);

			// This works too, is async
			// XMLHttpRequestSyc xhr = (XMLHttpRequestSyc)
			// XMLHttpRequestSyc.create();
			// xhr.setOnReadyStateChange(a -> {
			// if (a.getReadyState() == XMLHttpRequest.DONE && a.getStatus() ==
			// 200) {
			// new Fluent("script", this).inner(xhr.getResponseText());
			// // Element src = document.createElement("script");
			// // src.setAttribute("type", "text/javascript");
			// // // src.setAttribute("src", js);
			// // src.setInnerText(xhr.getResponseText());
			// // element.appendChild(src);
			// }
			// });
			// xhr.open("GET", js, false);
			// xhr.send();
		}
	}

	public static void style(String... csss) {
		for (String css : csss) {
			new Fluent("link", getHead()).attr(Att.rel, "stylesheet").attr(Att.href, css);
		}
	}

	public Fluent section() {
		return new Fluent("section", this);
	}

	public Fluent select() {
		return new Fluent("select", this);
	}

	public Fluent small() {
		return new Fluent("small", this);
	}

	public Fluent small(String text) {
		return new Fluent("small", this).inner(text);
	}

	public Fluent span() {
		return new Fluent("span", this);
	}

	public Fluent span(String text) {
		return new Fluent("span", this).inner(text);
	}

	public Fluent strong(String text) {
		return new Fluent("strong", this).inner(text);
	}

	public Fluent sub(String text) {
		return new Fluent("sub", this).inner(text);
	}

	public Fluent summary(String text) {
		return new Fluent("summary", this).inner(text);
	}

	public Fluent sup(String text) {
		return new Fluent("sup", this).inner(text);
	}

	public Fluent table() {
		return new Fluent("table", this);
	}

	public Fluent tbody() {
		return new Fluent("tbody", this);
	}

	public Fluent td() {
		return new Fluent("td", this);
	}

	public Fluent td(String text) {
		return new Fluent("td", this).inner(text);
	}

	public Fluent textarea() {
		return new Fluent("textarea", this);
	}

	public Fluent tfoot() {
		return new Fluent("tfoot", this);
	}

	public Fluent th() {
		return new Fluent("th", this);
	}

	public Fluent th(String text) {
		return new Fluent("th", this).inner(text);
	}

	public Fluent thead() {
		return new Fluent("thead", this);
	}

	public Fluent time() {
		return new Fluent("time", this);
	}

	public Fluent title() {
		return new Fluent("title", this);
	}

	public Fluent title(String text) {
		return new Fluent("title", this).inner(text);
	}

	public Fluent tr() {
		return new Fluent("tr", this);
	}

	public Fluent u() {
		return new Fluent("u", this);
	}

	public Fluent u(String text) {
		return new Fluent("u", this).inner(text);
	}

	public Fluent ul() {
		return new Fluent("ul", this);
	}

	public Fluent var() {
		return new Fluent("var", this);
	}

	public Fluent video() {
		return new Fluent("video", this);
	}

}
