package pl.mokaz.ratio.math.inf;

import java.util.List;

public interface Statistic<T> {

	T getStandardDeviation(List<T> list);

	T getMean(List<T> forRange);

}