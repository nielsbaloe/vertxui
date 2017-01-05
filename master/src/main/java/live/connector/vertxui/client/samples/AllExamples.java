package live.connector.vertxui.client.samples;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;

import live.connector.vertxui.client.samples.chatEventBus.Dto;

public class AllExamples {

	// Mapper for json-object
	public interface DtoMap extends ObjectMapper<Dto> {
	}

	// Mapper for json-object
	public static DtoMap dto = GWT.create(DtoMap.class);

}
