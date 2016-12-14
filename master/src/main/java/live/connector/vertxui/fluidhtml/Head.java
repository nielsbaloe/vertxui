package live.connector.vertxui.fluidhtml;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.html.HTMLElement;

public class Head extends FluidHtml {

	protected Head(HTMLElement head) {
		super(head);
	}

	/**
	 * Load one or more .js files.
	 * 
	 * @param jss
	 */
	public void script(String... jss) {
		for (String js : jss) {
			// This works but is not asynchronous, which can cause problems
			// Html result = new Html("script", this);
			// result.attribute("type", "text/javascript");
			// result.attribute("src", js);

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.onComplete(() -> {
				HTMLElement src = document.createElement("script");
				src.setAttribute("style", "text/javascript");
				// src.setAttribute("src", js);
				src.setAttribute("text", xhr.getResponseText());
				element.appendChild(src);
			});
			xhr.open("GET", js, false);
			xhr.send();
		}
	}

	public void stylesheet(String... csss) {
		for (String css : csss) {
			FluidHtml result = new FluidHtml("link", this);
			result.attribute("rel", "stylesheet");
			result.attribute("async", "false");
			result.attribute("href", css);
		}
	}

}
