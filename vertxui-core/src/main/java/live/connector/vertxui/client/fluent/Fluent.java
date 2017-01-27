package live.connector.vertxui.client.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
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
import live.connector.vertxui.client.test.ConsoleTester;

/**
 * Fluent HTML, child-based fluent-basednotation of html. Use getDocument()
 * getBody() and getHead() to start building your GUI. Adding childs is done by
 * using the methods (like .li() ) or by some constructors that can handle
 * multiple arguments (like .div(li[]) ). Attributes are set by attr(), styles
 * by style(), and listeners by listen() or their appropriate methods.
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
		this.tag = tag.toUpperCase(); // TODO uppercase all tags
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
	 * Set the inner text (HTML) for this element. Set to null (or empty string)
	 * to clear.
	 */
	// TODO move from innerHtml to innerText
	public Fluent inner(String innerHtml) {
		if (Renderer.equalsString(this.inner, innerHtml)) {
			// console.log("Skipping, still " + innerHtml);
			return this;
		}
		this.inner = innerHtml;
		if (element != null) {
			// console.log("setting innerHtml to "+innerHtml);
			element.setInnerHTML(innerHtml);
		}
		return this;
	}

	/**
	 * Gives the inner html that has been set; note that the real DOM inner HTML
	 * is "" if you set it to null or when no value is given anymore.
	 */
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

	public Fluent css(Style name, String value, Style name2, String value2) {
		css(name, value);
		css(name2, value2);
		return this;
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
			item = getRootOf((Fluent) item);
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

	public <T> ViewOn<T> add(ViewOn<T> result) {
		result.setParent(this);
		addNew(result);
		return result;
	}

	public <T> ViewOn<T> add(T initialState, Function<T, Fluent> method) {
		ViewOn<T> result = new ViewOn<T>(initialState, method);
		add(result);
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

	public Fluent input() {
		return new Fluent("INPUT", this);
	}

	public Fluent input(String classs, String inner, String type, String id) {
		return input().classs(classs).inner(inner).attr(Att.type, type).id(id);
	}

	public static Fluent Input() {
		return new Fluent("INPUT", null);
	}

	public static Fluent Input(String classs, String inner, String type, String id) {
		return Input().classs(classs).inner(inner).attr(Att.type, type).id(id);
	}

	public Fluent button() {
		return new Fluent("BUTTON", this);
	}

	public Fluent button(String classs) {
		return button().classs(classs);
	}

	public Fluent button(String classs, String text) {
		return button().classs(classs).inner(text);
	}

	public static Fluent Button() {
		return new Fluent("BUTTON", null);
	}

	public static Fluent Button(String classs) {
		return Button().classs(classs);
	}

	public static Fluent Button(String classs, String text) {
		return Button().classs(classs).inner(text);
	}

	public Fluent li() {
		return new Fluent("LI", this);
	}

	public Fluent li(String classs) {
		return li().classs(classs);
	}

	public Fluent li(String classs, String text) {
		return li(classs).inner(text);
	}

	public static Fluent Li() {
		return new Fluent("LI", null);
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

	public Fluent div(String classs) {
		return div().classs(classs);
	}

	public Fluent div(String classs, String inner) {
		return div(classs).inner(inner);
	}

	public Fluent div(Fluent... list) {
		return div().add(list);
	}

	public Fluent div(Stream<Fluent> stream) {
		return div().add(stream);
	}

	public Fluent div(String classs, String inner, Fluent... adds) {
		return div(classs).inner(inner).add(adds);
	}

	public Fluent div(String classs, Stream<Fluent> stream) {
		return div(classs).add(stream);
	}

	public Fluent div(String classs, Fluent... adds) {
		return div(classs).add(adds);
	}

	public static Fluent Div() {
		return new Fluent("DIV", null);
	}

	public static Fluent Div(String classs) {
		return Div().classs(classs);
	}

	public static Fluent Div(String classs, String inner) {
		return Div().classs(classs).inner(inner);
	}

	public static Fluent Div(Fluent... list) {
		return Div().add(list);
	}

	public static Fluent Div(String classs, Fluent... items) {
		return Div().classs(classs).add(items);
	}

	public static Fluent Div(String classs, Stream<Fluent> stream) {
		return Div(classs).add(stream);
	}

	// REST

	public Fluent area() {
		return new Fluent("AREA", this);
	}

	public Fluent base() {
		return new Fluent("BASE", this);
	}

	public Fluent br() {
		return new Fluent("BR", this);
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
		return new Fluent("AUDIO", this);
	}

	public Fluent b() {
		return new Fluent("B", this);
	}

	public Fluent bdi() {
		return new Fluent("BDI", this);
	}

	public Fluent bdo() {
		return new Fluent("bdo", this);
	}

	public Fluent blockquote() {
		return new Fluent("blockquote", this);
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

	public Fluent cite() {
		return new Fluent("cite", this);
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

	public Fluent dd() {
		return new Fluent("dd", this);
	}

	public Fluent del() {
		return new Fluent("DEL", this);
	}

	public Fluent details() {
		return new Fluent("DETAILS", this);
	}

	public Fluent dfn() {
		return new Fluent("DFN", this);
	}

	public Fluent dialog() {
		return new Fluent("DIALOG", this);
	}

	public Fluent dl() {
		return new Fluent("DL", this);
	}

	public Fluent dt() {
		return new Fluent("DT", this);
	}

	public Fluent em() {
		return new Fluent("em", this);
	}

	public Fluent fieldset() {
		return new Fluent("fieldset", this);
	}

	public Fluent figcaption() {
		return new Fluent("figcaption", this);
	}

	public Fluent figure() {
		return new Fluent("figure", this);
	}

	public Fluent footer() {
		return new Fluent("FOOTER", this);
	}

	public Fluent form() {
		return new Fluent("FORM", this);
	}

	public Fluent form(String classs) {
		return form().classs(classs);
	}

	public static Fluent Form() {
		return new Fluent("FORM", null);
	}

	public static Fluent Form(String classs) {
		return Form().classs(classs);
	}

	public Fluent h1() {
		return new Fluent("H1", this);
	}

	public Fluent h1(String classs, String text) {
		return h1().classs(classs).inner(text);
	}

	public static Fluent H1(String classs, String text) {
		return new Fluent("H1", null).classs(classs).inner(text);
	}

	public Fluent h2() {
		return new Fluent("H2", this);
	}

	public Fluent h3() {
		return new Fluent("H3", this);
	}

	public Fluent h4() {
		return new Fluent("h4", this);
	}

	public Fluent h5() {
		return new Fluent("h5", this);
	}

	public Fluent h6() {
		return new Fluent("h6", this);
	}

	public Fluent header() {
		return new Fluent("header", this);
	}

	public Fluent i() {
		return new Fluent("i", this);
	}

	public Fluent iframe() {
		return new Fluent("iframe", this);
	}

	public Fluent ins() {
		return new Fluent("ins", this);
	}

	public Fluent kbd() {
		return new Fluent("kbd", this);
	}

	public Fluent label() {
		return new Fluent("LABEL", this);
	}

	public Fluent label(String classs) {
		return label().classs(classs);
	}

	public Fluent label(String classs, String inner) {
		return label().classs(classs).inner(inner);
	}

	public static Fluent Label() {
		return new Fluent("LABEL", null);
	}

	public static Fluent Label(String classs) {
		return Label().classs(classs);
	}

	public static Fluent Label(String classs, String inner) {
		return Label().classs(classs).inner(inner);
	}

	public Fluent legend() {
		return new Fluent("LEGEND", this);
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

	public Fluent option() {
		return new Fluent("option", this);
	}

	public Fluent output() {
		return new Fluent("output", this);
	}

	public Fluent p() {
		return new Fluent("p", this);
	}

	public Fluent p(String classs, String text) {
		return p().classs(classs).inner(text);
	}

	public Fluent pre(String classs, String text) {
		return new Fluent("PRE", this).classs(classs).inner(text);
	}

	public Fluent progress() {
		return new Fluent("progress", this);
	}

	public Fluent q() {
		return new Fluent("Q", this);
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

	public Fluent s() {
		return new Fluent("s", this);
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
		return new Fluent("SECTION", this);
	}

	public Fluent select() {
		return new Fluent("SELECT", this);
	}

	public Fluent small() {
		return new Fluent("SMALL", this);
	}

	public Fluent span() {
		return new Fluent("SPAN", this);
	}

	public Fluent span(String classs) {
		return span().classs(classs);
	}

	public Fluent span(String classs, String inner) {
		return span().classs(classs).inner(inner);
	}

	public static Fluent Span() {
		return new Fluent("SPAN", null);
	}

	public static Fluent Span(String classs) {
		return Span().classs(classs);
	}

	public static Fluent Span(String classs, String inner) {
		return Span().classs(classs).inner(inner);
	}

	public Fluent strong() {
		return new Fluent("STRONG", this);
	}

	public Fluent sub() {
		return new Fluent("SUB", this);
	}

	public Fluent summary() {
		return new Fluent("SUMMARY", this);
	}

	public Fluent sup() {
		return new Fluent("SUP", this);
	}

	public Fluent table() {
		return new Fluent("TABLE", this);
	}

	public Fluent table(String classs) {
		return table().classs(classs);
	}

	public static Fluent Table() {
		return new Fluent("TABLE", null);
	}

	public static Fluent Table(String classs) {
		return Table().classs(classs);
	}

	public Fluent tbody() {
		return new Fluent("TBODY", this);
	}

	public Fluent td() {
		return new Fluent("TD", this);
	}

	public Fluent td(String classs, String inner) {
		return td().classs(classs).inner(inner);
	}

	public static Fluent Td() {
		return new Fluent("TD", null);
	}

	public static Fluent Td(String classs, String inner) {
		return Td().classs(classs).inner(inner);
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

	public Fluent thead() {
		return new Fluent("thead", this);
	}

	public Fluent time() {
		return new Fluent("time", this);
	}

	public Fluent title(String classs, String inner) {
		return new Fluent("TITLE", this).classs(classs).inner(inner);
	}

	public Fluent tr() {
		return new Fluent("tr", this);
	}

	public Fluent tr(Fluent... tds) {
		return tr().add(tds);
	}

	public Fluent u() {
		return new Fluent("U", this);
	}

	public Fluent ul() {
		return new Fluent("UL", this);
	}

	public Fluent ul(String classs) {
		return ul().classs(classs);
	}

	public Fluent ul(String classs, Fluent... items) {
		return ul(classs).add(items);
	}

	public static Fluent Ul() {
		return new Fluent("UL", null);
	}

	public static Fluent Ul(String classs) {
		return Ul().classs(classs);
	}

	public static Fluent Ul(String classs, Fluent... items) {
		return Ul(classs).add(items);
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
