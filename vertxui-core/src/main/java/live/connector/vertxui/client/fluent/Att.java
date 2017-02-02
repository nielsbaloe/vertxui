package live.connector.vertxui.client.fluent;

public enum Att {

	hidden, high, href, hreflang, icon, id, ismap, itemprop, //
	keytype, kind, label, lang, language, list, loop, low, manifest, //
	max, maxlength, media, method, min, multiple, novalidate, open, //
	optimum, pattern, ping, paceholder, poster, preload, pubdate, radiogroup, //
	readonly, rel, required, reversed, role, rows, rowspan, sandbox, spellcheck, scope, //
	scoped, seamless, selected, shape, size, sizes, span, src, srcdoc, srclang, srcset, //
	start, step, style, summary, tabindex, target, title, type, usemap, value, width, wrap, //
	border, buffered, challenge, charset, checked, cite, color, cols, colspan, content, //
	contenteditable, contextmenu, controls, coords, data, datetime, defer, dir, //
	dirnme, disable, download, draggable, dropzone, enctype, form, formaction, //
	headers, height, accept, accesskey, action, align, alt, async, //
	autocomplete, autofocus, autoplay, autosave, //
	name_, for_, default_, class_, http_equiv, accept_charset, text, dataProvide, dataRole, placeholder;

	public String nameValid() {
		switch (this) {
		case http_equiv: // java: '-' illegal
			return "http-equiv";
		case accept_charset: // java: '-' illegal
			return "accept-charset";
		case for_: // java: reserved keyword
			return "for";
		case default_:// java: reserved keyword
			return "default";
		case class_:// java: reserved keyword
			return "class";
		case name_:// javascript: reserved keyword
			return "name";
		case dataProvide:
			return "data-provide";
		case dataRole:
			return "data-role";
		default:
			return name();
		}
	}

	public static Att valueOfValid(String name) {
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
		case "name":// javascript: reserved keyword
			return name_;
		case "data-provide":
			return dataProvide;
		case "data-role":
			return dataRole;
		default:
			return valueOf(name);
		}
	}
};
