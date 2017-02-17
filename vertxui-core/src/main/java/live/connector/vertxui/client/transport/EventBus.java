package live.connector.vertxui.client.transport;

import java.util.function.BiConsumer;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.events.EventListener;
import elemental.json.JsonObject;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * A Vert.X sockJs EventBus wrapper for
 * https://github.com/vert-x3/vertx-bus-bower . WARNING: send() does not go to
 * the server but to one randomly chosen connected machine, which can be either
 * the server or any browser that has connected so far - be aware of that!!
 * 
 * @author Niels Gorisse
 *
 */
public class EventBus extends JavaScriptObject {

	protected EventBus() {
	}

	static {
		SockJS.ensureStaticLoadingJsFile();
		Fluent.head.scriptSync("https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");
	}

	public final native static EventBus create(String address, String[] options) /*-{
																					return new window.top.EventBus(address, options);
																					}-*/;

	public final native void onopen(EventListener listener)/*-{
															this.onopen = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);		
															}-*/;

	/**
	 * Warning: the thing you send can go to anyone connected to the eventbus,
	 * including other browsers that are connected. So please handle with care!
	 */
	/**
	 * @param address
	 *            the address
	 * @param message
	 *            the message
	 * @param headers
	 *            the headers
	 * @param receiver
	 *            the callback
	 */
	public final native void send(String address, String message, JsonObject headers,
			BiConsumer<JsonObject, JsonObject> receiver)/*-{
														this.send(address,message,headers, 
														function(a,b) 
														{ @live.connector.vertxui.client.transport.EventBus::doit(Ljava/util/function/BiConsumer;Lelemental/json/JsonObject;Lelemental/json/JsonObject;)
														(receiver,a,b); }
														);
														}-*/;

	public final native void registerHandler(String address, JsonObject headers,
			BiConsumer<JsonObject, JsonObject> receiver)/*-{
														this.registerHandler(address,headers, 
														function(a,b) 
														{ @live.connector.vertxui.client.transport.EventBus::doit(Ljava/util/function/BiConsumer;Lelemental/json/JsonObject;Lelemental/json/JsonObject;)
														(receiver,a,b); }
														);
														}-*/;

	private static void doit(BiConsumer<JsonObject, JsonObject> handler, JsonObject error, JsonObject message) {
		handler.accept(error, message);
	}

	public final native void unregisterHandler(String address, JsonObject headers,
			BiConsumer<JsonObject, JsonObject> receiver)/*-{
														this.unregisterHandler(address,headers, function(a,b) 
														{ @live.connector.vertxui.client.transport.EventBus::doit(Ljava/util/function/BiConsumer;Lelemental/json/JsonObject;Lelemental/json/JsonObject;)
														(receiver,a,b); }
														);
														}-*/;

	public final native void publish(String address, String message, JsonObject headers)/*-{
																						this.publish(address,message,headers);
																						}-*/;

	public final native void onclose(EventListener listener) /*-{
																this.onclose = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
																}-*/;

	public final native void onerror(EventListener listener) /*-{
																this.onerror = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
																}-*/;

}
