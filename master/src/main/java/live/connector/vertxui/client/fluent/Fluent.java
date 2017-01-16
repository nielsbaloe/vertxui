package live.connector.vertxui.client.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.dom.Document;
import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Console;
import elemental.html.Window;
import elemental.js.dom.JsDocument;
import elemental.js.html.JsWindow;
import live.connector.vertxui.client.fluent.ViewOn.Function;
import live.connector.vertxui.client.util.ConsoleTester;

/**
 * Fluent HTML, child-based fluent-basednotation of html. Use getDocument()
 * getBody() and getHead() to start building your interface. Adding childs is
 * done by using the methods (like .li() ) or by some constructors that can
 * handle multiple arguments (like .div(li[]) ). Attributes are set by attr(),
 * styles by style(), and listeners by listen() or their appropriate methods.
 * 
 * @author ng
 *
 */
public class Fluent implements Viewable {

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
												return window;
												}-*/;

	private static native JsDocument getDocument() /*-{
													return window.parent.document;
														}-*/;

	/**
	 * If we are attached, this element exists, otherwise this is null (or we
	 * must be synced).
	 */
	protected Element element;
	protected Fluent parent;

	/**
	 * Attached or detached: these are tag, attrs and children. If tag null, we
	 * are a non-API tag.
	 */
	protected String tag;
	protected TreeMap<Att, String> attrs;
	protected TreeMap<Style, String> styles;
	protected TreeMap<String, EventListener> listeners;
	protected List<Viewable> childs;
	protected String inner;

	/**
	 * API call for normal HTML elements. Without a parent: detached.
	 */
	private Fluent(String tag, Fluent parent) {
		this.tag = tag.toUpperCase();
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
			element = document.createElement(tag);
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
	protected Fluent(Element parent) {
		element = parent;
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

	protected static Fluent dom(String id) {
		Element found = document.getElementById(id);
		Fluent result = new Fluent(found);
		result.tag = found.getTagName();
		return result;
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
			listeners = new TreeMap<>();
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
		return listen("load", listener);
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
		return listen("mouseenter", listener);
	}

	public Fluent mouseleave(EventListener listener) {
		return listen("mouseleave", listener);
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
			styles = new TreeMap<>();
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
		if (name == null) { // ignoring the call
			return this;
		}

		if (attrs == null) {
			attrs = new TreeMap<>();
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

	private void addNew(Viewable item) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		if (item instanceof ViewOn) {
			((ViewOn<?>) item).setParent(this);
			((ViewOn<?>) item).sync(); // needs to render!
		} else {
			// When a Fluent craeted by a static function is given, we should
			// get the most upper parent, not the last item of the fluent
			// notated item.
			while (((Fluent) item).parent != null) {
				if (((Fluent) item).element != null) {
					throw new IllegalArgumentException("Can not reconnect connected DOM elements");
				}
				item = ((Fluent) item).parent;
			}
		}
		childs.add(item);
	}

	private Fluent add(Fluent... items) {
		for (Fluent item : items) {
			addNew(item);
		}
		return this;
	}

	public Fluent add(Stream<Fluent> stream) {
		stream.forEach(item -> addNew(item));
		return this;
	}

	public List<Viewable> getChildren() {
		return childs;
	}

	public <T> ViewOn<T> add(T initialState, Function<T, Fluent> method) {
		ViewOn<T> result = new ViewOn<T>(initialState, method);
		result.setParent(this);
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
		result += " /> el=";
		if (element == null) {
			result += "null";
		} else {
			result += element.getNodeName();
		}
		result += ", parent.tag=";
		if (parent != null) {
			result += parent.tag;
		}
		result += "}";
		return result;
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
		Fluent result = new Fluent("BUTTON", this);
		result.inner(text);
		return result;
	}

	public Fluent li(String classs, String text) {
		return li(classs).inner(text);
	}

	public static Fluent Li() {
		return new Fluent("LI", null);
	}

	public Fluent li() {
		return new Fluent("LI", this);
	}

	public Fluent li(String classs) {
		return li().classs(classs);
	}

	public static Fluent Li(String classs) {
		return Li().classs(classs);
	}

	public static Fluent Li(String classs, String inner) {
		return Li(classs).inner(inner);
	}

	public Fluent div() {
		return new Fluent("DIV", this);
	}

	public static Fluent Div() {
		return new Fluent("DIV", null);
	}

	public Fluent div(String classs) {
		return div().classs(classs);
	}

	public static Fluent Div(String classs, Fluent... items) {
		return Div().classs(classs).add(items);
	}

	public static Fluent Div(String classs, Stream<Fluent> stream) {
		return Div(classs).add(stream);
	}

	public Fluent div(Fluent... list) {
		return div().add(list);
	}

	public Fluent div(Stream<Fluent> stream) {
		return div().add(stream);
	}

	public Fluent div(String classs, Stream<Fluent> stream) {
		return div(classs).add(stream);
	}

	public static Fluent Div(Fluent... list) {
		return Div().add(list);
	}

	public static Fluent Div(String classs) {
		return Div().classs(classs);
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
		return new Fluent("WBR", this);
	}

	public Fluent a(String inner, String href) {
		return new Fluent("A", this).attr(Att.href, href).inner(inner);
	}

	public static Fluent A(String classs, String inner, String href) {
		return new Fluent("A", null).attr(Att.href, href).classs(classs).inner(inner);
	}

	public Fluent abbr() {
		return new Fluent("ABBR", this);
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
		return new Fluent("H1", this).inner(text);
	}

	public static Fluent H1(String text) {
		return new Fluent("H1", null).inner(text);
	}

	public Fluent h2(String text) {
		return new Fluent("H2", this).inner(text);
	}

	public Fluent h3(String text) {
		return new Fluent("H3", this).inner(text);
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

	public Fluent nav(String classs) {
		return nav().classs(classs);
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

	/**
	 * Load javascript files synchronously and evalue/execute them directly too.
	 * You can also add them at the head of the html-document with
	 * Vertx.addLibrariesJs(), which is the same but more 'to the rules'. You
	 * need this if you want to use the javascript right after loading it (which
	 * is normal in most cases).
	 * 
	 * @return
	 */
	public Fluent scriptSync(String... jss) {
		if (!GWT.isClient()) {
			return this;
		}
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
		return this;
	}

	/**
	 * Load one or more javascript files, asynchronous as normal. You can't use
	 * these libraries in your code directly, for that, use scriptAsyncEval().
	 * 
	 * @param jss
	 * @return
	 */
	// TODO: write VertxUI.addScript()
	public Fluent script(String... jss) {
		for (String js : jss) {
			new Fluent("script", this).attr(Att.type, "text/javascript").attr(Att.src, js);

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
		return this;
	}

	public Fluent style(String... csss) {
		for (String css : csss) {
			new Fluent("link", this).attr(Att.rel, "stylesheet").attr(Att.href, css);
		}
		return this;
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
		return new Fluent("TABLE", this);
	}

	public Fluent tbody() {
		return new Fluent("tbody", this);
	}

	public Fluent td() {
		return new Fluent("TD", this);
	}

	public Fluent td(String text) {
		return new Fluent("TD", this).inner(text);
	}

	public Fluent textarea() {
		return new Fluent("TEXTAREA", this);
	}

	public Fluent tfoot() {
		return new Fluent("TFOOT", this);
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
		return new Fluent("U", this).inner(text);
	}

	public Fluent ul() {
		return new Fluent("UL", this);
	}

	public static Fluent Ul() {
		return new Fluent("UL", null);
	}

	public static Fluent Ul(String classs) {
		return Ul().classs(classs);
	}

	public Fluent ul(String classs) {
		return ul().classs(classs);
	}

	public Fluent ul(String classs, Fluent... items) {
		return ul(classs).add(items);
	}

	public Fluent var() {
		return new Fluent("VAR", this);
	}

	public Fluent video() {
		return new Fluent("VIDEO", this);
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

}
