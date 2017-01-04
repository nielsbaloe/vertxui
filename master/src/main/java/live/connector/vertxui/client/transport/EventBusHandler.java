package live.connector.vertxui.client.transport;

import elemental.json.JsonObject;

public interface EventBusHandler {

	public void handle(JsonObject error, JsonObject message);

}