package live.connector.vertxui.client;

public class console {

	public native static void log(Object message) /*-{
													console.log(message);
													}-*/;
}
