package live.connector.vertxui.client.transport;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.xhr.client.XMLHttpRequest;

import live.connector.vertxui.client.transport.EventBus.Handler;

public class Pojofy {

	@SuppressWarnings("unchecked")
	public final static <T, R> void ajax(String protocol, String url, T model, ObjectMapper<T> inMapper,
			ObjectMapper<R> outMapper, Handler<R> replyHandler) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if (xhr.getReadyState() == 4 && xhr.getStatus() == 200) {
				R result = null;
				if (outMapper == null) { // no outMapper? -> use string
					result = (R) xhr.getResponseText();
				} else {
					result = outMapper.read(xhr.getResponseText());
				}
				replyHandler.handle(result);
			}
		});
		xhr.open(protocol, url);
		if (model == null) {
			xhr.send();
		} else {
			xhr.send(inMapper.write(model));
		}
	}

}
