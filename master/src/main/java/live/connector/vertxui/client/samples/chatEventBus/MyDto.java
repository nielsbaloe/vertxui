package live.connector.vertxui.client.samples.chatEventBus;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

public class MyDto {

	public static String classs = "live.connector.vertxui.client.samples.chatEventBus.MyDto";
	public static interface Mapper extends ObjectMapper<MyDto> {
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String color;

}
