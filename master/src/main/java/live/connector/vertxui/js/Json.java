package live.connector.vertxui.js;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public abstract class Json implements JSObject {

	private Json() {
	}

	@JSBody(params = { "object" }, script = "return JSON.stringify(object);")
	public static native String stringify(JSObject object);

	@JSBody(params = { "string" }, script = "return JSON.parse(string);")
	public static native Json parse(String string);

	@JSBody(params = { "key" }, script = "return this.key;")
	public abstract String get(String key);

}
