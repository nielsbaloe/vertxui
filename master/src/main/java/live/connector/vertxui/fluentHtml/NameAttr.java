package live.connector.vertxui.fluentHtml;

public enum NameAttr {
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
	for_, default_, class_, http_equiv, accept_charset;

	public String nameValid() {
		switch (this) {
		case http_equiv:
			return "http-equiv";
		case accept_charset:
			return "accept-charset";
		case for_:
			return "for";
		case default_:
			return "default";
		case class_:
			return "class";
		default:
			return name();
		}
	}

	public static NameAttr valueOfValid(String name) {
		switch (name) {
		case "http-equiv":
			return http_equiv;
		case "accept-charset":
			return accept_charset;
		case "for":
			return for_;
		case "default":
			return default_;
		case "class":
			return class_;
		default:
			return valueOf(name);
		}
	}

};
