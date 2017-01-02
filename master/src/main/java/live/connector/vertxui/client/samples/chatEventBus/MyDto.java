package live.connector.vertxui.client.samples.chatEventBus;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

public class MyDto {

	// put this mapper here for client-side Json translation...
	public static interface Mapper extends ObjectMapper<MyDto> {
	}

	public String color;

	public MyDto() {
	}

	public MyDto(String color) {
		this.color = color;
	}


}
