package live.connector.vertxui.fluentHtml;

public enum AName {
	hidden, high, href, hreflang, icon, id, ismap, itemprop, //
	keytype, kind, label, lang, language, list, loop, low, manifest, //
	max, maxlength, media, method, min, multiple, name, novalidate, open, //
	optimum, pattern, ping, paceholder, poster, preload, pubdate, radiogroup, //
	readonly, rel, required, reversed, role, rows, rowspan, sandbox, spellcheck, scope, //
	scoped, seamless, seleted, shape, size, sizes, span, src, srcdoc, srclang, srcset, //
	start, step, style, summary, tabindex, target, title, type, usemap, value, width, wrap, //
	border, buffered, challenge, charset, checked, cite, color, cols, colspan, content, //
	contenteditable, contextmenu, controls, coords, data, datetime, defer, dir, //
	dirnme, disable, download, draggable, dropzone, enctype, form, formaction, //
	headers, height, accept, accesskey, action, align, alt, async, //
	autocomplete, autofocus, autoplay, autosave, //
	forr, defaultt, classs, httpEquiv, acceptCharset;

	public String nameValid() {
		switch (this) {
		case httpEquiv:
			return "http-equiv";
		case acceptCharset:
			return "accept-charset";
		case forr:
			return "for";
		case defaultt:
			return "default";
		case classs:
			return "classs";
		default:
			return name();
		}
	}
	
	
};
