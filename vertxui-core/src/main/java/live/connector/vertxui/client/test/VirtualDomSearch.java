package live.connector.vertxui.client.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.fluent.Viewable;

import static live.connector.vertxui.client.fluent.Fluent.*;

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
		if (GWT.isClient()) {
			console.warn(
					"Warning: avoid using VirtualDomSearch in a browser (including TestDom). If you want to use this, run the tests without TestDOM, which is 1000 times faster!");
		}
		filter(target, filter, result);
		return result;
	}

	public static Fluent getElementById(String input, Fluent target) {
		List<Fluent> result = getElementsBy(f -> {
			String found = f.id();
			if (found == null || !found.equals(input)) {
				return false;
			} else {
				return true;
			}
		}, target);
		if (result.isEmpty()) {
			return null;
		} else if (result.size() > 1) {
			throw new Error("Found " + result.size() + " nodes with id=" + input + " for " + target);
		} else {
			return result.get(0);
		}
	}

	public static List<Fluent> getElementsByTagName(String input, Fluent target) {
		return getElementsBy(f -> {
			String found = f.tag();
			if (found == null || !found.equals(input)) {
				return false;
			} else {
				return true;
			}
		}, target);
	}

	public static List<Fluent> getElementsByClassName(String input, Fluent target) {
		return getElementsBy(f -> {
			String found = f.classs();
			if (found == null || (!found.equals(input) && !found.contains(" " + input))) {
				return false;
			} else {
				return true;
			}
		}, target);
	}

}
