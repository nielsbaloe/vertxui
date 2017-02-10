package live.connector.vertxui.client.test;

import elemental.html.Console;
import elemental.html.MemoryInfo;
import elemental.util.Indexable;

/**
 * A class which simulates console.log for plain-java outside-gwt junit testing.
 * 
 * @author Niels Gorisse
 *
 */
public class ConsoleTester implements Console {

	public ConsoleTester() {
	}

	@Override
	public MemoryInfo getMemory() {
		return null;
	}

	@Override
	public Indexable getProfiles() {
		return null;
	}

	@Override
	public void assertCondition(boolean condition, Object arg) {
		if (condition == false) {
			System.err.println(arg);
		}
	}

	@Override
	public void count() {
	}

	@Override
	public void debug(Object arg) {
		System.out.println(arg);
	}

	@Override
	public void dir() {
	}

	@Override
	public void dirxml() {
	}

	@Override
	public void error(Object arg) {
		System.err.println(arg);
	}

	@Override
	public void group(Object arg) {
	}

	@Override
	public void groupCollapsed(Object arg) {
	}

	@Override
	public void groupEnd() {
	}

	@Override
	public void info(Object arg) {
		System.out.println(arg);
	}

	@Override
	public void log(Object arg) {
		System.out.println(arg);
	}

	@Override
	public void markTimeline() {
	}

	@Override
	public void profile(String title) {
	}

	@Override
	public void profileEnd(String title) {
	}

	@Override
	public void time(String title) {
	}

	@Override
	public void timeEnd(String title, Object arg) {
	}

	@Override
	public void timeStamp(Object arg) {
	}

	@Override
	public void trace(Object arg) {
	}

	@Override
	public void warn(Object arg) {
		System.err.println(arg);
	}

}
