package live.connector.vertxui.fluidhtml;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLElement;

public class Head extends Html {

	protected Head(HTMLElement head) {
		super(head);
	}

	@JSBody(params = { "filename" }, script = "{var fileref=document.createElement('script');"
			+ "fileref.setAttribute('type','text/javascript');" + "fileref.setAttribute('src', filename);"
			+ "document.getElementsByTagName('head')[0].appendChild(fileref);console.log('consoletest');}")
	public static native void js(String pureJs);

	public void src(String js) {
//		Html result = new Html("script", this);
//		result.attribute("type", "text/javascript");
//		result.attribute("async", "false");
//		result.attribute("src", js);

		 js(js);

		// js("document.write('<script type=\"text/javascript\"
		// src=\"http://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js\"><//script>');console.log('consoletest');");

		// HTMLElement src = document.createElement("script");
		// src.setAttribute("style", "text/javascript");
		// src.setAttribute("src", js);
		// document.getHead().appendChild(src);

		// document.getHead().setInnerHTML("<script type=\"text/javascript\"
		// src=\"" + js + "\"></script>");

	}

	public void style(String css) {
		Html result = new Html("link", this);
		result.attribute("rel", "stylesheet");
		result.attribute("href", css);
	}

}
