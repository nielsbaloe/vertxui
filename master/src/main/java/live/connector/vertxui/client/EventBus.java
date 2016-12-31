package live.connector.vertxui.client;

import elemental.events.EventListener;
import elemental.js.dom.JsElementalMixinBase;

/**
 * A Vert.X sockJs wrapper for in javascript.
 * 
 * @author Niels Gorisse
 *
 */
public class EventBus extends JsElementalMixinBase { // JavaScriptObject

	protected EventBus() {
	}

	public final native static EventBus create(String address, String[] options) /*-{
																					return new EventBus(address, options);
																					}-*/;

	public final native void onopen(EventListener listener)/*-{
															this.onopen = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(listener);
															}-*/;

	public static interface SendReply {
		public void handle(String a, String message);
	}

	public final native void send(String address, String message, String[] headers, SendReply callback)/*-{
																										this.send(address,message,headers,callback);
																										}-*/;

	// public static interface Receiver {
	// public void handle(String status, String message);
	// }
	//
	// public final native void registerHandler(String address, String[]
	// headers, Receiver handler)/*-{
	// this.registerHandler(address,headers,handler);
	// }-*/;;
	//
	// public native void unregisterHandler(String address, String[] headers,
	// Handler<String> callback);
	//
	// public native String publish(String address, String message, String[]
	// headers);
	//
	// public native void onClose(Runnable object);
	//
	// public native void onError(Handler<String> callback);
	//
	// /**
	// * Push a DTO to the server, where we registered this class to be received
	// * at the right place.
	// */
	// public <T> void publish(T model) {
	// publish(model.getClass().getName(), model);
	// }
	//
	// public <T> void publish(String address, T model) {
	// // publish(model.getClass().getName(),
	// // SONRunner.serialize(model).asText(), null);
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

}
