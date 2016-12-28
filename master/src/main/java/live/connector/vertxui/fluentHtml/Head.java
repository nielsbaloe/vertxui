package live.connector.vertxui.fluentHtml;

import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.dom.Element;

public class Head extends FluentHtml {

	protected Head(Element head) {
		super(head);
	}

	/**
	 * Load one or more .js files.
	 * 
	 * @param jss
	 */

	static class XMLHttpRequestSyc extends XMLHttpRequest {

		protected XMLHttpRequestSyc() {
		}

		public final native void open(String httpMethod, String url, boolean sync) /*-{
																						this.open(httpMethod, url, sync);
																						}-*/;
	}

	public void script(String... jss) {
		for (String js : jss) {
			// This works but is not asynchronous, which can cause problems
			// Html result = new Html("script", this);
			// result.attribute("type", "text/javascript");
			// result.attribute("src", js);

			XMLHttpRequestSyc xhr = (XMLHttpRequestSyc) XMLHttpRequestSyc.create();
			xhr.setOnReadyStateChange(a -> {
				if (a.getReadyState() == XMLHttpRequest.DONE && a.getStatus() == 200) {
					Element src = document.createElement("script");
					src.setAttribute("style", "text/javascript");
					// src.setAttribute("src", js);
					src.setAttribute("text", xhr.getResponseText());
					element.appendChild(src);
				}
			});
			xhr.open("GET", js, false);
			xhr.send();
		}
	}

	public void stylesheet(String... csss) {
		for (String css : csss) {
			FluentHtml result = new FluentHtml("link", this);
			result.attr(Att.rel, "stylesheet");
			result.attr(Att.async, "false");
			result.attr(Att.href, css);
		}
	}

}
