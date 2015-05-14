package pl.mokaz.ratio.util;

import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import pl.mokaz.ratio.function.LocalDateToString;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class DateUtil {

	private static final String SEEK_PATTERN = "yyMMdd";

	public String convertDate(String date) {
		LocalDate localDate = LocalDate.parse(date);

		String string = localDate.toString(SEEK_PATTERN);
		return string;
	}

	public List<String> getDates(String beginDate, String endDate) {
		LocalDate begin = LocalDate.parse(beginDate);
		LocalDate end = LocalDate.parse(endDate);

		int days = Days.daysBetween(begin, end).getDays();

		List<LocalDate> dates = Lists.newArrayList();

		for (int i = 0; i < days + 1; i++) {
			LocalDate date = begin.plusDays(i);
			dates.add(date);
		}
		return transformDatesToString(dates);
	}

	private List<String> transformDatesToString(List<LocalDate> dates) {
		return FluentIterable.from(dates).transform(new LocalDateToString()).toList();
	}

}
