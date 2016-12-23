package live.connector.vertxui.reacty;

import live.connector.vertxui.fluentHtml.FluentHtml;

/**
 * Makes a ReactC from a ReactM method.
 *
 * @param <A>
 */
public class ReactMC<A> extends ReactC {
	private A state;
	private ReactM<A> m;

	public ReactMC(A state, ReactM<A> m) {
		this.state = state;
		this.m = m;
	}

	@Override
	public FluentHtml generate() {
		return m.generate(state);
	}

}
