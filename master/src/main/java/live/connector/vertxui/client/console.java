package live.connector.vertxui.client;

public class console {

	public native static void log(String message) /*-{
													console.log(message);
													}-*/;
}
