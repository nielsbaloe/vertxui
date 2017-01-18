package live.connector.vertxui.server.test;

import java.util.ArrayList;
import java.util.List;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.fluent.Viewable;

public class VirtualDomSearch {

	public static interface Filter {
		public boolean matched(Fluent f);
	}

	private static void filter(Fluent target, Filter filter, List<Fluent> result) {
		if (filter.matched(target)) {
			result.add(target);
		}
		if (target.getChildren() != null) {
			for (Viewable child : target.getChildren()) {
				if (child instanceof Fluent) {
					filter((Fluent) child, filter, result);
				} else {
					filter(((ViewOn<?>) child).getViewForDebugPurposesOnly(), filter, result);
				}
			}
		}
	}

	public static List<Fluent> getElementsBy(Filter filter, Fluent target) {
		List<Fluent> result = new ArrayList<>();
		filter(target, filter, result);
		return result;
	}

	public static List<Fluent> getElementsById(String input, Fluent target) {
		List<Fluent> result = new ArrayList<>();
		filter(target, f -> {
			String found = f.id();
			if (found == null || !found.equals(input)) {
				return false;
			} else {
				return true;
			}
		}, result);
		return result;
	}

	public static List<Fluent> getElementsByTagName(String input, Fluent target) {
		List<Fluent> result = new ArrayList<>();
		filter(target, f -> {
			String found = f.tag();
			if (found == null || !found.equals(input)) {
				return false;
			} else {
				return true;
			}
		}, result);
		return result;
	}

	public static List<Fluent> getElementsByClassName(String input, Fluent target) {
		List<Fluent> result = new ArrayList<>();
		filter(target, f -> {
			String found = f.classs();
			if (found == null || (!found.equals(input) && !found.contains(" " + input))) {
				return false;
			} else {
				return true;
			}
		}, result);
		return result;
	}

}
