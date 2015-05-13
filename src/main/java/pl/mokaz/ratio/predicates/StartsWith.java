package pl.mokaz.ratio.predicates;

import com.google.common.base.Predicate;

public class StartsWith implements Predicate<String> {
	private String sentence;
	
	public StartsWith(String string) {
		this.sentence = string;
	}

	@Override
	public boolean apply(String element) {
		return element.startsWith(sentence);
	}

}
