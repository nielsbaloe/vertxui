package live.connector.vertxui.client;

import elemental.json.JsonObject;

public interface EventBusReplyReceive {

	public void handle(JsonObject error, JsonObject message);

}