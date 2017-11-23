package live.connector.vertxui.client.fluent;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.dom.Node;
import elemental.events.MouseEvent;

/**
 * Fluent HTML is a child-based fluent notation of html. Use getDocument()
 * getBody() and getHead() to start building your GUI.
 * 
 * Adding childs is done by using the methods (like .li() ) or by some
 * constructors that can handle multiple arguments (like .div(li[]) ).
 * Attributes are set by att(), styles by css(), and listeners by listen() or
 * their appropriate helper methods like click().
 * 
 * @author Niels Gorisse
 *
 */
public class Fluent extends FluentBase {

	protected Fluent(Node parent) {
		super(parent);
	}

	protected Fluent(String tag, Fluent parent) {
		super(tag, parent);
	}

	public Fluent input() {
		return new Fluent("INPUT", this);
	}

	public Fluent input(String classs) {
		return input().classs(classs);
	}

	public Fluent input(String classs, String type) {
		return input().classs(classs).att(Att.type, type);
	}

	public static Fluent Input() {
		return new Fluent("INPUT", null);
	}

	public static Fluent Input(String classs) {
		return Input().classs(classs);
	}

	public static Fluent Input(String classs, String type) {
		return Input().classs(classs).att(Att.type, type);
	}

	public Fluent button(String classs, String type, String text) {
		if (type == null) {
			throw new IllegalArgumentException(
					"You must specify the button type because the default type is 'submit'.");
		}
		return new Fluent("BUTTON", this).classs(classs).att(Att.type, type).txt(text);
	}

	public static Fluent Button(String classs, String type, String text) {
		if (type == null) {
			throw new IllegalArgumentException("You must specify the button type");
		}
		return new Fluent("BUTTON", null).classs(classs).att(Att.type, type).txt(text);
	}

	public Fluent li() {
		return new Fluent("LI", this);
	}

	public Fluent li(String classs) {
		return li().classs(classs);
	}

	public Fluent li(String classs, String text) {
		return li(classs).txt(text);
	}

	public Fluent li(Fluent... fluents) {
		return li().add(fluents);
	}

	public static Fluent Li() {
		return new Fluent("LI", null);
	}

	public static Fluent Li(String classs) {
		return Li().classs(classs);
	}

	public static Fluent Li(String classs, String inner) {
		return Li(classs).txt(inner);
	}

	public static Fluent Li(Fluent... fluents) {
		return Li().add(fluents);
	}

	public Fluent div() {
		return new Fluent("DIV", this);
	}

	public Fluent div(String classs) {
		return div().classs(classs);
	}

	public Fluent div(String classs, String inner) {
		return div(classs).txt(inner);
	}

	public Fluent div(Fluent... list) {
		return div().add(list);
	}

	public Fluent div(Stream<Fluent> stream) {
		return div().add(stream);
	}

	public Fluent div(String classs, String inner, Fluent... adds) {
		return div(classs).txt(inner).add(adds);
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
		return Div().classs(classs).txt(inner);
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

	/**
	 * If you really really have to combine plain text and DOM elements next to
	 * each other: create a html text NODE. The resulting behavior is plain
	 * text. For 99% of all cases, please please use txt(...) to set the text of
	 * an node, however this fails when there are other html tags inside too
	 * (which is badly ugly and therefore not recommended). So if you can, do
	 * not use this method, and try to avoid mixing tags and text.
	 * 
	 * @param text
	 *            the text for a textNode
	 * @return this
	 */
	public Fluent textNode(String text) {
		return new Fluent("TEXT", this).txt(text);
	}

	public Fluent col() {
		return new Fluent("COL", this);
	}

	public Fluent embed() {
		return new Fluent("EMBED", this);
	}

	public Fluent hr() {
		return new Fluent("HR", this);
	}

	public Fluent img(String src) {
		Fluent result = new Fluent("IMG", this);
		result.att(Att.src, src);
		return result;
	}

	public Fluent keygen() {
		return new Fluent("KEYGEN", this);
	}

	public Fluent meta() {
		return new Fluent("META", this);
	}

	public Fluent param() {
		return new Fluent("PARAM", this);
	}

	public Fluent source() {
		return new Fluent("SOURCE", this);
	}

	public Fluent track() {
		return new Fluent("TRACK", this);
	}

	public Fluent wbr() {
		return new Fluent("WBR", this);
	}

	public Fluent a(String classs, String inner, String href, BiConsumer<Fluent, MouseEvent> clickListener) {
		return new Fluent("A", this).classs(classs).txt(inner).att(Att.href, href).click(clickListener);
	}

	public static Fluent A(String classs, String inner, String href, BiConsumer<Fluent, MouseEvent> clickListener) {
		return new Fluent("A", null).classs(classs).txt(inner).att(Att.href, href).click(clickListener);
	}

	public Fluent abbr() {
		return new Fluent("ABBR", this);
	}

	public Fluent address() {
		return new Fluent("ADDRESS", this);
	}

	public Fluent article() {
		return new Fluent("ARTICLE", this);
	}

	public Fluent aside() {
		return new Fluent("ASIDE", this);
	}

	public Fluent aside(String classs) {
		return aside().classs(classs);
	}

	public static Fluent Aside() {
		return new Fluent("ASIDE", null);
	}

	public static Fluent Aside(String classs) {
		return Aside().classs(classs);
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
		return new Fluent("BDO", this);
	}

	public Fluent blockquote() {
		return new Fluent("BLOCKQUOTE", this);
	}

	public Fluent body() {
		return new Fluent("BODY", this);
	}

	public Fluent canvas() {
		return new Fluent("CANVAS", this);
	}

	public Fluent caption() {
		return new Fluent("CAPTION", this);
	}

	public Fluent cite() {
		return new Fluent("CITE", this);
	}

	public Fluent code() {
		return new Fluent("CODE", this);
	}

	public Fluent colgroup() {
		return new Fluent("COLGROUP", this);
	}

	public Fluent datalist() {
		return new Fluent("DATALIST", this);
	}

	public Fluent dd() {
		return new Fluent("DD", this);
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
		return new Fluent("EM", this);
	}

	public Fluent fieldset() {
		return new Fluent("FIELDSET", this);
	}

	public Fluent figcaption() {
		return new Fluent("FIGCAPTION", this);
	}

	public Fluent figure() {
		return new Fluent("FIGURE", this);
	}

	public Fluent footer() {
		return new Fluent("FOOTER", this);
	}

	public Fluent footer(String classs) {
		return footer().classs(classs);
	}

	public static Fluent Footer() {
		return new Fluent("FOOTER", null);
	}

	public static Fluent Footer(String classs) {
		return Footer().classs(classs);
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
		return h1().classs(classs).txt(text);
	}

	public static Fluent H1(String classs, String text) {
		return new Fluent("H1", null).classs(classs).txt(text);
	}

	public Fluent h2() {
		return new Fluent("H2", this);
	}

	public Fluent h2(String classs, String text) {
		return h2().classs(classs).txt(text);
	}

	public Fluent h3() {
		return new Fluent("H3", this);
	}

	public Fluent h4() {
		return new Fluent("H4", this);
	}

	public Fluent h5() {
		return new Fluent("H5", this);
	}

	public Fluent h6() {
		return new Fluent("H6", this);
	}

	public Fluent header() {
		return new Fluent("HEADER", this);
	}

	public Fluent header(String classs) {
		return header().classs(classs);
	}

	public Fluent header(String classs, Fluent... items) {
		return header().classs(classs).add(items);
	}

	public Fluent i() {
		return new Fluent("I", this);
	}

	public Fluent iframe() {
		return new Fluent("IFRAME", this);
	}

	public Fluent ins() {
		return new Fluent("INS", this);
	}

	public Fluent kbd() {
		return new Fluent("KBD", this);
	}

	public Fluent label() {
		return new Fluent("LABEL", this);
	}

	public Fluent label(Fluent... fluents) {
		return label().add(fluents);
	}

	public Fluent label(String classs) {
		return label().classs(classs);
	}

	public Fluent label(String classs, String inner) {
		return label().classs(classs).txt(inner);
	}

	public static Fluent Label() {
		return new Fluent("LABEL", null);
	}

	public static Fluent Label(String classs) {
		return Label().classs(classs);
	}

	public static Fluent Label(String classs, String inner) {
		return Label().classs(classs).txt(inner);
	}

	public Fluent legend() {
		return new Fluent("LEGEND", this);
	}

	public Fluent main() {
		return new Fluent("MAIN", this);
	}

	public Fluent map() {
		return new Fluent("MAP", this);
	}

	public Fluent mark() {
		return new Fluent("MARK", this);
	}

	public Fluent menu() {
		return new Fluent("MENU", this);
	}

	public Fluent menuitem() {
		return new Fluent("MENUITEM", this);
	}

	public Fluent meter() {
		return new Fluent("METER", this);
	}

	public Fluent nav() {
		return new Fluent("NAV", this);
	}

	public Fluent nav(String classs) {
		return nav().classs(classs);
	}

	public Fluent noscript() {
		return new Fluent("NOSCRIPT", this);
	}

	public Fluent object() {
		return new Fluent("OBJECT", this);
	}

	public Fluent ol() {
		return new Fluent("OL", this);
	}

	public Fluent optgroup() {
		return new Fluent("OPTGROUP", this);
	}

	public Fluent option() {
		return new Fluent("OPTION", this);
	}

	public static Fluent Option() {
		return new Fluent("OPTION", null);
	}

	public static Fluent Option(String classs, String inner) {
		return Option().classs(classs).txt(inner);
	}

	public Fluent output() {
		return new Fluent("OUTPUT", this);
	}

	public static Fluent P() {
		return new Fluent("P", null);
	}

	public Fluent p() {
		return new Fluent("P", this);
	}

	public Fluent p(String classs, String text) {
		return p().classs(classs).txt(text);
	}

	public Fluent pre(String classs, String text) {
		return new Fluent("PRE", this).classs(classs).txt(text);
	}

	public Fluent progress() {
		return new Fluent("PROGRESS", this);
	}

	public Fluent q() {
		return new Fluent("Q", this);
	}

	public Fluent rp() {
		return new Fluent("RP", this);
	}

	public Fluent rt() {
		return new Fluent("RT", this);
	}

	public Fluent ruby() {
		return new Fluent("RUBY", this);
	}

	public Fluent s() {
		return new Fluent("S", this);
	}

	public Fluent samp() {
		return new Fluent("SAMP", this);
	}

	private static class XMLHttpRequestSyc extends XMLHttpRequest {
		protected XMLHttpRequestSyc() {
		}

		public final native void open(String httpMethod, String url, boolean sync) /*-{
																						this.open(httpMethod, url, sync);
																						}-*/;
	}

	/**
	 * Load javascript files synchronously and evalue/execute them directly too.
	 * You can also add them at the head of the html-document with
	 * Vertx.addLibrariesJs(), which is the same but more 'to the rules'. You
	 * need this if you want to use the javascript right after loading it (which
	 * is normal in most cases).
	 * 
	 * @param jss
	 *            javascript file(s)
	 * @return this
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
	 * these libraries in your code directly, for that, use scriptSync().
	 * 
	 * @param jss
	 *            javascript urls
	 * @return this
	 */
	public Fluent script(String... jss) {
		for (String js : jss) {
			new Fluent("script", this).att(Att.type, "text/javascript").att(Att.src, js);

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

	public Fluent stylesheet(String... csss) {
		for (String css : csss) {
			new Fluent("link", this).att(Att.rel, "stylesheet").att(Att.href, css);
		}
		return this;
	}

	public Fluent section() {
		return new Fluent("SECTION", this);
	}

	public Fluent section(String classs) {
		return section().classs(classs);
	}

	public static Fluent Section() {
		return new Fluent("SECTION", null);
	}

	public static Fluent Section(String classs) {
		return Section().classs(classs);
	}

	public Fluent select() {
		return new Fluent("SELECT", this);
	}

	public static Fluent Select() {
		return new Fluent("SELECT", null);
	}

	public static Fluent Select(String classs) {
		return Select().classs(classs);
	}

	public static Fluent Select(String classs, Fluent... fluents) {
		return Select().classs(classs).add(fluents);
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
		return span().classs(classs).txt(inner);
	}

	public static Fluent Span() {
		return new Fluent("SPAN", null);
	}

	public static Fluent Span(String classs) {
		return Span().classs(classs);
	}

	public static Fluent Span(String classs, String inner) {
		return Span().classs(classs).txt(inner);
	}

	public Fluent strong() {
		return new Fluent("STRONG", this);
	}

	public Fluent strong(String classs) {
		return strong().classs(classs);
	}

	public Fluent strong(String classs, String text) {
		return strong().classs(classs).txt(text);
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
		return td().classs(classs).txt(inner);
	}

	public static Fluent Td() {
		return new Fluent("TD", null);
	}

	public static Fluent Td(String classs, String inner) {
		return Td().classs(classs).txt(inner);
	}

	public Fluent textarea() {
		return new Fluent("TEXTAREA", this);
	}

	public Fluent tfoot() {
		return new Fluent("TFOOT", this);
	}

	public Fluent th() {
		return new Fluent("TH", this);
	}

	public Fluent thead() {
		return new Fluent("THEAD", this);
	}

	public Fluent time() {
		return new Fluent("TIME", this);
	}

	public Fluent title(String classs, String inner) {
		return new Fluent("TITLE", this).classs(classs).txt(inner);
	}

	public Fluent tr() {
		return new Fluent("TR", this);
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

	public Fluent ul(Fluent... items) {
		return ul().add(items);
	}

	public Fluent ul(Stream<Fluent> stream) {
		return ul().add(stream);
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

	public static Fluent Ul(String classs, Stream<Fluent> items) {
		return Ul().classs(classs).add(items);
	}

	public static Fluent Ul(Fluent... items) {
		return Ul().add(items);
	}

	public static Fluent Ul(Stream<Fluent> items) {
		return Ul().add(items);
	}

	public Fluent var() {
		return new Fluent("VAR", this);
	}

	public Fluent video() {
		return new Fluent("VIDEO", this);
	}

}