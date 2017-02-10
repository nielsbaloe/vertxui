package live.connector.vertxui.client.fluent;

/**
 * A base for all view elements, currently Fluent and ViewOn.
 * 
 * @author Niels Gorisse
 *
 */
public interface Viewable {

	public int getCrc();

	public String getCrcString();

	public Viewable hide(boolean doit);

}
