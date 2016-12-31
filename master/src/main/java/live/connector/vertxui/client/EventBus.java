package live.connector.vertxui.client;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.json.JsonObject;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * A Vert.X sockJs wrapper for in javascript.
 * 
 * @author Niels Gorisse
 *
 */
public class EventBus extends JavaScriptObject {

	protected EventBus() {
	}

	static {
		SockJS.ensureStaticLoading();
		Fluent.scriptSyncEval("https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");
		// TODO mail vertx why not a CDN version?
	}

	public final native static EventBus create(String address, String[] options) /*-{
																					return new EventBus(address, options);
																					}-*/;

	// TODO callbacks for onopen and send, and uncommented methods
	public final native void onopen(EventListener listener)/*-{
															this.onopen = @live.connector.vertxui.client.EventBus::getHandlerFor(Lelemental/events/EventListener;)(listener);
															}-*/;

	public final native void send(String address, String message, String[] headers, EventListener callback)/*-{
																											this.send(address,message,headers, @live.connector.vertxui.client.EventBus::getHandlerFor(Lelemental/events/EventListener;)(callback) );
																											}-*/;

	public final native void registerHandler(String address, String[] headers, EventBusReplyReceive receiver)/*-{
																												this.registerHandler(address,headers, function(a,b) 
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

	// public final native void onClose(Runnable object);
	//
	// public final native void onError(Handler<String> callback);
	//
	// /**
	// * Push a DTO to the server, where we registered this class to be received
	// * at the right place.
	// */
	// public <T> void publish(T model) {
	// publish(model.getClass().getName(), model);
	// }
	//
	// public <T> void publish(String address, T model, String[] headers) {
	// publish(address, Json.stringify(model), headers);
	// }
	//
	// /**
	// * Consume a DTO from the server, where we registered this class to be
	// sent
	// * to here.
	// *
	// * @param classs
	// * the class of the dto that needs to be sended.
	// * @param handler
	// * a method that handles the dto
	// */
	// public <T> void register(Class<T> classs, Handler<T> handler) {
	// register(classs.getName(), null, handler);
	// }
	//
	// public <T> void register(String address, Class<T> classs, Handler<T>
	// handler) {
	// // registerHandler(address, null, string -> {
	// // handler.handle(SONRunner.deserialize(string, classs));
	// // });
	// }

	// lambda stuff copied from JsElementalMixinBase

	private native static JavaScriptObject ccreateHandler(EventListener listener) /*-{
																					var handler = listener.handler;
																					if (!handler) {
																					handler = $entry(function(event) {
																					@live.connector.vertxui.client.EventBus::handleEvent(Lelemental/events/EventListener;Lelemental/events/Event;)(listener, event);
																					});
																					handler.listener = listener;
																					// TODO(knorton): Remove at Christmas when removeEventListener is removed.
																					listener.handler = handler;
																					}
																					return handler;
																					}-*/;

	private static void handleEvent(EventListener listener, Event event) {
		listener.handleEvent(event);
	}

	private static JavaScriptObject getHandlerFor(EventListener listener) {
		return listener == null ? null : ccreateHandler(listener);
	}

}
