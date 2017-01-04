package live.connector.vertxui.client.samples.chatEventBus;

import java.util.ArrayList;
import java.util.List;

public class Dto {

	public String color;

	public Car car = Car.Volvo;

	public List<String> options = new ArrayList<>();

	public Dto() { // empty constructor needed for serialization
	}

	public Dto(String color) {
		this.color = color;
	}

	public enum Car {
		Toyota, Volvo, Honda
	}

}
