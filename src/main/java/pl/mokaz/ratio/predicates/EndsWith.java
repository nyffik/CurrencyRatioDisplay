package pl.mokaz.ratio.predicates;

import com.google.common.base.Predicate;

public final class EndsWith implements Predicate<String> {
	private String sentence;
	
	public EndsWith(String string) {
		this.sentence = string;
	}

	@Override
	public boolean apply(String element) {
		return element.endsWith(sentence);
	}
}
