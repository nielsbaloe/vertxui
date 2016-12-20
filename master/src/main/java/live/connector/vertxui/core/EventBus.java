package live.connector.vertxui.core;

import org.teavm.flavour.json.test.TeaVMJSONRunner;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

import io.vertx.core.Handler;
import live.connector.vertxui.fluentHtml.FluentHtml;

/**
 * A Vert.X sockJs wrapper for in javascript.
 * 
 * @author Niels Gorisse
 *
 */
public class EventBus implements JSObject {

	static {
		FluentHtml.getHead().script("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");
	}

	public EventBus(String serverAddress, String[] options) {
	}

	@JSProperty()
	public void onopen(Handler<String> handler) {
		// TODO Auto-generated method stub
	}

	// function (address, headers, callback) {
	@JSProperty()
	public void registerHandler(String address, Handler<String> handler) {
		// TODO
	}

	// /e.unregisterHandler = function (address, headers, callback) {
	@JSProperty()
	public void unregisterHandler(String address, String[] headers, Handler<String> callback) {

	}

	// function (address, message, headers) {
	@JSProperty()
	public String publish(String address, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	// (address, message, headers, callback) {
	public void send(String address, String message, String something, Handler<?> reply) {
		// TODO Auto-generated method stub
	}

	public void onClose(Runnable object) {
		// TODO Auto-generated method stub
	}

	public void onError(Handler<String> object) {
		// TODO Auto-generated method stub
	}

	/**
	 * Push a DTO to the server, where we registered this class to be received
	 * at the right place.
	 */
	public <T> void publish(T model) {
		publish(model.getClass().getName(), TeaVMJSONRunner.serialize(model).asText());
	}

	/**
	 * Consume a DTO from the server, where we registered this class to be sent
	 * to here.
	 * 
	 * @param classs
	 *            the class of the dto that needs to be sended.
	 * @param handler
	 *            a method that handles the dto
	 */
	public <T> void consume(Class<T> classs, Handler<T> handler) {
		registerHandler(classs.getName(), string -> {
			handler.handle(TeaVMJSONRunner.deserialize(string, classs));
		});
	}

}
