package live.connector.vertxui.client;

import static live.connector.vertxui.client.fluent.Fluent.console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

import elemental.events.EventListener;
import elemental.json.JsonObject;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.samples.chatEventBus.MyDto;
import live.connector.vertxui.client.samples.chatEventBus.MyDto.Mapper;

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

	public final <T, R> void send(String address, T model, String[] headers, Handler<R> replyHandler) {
		Mapper mapper = GWT.create(Mapper.class);
		// TODO: mapper is linked to MyDto...
		String message = mapper.write((MyDto) model);
		send(address, message, headers, (e, m) -> {
			@SuppressWarnings("unchecked")
			R result = (R) mapper.read(m.get("body"));
			replyHandler.handle(result);
		});
	}

	public final <T> void publish(String modelClass, T model, String[] headers) {
		Mapper mapper = GWT.create(Mapper.class);
		publish(modelClass, mapper.write((MyDto) model), headers);
	}

	public final <T> void consumer(String modelClass, String[] headers, Handler<T> handler) {
		Mapper mapper = GWT.create(Mapper.class);
		registerHandler(modelClass, headers, (error, message) -> {
			console.log("yess consuming " + message);
			@SuppressWarnings("unchecked")
			T result = (T) mapper.read(message.get("body"));
			handler.handle(result);
		});
	}

	public static interface Handler<T> {
		void handle(T object);
	}

}
