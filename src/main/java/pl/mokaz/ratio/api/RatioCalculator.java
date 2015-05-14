package pl.mokaz.ratio.api;

import java.util.List;

import com.google.common.base.Function;

public interface RatioCalculator<S,T> {

	S calculateAverageFor(String beginDate, String endDate, String currency,
			Function<T, S> functionFor) throws Exception;

	S calculateStandardDevationFor(String beginDate, String endDate, String currency,
			Function<T, S> functionFor) throws Exception;

	List<T> getForRangeByCurrency(String beginDate, String endDate, String currency) throws Exception;

}