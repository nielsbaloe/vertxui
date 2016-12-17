package live.connector.vertxui.core;

import org.teavm.flavour.json.JSON;
import org.teavm.flavour.json.test.TeaVMJSONRunner;

import io.vertx.core.Handler;
import live.connector.vertxui.fluentHtml.FluentHtml;

/**
 * A Vert.X sockJs wrapper for in javascript.
 * 
 * @author Niels Gorisse
 *
 */
public class EventBus {

	static {
		FluentHtml.getHead().script("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");
	}

	public EventBus(String serverAddress) {
		// TODO Auto-generated constructor stub
	}

	public void onOpen(Handler<JSON> handler) {
		// TODO Auto-generated method stub
	}

	public void registerHandler(String address, Handler<String> handler) {
		// TODO
	}

	public String publish(String address, String message) {
		// TODO Auto-generated method stub
		return null;
	}

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
