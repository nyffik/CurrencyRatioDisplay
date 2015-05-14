package pl.mokaz.ratio.function;

import org.joda.time.LocalDate;

import com.google.common.base.Function;

public final class LocalDateToString implements Function<LocalDate, String> {
	
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

	@Override
	public String apply(LocalDate input) {
		return input.toString(DEFAULT_PATTERN);
	}
}
