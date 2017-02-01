package live.connector.vertxui.samples.client.todomvc;

import java.util.ArrayList;
import java.util.List;

public class Controller {

	private View view;

	private List<Model> noCommunicationYetWillBeAjax = new ArrayList<Model>();

	public void setView(View view) {
		this.view = view;
	}

	public List<Model> getModels() {
		return noCommunicationYetWillBeAjax;
	}

	public void addModel(String value) {
		noCommunicationYetWillBeAjax.add(new Model(value));
		view.setModels(noCommunicationYetWillBeAjax);
	}

}
