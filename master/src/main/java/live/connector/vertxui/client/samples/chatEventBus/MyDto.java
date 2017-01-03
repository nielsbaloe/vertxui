package live.connector.vertxui.client.samples.chatEventBus;

import java.util.ArrayList;
import java.util.List;

public class MyDto {

	public String color;

	public Car car = Car.Volvo;

	public List<String> options = new ArrayList<>();

	public MyDto() { // empty constructor needed for serialization
		options.add("lether chairs");
	}

	public MyDto(String color) {
		this.color = color;
	}

	public enum Car {
		Toyota, Volvo, Honda
	}

}
