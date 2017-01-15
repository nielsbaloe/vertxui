package live.connector.vertxui.client.fluent;

import java.util.ArrayList;
import java.util.List;

public class VirtualDomSearch {

	private static interface Filter {
		public boolean matched(Fluent f);
	}

	private static List<Fluent> filter(Fluent target, Filter filter) {
		List<Fluent> results = new ArrayList<>();
		if (filter.matched(target)) {
			results.add(target);
		}
		if (target.childs != null) {
			for (Viewable child : target.childs) {
				if (child instanceof Fluent) {
					results.addAll(filter((Fluent) child, filter));
				} else {
					results.addAll(filter(((ViewOn<?>) child).getView(), filter));
				}
			}
		}
		return results;
	}

	public static List<Fluent> getElementsById(String input, Fluent target) {
		return filter(target, f -> {
			String found = f.id();
			if (found == null || !found.equals(input)) {
				return false;
			} else {
				return true;
			}
		});
	}

	public static List<Fluent> getElementsByTagName(String input, Fluent target) {
		return filter(target, f -> {
			String found = f.tag();
			if (found == null || !found.equals(input)) {
				return false;
			} else {
				return true;
			}
		});
	}

	public static List<Fluent> getElementsByClassName(String input, Fluent target) {
		return filter(target, f -> {
			String found = f.classs();
			if (found == null || (!found.equals(input) && !found.contains(" " + input))) {
				return false;
			} else {
				return true;
			}
		});
	}

}
