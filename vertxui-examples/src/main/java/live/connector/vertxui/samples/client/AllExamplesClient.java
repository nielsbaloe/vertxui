package live.connector.vertxui.samples.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;

public class AllExamplesClient {

	public final static String figwheelyLocation = "/figwheely.js";

	// Mapper for json-object
	public interface DtoMap extends ObjectMapper<Dto> {
	}

	// Mapper for json-object
	public final static DtoMap dto = GWT.isClient() ? GWT.create(DtoMap.class) : null;

}
