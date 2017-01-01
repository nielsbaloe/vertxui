package live.connector.vertxui.client;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.events.EventListener;
import elemental.js.util.Json;
import elemental.json.JsonObject;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * A Vert.X sockJs wrapper.
 * 
 * @author Niels Gorisse
 *
 */
public class EventBus extends JavaScriptObject {

	protected EventBus() {
	}

	static {
		SockJS.ensureStaticLoading();
		Fluent.head.scriptSync("https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");

	}

	public final native static EventBus create(String address, String[] options) /*-{
																					return new EventBus(address, options);
																					}-*/;

	public final native void onopen(EventListener listener)/*-{
															this.onopen = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);		
															}-*/;

	public final native void send(String address, String message, String[] headers, EventBusReplyReceive receiver)/*-{
																													this.send(address,message,headers, 
																														function(a,b) 
																													{ @live.connector.vertxui.client.EventBus::doit(Llive/connector/vertxui/client/EventBusReplyReceive;Lelemental/json/JsonObject;Lelemental/json/JsonObject;)
																													(receiver,a,b); }
																													);
																													}-*/;

	public final native void registerHandler(String address, String[] headers, EventBusReplyReceive receiver)/*-{
																												this.registerHandler(address,headers, 
																													function(a,b) 
																												{ @live.connector.vertxui.client.EventBus::doit(Llive/connector/vertxui/client/EventBusReplyReceive;Lelemental/json/JsonObject;Lelemental/json/JsonObject;)
																												(receiver,a,b); }
																												   );
																												}-*/;

	private static void doit(EventBusReplyReceive handler, JsonObject error, JsonObject message) {
		handler.handle(error, message);
	}

	public final native void unregisterHandler(String address, String[] headers, EventBusReplyReceive receiver)/*-{
																												this.unregisterHandler(address,headers, function(a,b) 
																												{ @live.connector.vertxui.client.EventBus::doit(Llive/connector/vertxui/client/EventBusReplyReceive;Lelemental/json/JsonObject;Lelemental/json/JsonObject;)
																												(receiver,a,b); }
																												);
																												}-*/;

	public final native void publish(String address, String message, String[] headers)/*-{
																						this.publish(address,message,headers);
																						}-*/;

	public final native void onclose(EventListener listener) /*-{
																this.onclose = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
																}-*/;

	public final native void onerror(EventListener listener) /*-{
																this.onerror = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
																}-*/;

	// Extra utils - TODO test!!
	// if necessary, also check:
	// https://github.com/hpehl/piriti
	// https://github.com/heroandtn3/bGwtGson

	public final <T extends JavaScriptObject> void publish(T model, String[] headers) {
		publish(model.getClass().getName(), Json.stringify(model), headers);
	}

	public final <T extends JavaScriptObject> void consumer(Class<T> classs, Handler<T> handler) {
		registerHandler(classs.getName(), null, (error, message) -> {
			handler.handle(Json.parse(message.asString()));
		});
	}

	public static interface Handler<T> {
		void handle(T object);
	}

}
