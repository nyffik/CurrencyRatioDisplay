package pl.mokaz.ratio;

import java.math.BigDecimal;
import java.util.List;

import pl.mokaz.ratio.api.RatioCalculator;
import pl.mokaz.ratio.math.BigDecimalStatistic;
import pl.mokaz.ratio.math.inf.Statistic;
import pl.mokaz.ratio.nbp.NBPCurrencyDownloader;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;
import pl.mokaz.ratio.util.DateUtil;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class NBPCalculator implements RatioCalculator<BigDecimal, Pozycja> {

	private Statistic<BigDecimal> statistic = new BigDecimalStatistic();
	private NBPCurrencyDownloader downloader = new NBPCurrencyDownloader();
	private DateUtil dateUtil = new DateUtil();

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.mokaz.ratio.ICalculator#calculateAverageFor(java.lang.String,
	 * java.lang.String, java.lang.String, com.google.common.base.Function)
	 */
	@Override
	public BigDecimal calculateAverageFor(String beginDate, String endDate, String currency,
			Function<Pozycja, BigDecimal> functionFor) throws Exception {
		List<Pozycja> forRange = getForRangeByCurrency(beginDate, endDate, currency);

		List<BigDecimal> list = FluentIterable.from(forRange).transform(functionFor).toList();

		return statistic.getMean(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.mokaz.ratio.ICalculator#calculateStandardDevationFor(java.lang.String,
	 * java.lang.String, java.lang.String, com.google.common.base.Function)
	 */
	@Override
	public BigDecimal calculateStandardDevationFor(String beginDate, String endDate, String currency,
			Function<Pozycja, BigDecimal> functionFor) throws Exception {
		List<Pozycja> forRange = getForRangeByCurrency(beginDate, endDate, currency);

		List<BigDecimal> list = FluentIterable.from(forRange).transform(functionFor).toList();

		return statistic.getStandardDeviation(list);

	}

	@Override
	public List<Pozycja> getForRangeByCurrency(String beginDate, String endDate, String currency) throws Exception {

		List<String> dates = dateUtil.getDates(beginDate, endDate);

		List<Pozycja> result = downloader.getForCurrency(currency, dates);

		return result;
	}

}
