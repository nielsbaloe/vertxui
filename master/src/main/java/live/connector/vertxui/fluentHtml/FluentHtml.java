package live.connector.vertxui.fluentHtml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Attr;
import org.teavm.jso.dom.xml.NamedNodeMap;

import live.connector.vertxui.reacty.ReactC;
import live.connector.vertxui.reacty.ReactM;
import live.connector.vertxui.reacty.ReactMC;
import live.connector.vertxui.streamy.Str;

/**
 * Create fluent HTML. // TODO Markup: +child: methode, +childs: constructor,
 * attrs: attr()
 * 
 * @author ng
 *
 */
public class FluentHtml {

	protected final static HTMLDocument document = Window.current().getDocument();

	// @JSBody(params = { "javascript" }, script = "javascript")
	// protected static native void javascript(String pureJs);

	/**
	 * If we are attached, this element exists, otherwise this is null (or we
	 * must be synced).
	 */
	protected HTMLElement element;

	/**
	 * Attached or detached: these are tag, attrs and children. If tag null, we
	 * are a non-API tag.
	 */
	private String tag;
	private Map<AName, String> attrs;
	private Map<String, String> style;
	private Map<String, EventListener<?>> listeners;
	private List<FluentHtml> childs;
	private String inner;

	/**
	 * API call for normal HTML elements. Without a parent: detached.
	 */
	protected FluentHtml(String tag, FluentHtml parent) {
		this.tag = tag;
		if (tag != null) {
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
	protected FluentHtml(HTMLElement parent) {
		element = parent;
	}

	/**
	 * Do not create but GET the body.
	 * 
	 */
	public static Body getBody() {
		return new Body(document.getBody());
	}

	/**
	 * Do not create but GET the head.
	 * 
	 */
	public static Head getHead() {
		return new Head(document.getHead());
	}

	/**
	 * Do not create but GET the dom object.
	 */
	public HTMLElement dom() {
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
	protected static <T extends FluentHtml> T dom(String id, Class<? extends FluentHtml> classs) {
		HTMLElement found = document.getElementById("id");
		if (!found.getTagName().equals(classs.getSimpleName().toLowerCase())) {
			throw new IllegalArgumentException("Requested non-existing dom with id=" + id + ": tagname="
					+ found.getTagName() + " requesting tagname=" + classs.getSimpleName().toLowerCase());
		}
		return (T) new FluentHtml(found);
	}

	public FluentHtml inner(String innerHtml) {
		this.inner = innerHtml;
		if (element != null) {
			element.setInnerHTML(innerHtml);
		}
		return this;
	}

	public String inner() {
		return this.inner;
	}

	public FluentHtml listen(String event, EventListener<?> listener) {
		if (listeners == null) {
			listeners = new HashMap<>();
		}
		listeners.put(event, listener);
		if (element != null) {
			element.addEventListener(event, listener);
		}
		return this;
	}

	public EventListener<?> listen(String event) {
		if (listeners == null) {
			return null;
		}
		return listeners.get(event);
	}

	public String css(String property) {
		if (style == null) {
			return null;
		}
		return style.get(property);
	}

	public FluentHtml css(String property, String value) {
		if (style == null) {
			style = new HashMap<>();
		}
		style.put(property, value);
		if (element != null) {
			element.getStyle().setProperty(property, value);
		}
		return this;
	}

	public String attr(AName name) {
		if (attrs == null) {
			return null;
		}
		return attrs.get(name);
	}

	public FluentHtml attr(AName name, String value) {
		if (attrs == null) {
			attrs = new HashMap<>();
		}
		attrs.put(name, value);
		if (element != null) {
			element.setAttribute(name.nameValid(), value);
		}
		return this;
	}

	public String id() {
		return attr(AName.id);
	}

	public FluentHtml id(String string) {
		return attr(AName.id, string);
	}

	public String classs() {
		return attr(AName.classs);
	}

	public FluentHtml classs(String string) {
		return attr(AName.classs, string);
	}

	public FluentHtml add(List<? extends FluentHtml> items) {
		for (FluentHtml item : items) {
			add(item);
		}
		return this;
	}

	public FluentHtml add(FluentHtml[] items) {
		for (FluentHtml item : items) {
			add(item);
		}
		return this;
	}

	public FluentHtml addd(FluentHtml... items) {
		for (FluentHtml item : items) {
			add(item);
		}
		return this;
	}

	public FluentHtml add(Str<FluentHtml> streamy) {
		add(streamy.collectToList());
		return this;
	}

	public <T> FluentHtml add(T model, ReactM<T> method) {
		return new ReactMC<T>(model, method);
	}

	/**
	 * Add objects - only for creating own objects, please use the fluent
	 * methods.
	 * 
	 */
	public FluentHtml add(FluentHtml add) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		childs.add(add);
		if (element != null) { // 'render'
			addElementFrom(add);
		}
		return this;
	}

	private void addElementFrom(FluentHtml add) {
		if (add.tag == null) { // go deeper
			addElementFrom(((ReactC) add).generate());
			return;
		}
		if (add.element != null) { // both attached
			element.appendChild(add.element);
			add.sync();
			return;
		}

		// create node
		add.element = document.createElement(add.tag);
		if (add.attrs != null) {
			for (AName name : add.attrs.keySet()) {
				add.element.setAttribute(name.nameValid(), add.attrs.get(name));
			}
		}
		if (add.style != null) {
			for (String name : add.style.keySet()) {
				add.element.getStyle().setProperty(name, add.style.get(name));
			}
		}
		if (add.inner != null) {
			add.inner(add.inner);
		}

		element.appendChild(add.element);
		if (add.childs != null) {
			for (FluentHtml child : add.childs) {
				add.addElementFrom(child);
			}
		}
		if (add.listeners != null) {
			for (String name : add.listeners.keySet()) {
				add.element.addEventListener(name, add.listeners.get(name));
			}

		}

	}

	private static boolean compare(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equals(str2));
	}

	public void sync() { // status: we are attached, check whether virtual and
							// real DOM are the same
		// Tag
		if (!compare(tag, element.getTagName())) {
			element = document.createElement(tag);
		}
		// innerHtml
		if (!compare(inner, element.getInnerHTML())) {
			element.setInnerHTML(inner);
		}
		// Attrs
		NamedNodeMap<Attr> elementAttrs = element.getAttributes();
		for (AName name : attrs.keySet()) { // add or adjust
			String value = attrs.get(name);

			Attr existing = elementAttrs.getNamedItem(name.nameValid());
			if (existing == null) {
				element.setAttribute(name.nameValid(), value);
			} else {
				if (!existing.getValue().equals(value)) {
					existing.setValue(value);
				}
			}
		}
		for (int x = 0; x < elementAttrs.getLength(); x++) { // remove
			Attr elementAttr = elementAttrs.item(x);
			if (!attrs.containsKey(elementAttr.getName())) {
				elementAttr.delete();
			}
		}
		// Style
		CSSStyleDeclaration elementStyle = element.getStyle();
		for (String name : style.keySet()) { // add or adjust
			String value = style.get(name);

			String existing = elementStyle.getPropertyValue(name);
			if (existing == null) {
				elementStyle.setProperty(name, value);
			} else {
				if (!existing.equals(value)) {
					elementStyle.removeProperty(name);
					// TODO is remove necessary?
					elementStyle.setProperty(name, value);
				}
			}
		}
		// TODO hier gebleven
		private Map<String, EventListener<?>> listeners;
		private List<FluentHtml> childs;

		// TODO: functies toevoegen voor removeListener() e.d.
		System.out.println("I'm rerendering");
		// https://medium.com/@deathmood/how-to-write-your-own-virtual-dom-ee74acc13060#.wc4ze6sj1
	}

	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:

	public Input input(String type, String name) {
		Input result = new Input(this);
		result.attr(AName.type, type);
		result.attr(AName.name, name);
		return result;
	}

	public Button button(String text) {
		Button result = new Button(this);
		result.inner(text);
		return result;
	}

	public Li li(String text) {
		Li result = new Li(this);
		result.inner(text);
		return result;
	}

	public Div div() {
		return new Div(this);
	}

	public Div div(List<? extends FluentHtml> list) {
		return (Div) div().add(list);
	}

	public Div div(FluentHtml[] list) {
		return (Div) div().add(list);
	}

	public Div divd(FluentHtml... fluentHtmls) {
		return div(fluentHtmls);
	}

	// UNORDERED YET

	public FluentHtml area() {
		return new FluentHtml("area", this);
	}

	public FluentHtml base() {
		return new FluentHtml("base", this);
	}

	public FluentHtml br() {
		return new FluentHtml("br", this);
	}

	public FluentHtml col() {
		return new FluentHtml("col", this);
	}

	public FluentHtml document() {
		return new FluentHtml("!DOCTYPE html", this);
	}

	public FluentHtml embed() {
		return new FluentHtml("embed", this);
	}

	public FluentHtml hr() {
		return new FluentHtml("hr", this);
	}

	public FluentHtml img() {
		return new FluentHtml("img", this);
	}

	public FluentHtml input() {
		return new FluentHtml("input", this);
	}

	public FluentHtml keygen() {
		return new FluentHtml("keygen", this);
	}

	public FluentHtml link() {
		return new FluentHtml("link", this);
	}

	public FluentHtml meta() {
		return new FluentHtml("meta", this);
	}

	public FluentHtml param() {
		return new FluentHtml("param", this);
	}

	public FluentHtml source() {
		return new FluentHtml("source", this);
	}

	public FluentHtml track() {
		return new FluentHtml("track", this);
	}

	public FluentHtml wbr() {
		return new FluentHtml("wbr", this);
	}

	public FluentHtml a(String text) {
		return new FluentHtml("a", this).inner(text);
	}

	public FluentHtml abbr() {
		return new FluentHtml("abbr", this);
	}

	public FluentHtml address() {
		return new FluentHtml("address", this);
	}

	public FluentHtml article() {
		return new FluentHtml("article", this);
	}

	public FluentHtml aside() {
		return new FluentHtml("aside", this);
	}

	public FluentHtml audio() {
		return new FluentHtml("audio", this);
	}

	public FluentHtml b(String text) {
		FluentHtml result = new FluentHtml("b", this);
		result.inner(text);
		return result;
	}

	public FluentHtml bdi(String text) {
		return new FluentHtml("bdi", this).inner(text);
	}

	public FluentHtml bdo() {
		return new FluentHtml("bdo", this);
	}

	public FluentHtml bdo(String text) {
		return new FluentHtml("bdo", this).inner(text);
	}

	public FluentHtml blockquote() {
		return new FluentHtml("blockquote", this);
	}

	public FluentHtml blockquote(String text) {
		return new FluentHtml("blockquote", this).inner(text);
	}

	public FluentHtml body() {
		return new FluentHtml("body", this);
	}

	public FluentHtml canvas() {
		return new FluentHtml("canvas", this);
	}

	public FluentHtml caption() {
		return new FluentHtml("caption", this);
	}

	public FluentHtml caption(String text) {
		return new FluentHtml("caption", this).inner(text);
	}

	public FluentHtml cite() {
		return new FluentHtml("cite", this);
	}

	public FluentHtml cite(String text) {
		return new FluentHtml("cite", this).inner(text);
	}

	public FluentHtml code() {
		return new FluentHtml("code", this);
	}

	public FluentHtml colgroup() {
		return new FluentHtml("colgroup", this);
	}

	public FluentHtml datalist() {
		return new FluentHtml("datalist", this);
	}

	public FluentHtml dd(String text) {
		return new FluentHtml("dd", this).inner(text);
	}

	public FluentHtml del(String text) {
		return new FluentHtml("del", this).inner(text);
	}

	public FluentHtml details() {
		return new FluentHtml("details", this);
	}

	public FluentHtml dfn(String text) {
		return new FluentHtml("dfn", this).inner(text);
	}

	public FluentHtml dialog() {
		return new FluentHtml("dialog", this);
	}

	public FluentHtml dialog(String text) {
		return new FluentHtml("dialog", this).inner(text);
	}

	public FluentHtml dl() {
		return new FluentHtml("dl", this);
	}

	public FluentHtml dt() {
		return new FluentHtml("dt", this);
	}

	public FluentHtml dt(String text) {
		return new FluentHtml("dt", this).inner(text);
	}

	public FluentHtml em() {
		return new FluentHtml("em", this);
	}

	public FluentHtml em(String text) {
		return new FluentHtml("em", this).inner(text);
	}

	public FluentHtml fieldset() {
		return new FluentHtml("fieldset", this);
	}

	public FluentHtml figcaption() {
		return new FluentHtml("figcaption", this);
	}

	public FluentHtml figcaption(String text) {
		return new FluentHtml("figcaption", this).inner(text);
	}

	public FluentHtml figure() {
		return new FluentHtml("figure", this);
	}

	public FluentHtml footer() {
		return new FluentHtml("footer", this);
	}

	public FluentHtml form() {
		return new FluentHtml("form", this);
	}

	public FluentHtml h1(String text) {
		return new FluentHtml("h1", this).inner(text);
	}

	public FluentHtml h2(String text) {
		return new FluentHtml("h2", this).inner(text);
	}

	public FluentHtml h3(String text) {
		return new FluentHtml("h3", this).inner(text);
	}

	public FluentHtml h4(String text) {
		return new FluentHtml("h4", this).inner(text);
	}

	public FluentHtml h5(String text) {
		return new FluentHtml("h5", this).inner(text);
	}

	public FluentHtml h6(String text) {
		return new FluentHtml("h6", this).inner(text);
	}

	public FluentHtml header() {
		return new FluentHtml("header", this);
	}

	public FluentHtml i() {
		return new FluentHtml("i", this);
	}

	public FluentHtml i(String text) {
		return new FluentHtml("i", this).inner(text);
	}

	public FluentHtml iframe() {
		return new FluentHtml("iframe", this);
	}

	public FluentHtml ins(String text) {
		return new FluentHtml("ins", this).inner(text);
	}

	public FluentHtml kbd() {
		return new FluentHtml("kbd", this);
	}

	public FluentHtml label(String text) {
		return new FluentHtml("label", this).inner(text);
	}

	public FluentHtml legend(String text) {
		return new FluentHtml("legend", this).inner(text);
	}

	public FluentHtml main() {
		return new FluentHtml("main", this);
	}

	public FluentHtml map() {
		return new FluentHtml("map", this);
	}

	public FluentHtml mark() {
		return new FluentHtml("mark", this);
	}

	public FluentHtml menu() {
		return new FluentHtml("menu", this);
	}

	public FluentHtml menuitem() {
		return new FluentHtml("menuitem", this);
	}

	public FluentHtml meter() {
		return new FluentHtml("meter", this);
	}

	public FluentHtml nav() {
		return new FluentHtml("nav", this);
	}

	public FluentHtml noscript() {
		return new FluentHtml("noscript", this);
	}

	public FluentHtml object() {
		return new FluentHtml("object", this);
	}

	public FluentHtml ol() {
		return new FluentHtml("ol", this);
	}

	public FluentHtml optgroup() {
		return new FluentHtml("optgroup", this);
	}

	public FluentHtml option(String text) {
		return new FluentHtml("option", this).inner(text);
	}

	public FluentHtml output() {
		return new FluentHtml("output", this);
	}

	public FluentHtml p() {
		return new FluentHtml("p", this);
	}

	public FluentHtml p(String text) {
		return new FluentHtml("p", this).inner(text);
	}

	public FluentHtml pre(String text) {
		return new FluentHtml("pre", this).inner(text);
	}

	public FluentHtml progress() {
		return new FluentHtml("progress", this);
	}

	public FluentHtml q(String text) {
		return new FluentHtml("q", this).inner(text);
	}

	public FluentHtml rp() {
		return new FluentHtml("rp", this);
	}

	public FluentHtml rt() {
		return new FluentHtml("rt", this);
	}

	public FluentHtml ruby() {
		return new FluentHtml("ruby", this);
	}

	public FluentHtml s(String text) {
		return new FluentHtml("s", this).inner(text);
	}

	public FluentHtml samp() {
		return new FluentHtml("samp", this);
	}

	public FluentHtml script() {
		return new FluentHtml("script", this);
	}

	public FluentHtml section() {
		return new FluentHtml("section", this);
	}

	public FluentHtml select() {
		return new FluentHtml("select", this);
	}

	public FluentHtml small() {
		return new FluentHtml("small", this);
	}

	public FluentHtml small(String text) {
		return new FluentHtml("small", this).inner(text);
	}

	public FluentHtml span() {
		return new FluentHtml("span", this);
	}

	public FluentHtml span(String text) {
		return new FluentHtml("span", this).inner(text);
	}

	public FluentHtml strong(String text) {
		return new FluentHtml("strong", this).inner(text);
	}

	public FluentHtml style() {
		return new FluentHtml("style", this);
	}

	public FluentHtml sub(String text) {
		return new FluentHtml("sub", this).inner(text);
	}

	public FluentHtml summary(String text) {
		return new FluentHtml("summary", this).inner(text);
	}

	public FluentHtml sup(String text) {
		return new FluentHtml("sup", this).inner(text);
	}

	public FluentHtml table() {
		return new FluentHtml("table", this);
	}

	public FluentHtml tbody() {
		return new FluentHtml("tbody", this);
	}

	public FluentHtml td() {
		return new FluentHtml("td", this);
	}

	public FluentHtml td(String text) {
		return new FluentHtml("td", this).inner(text);
	}

	public FluentHtml textarea() {
		return new FluentHtml("textarea", this);
	}

	public FluentHtml tfoot() {
		return new FluentHtml("tfoot", this);
	}

	public FluentHtml th() {
		return new FluentHtml("th", this);
	}

	public FluentHtml th(String text) {
		return new FluentHtml("th", this).inner(text);
	}

	public FluentHtml thead() {
		return new FluentHtml("thead", this);
	}

	public FluentHtml time() {
		return new FluentHtml("time", this);
	}

	public FluentHtml title() {
		return new FluentHtml("title", this);
	}

	public FluentHtml title(String text) {
		return new FluentHtml("title", this).inner(text);
	}

	public FluentHtml tr() {
		return new FluentHtml("tr", this);
	}

	public FluentHtml u() {
		return new FluentHtml("u", this);
	}

	public FluentHtml u(String text) {
		return new FluentHtml("u", this).inner(text);
	}

	public FluentHtml ul() {
		return new FluentHtml("ul", this);
	}

	public FluentHtml var() {
		return new FluentHtml("var", this);
	}

	public FluentHtml video() {
		return new FluentHtml("video", this);
	}

}
