package pl.mokaz.ratio.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import pl.mokaz.ratio.math.inf.Statistic;

public class BigDecimalStatistic implements Statistic<BigDecimal> {

	/* (non-Javadoc)
	 * @see pl.mokaz.ratio.math.Istatistic#getStandardDeviation(java.util.List)
	 */
	@Override
	public BigDecimal getStandardDeviation(List<BigDecimal> list) {
		BigDecimal variance = BigDecimal.ZERO;
		int count = list.size();
		BigDecimal avarage = getMean(list);
		BigDecimal temp = BigDecimal.ZERO;
		for (BigDecimal l : list) {
			temp = temp.add(l.subtract(avarage).pow(2));
		}
		variance = temp.divide(BigDecimal.valueOf(count)); 

		BigDecimal avg = new BigDecimal(Math.sqrt(variance.doubleValue())).setScale(4, RoundingMode.HALF_UP);
		return avg;
	}
	
	/* (non-Javadoc)
	 * @see pl.mokaz.ratio.math.Istatistic#getMean(java.util.List)
	 */
	@Override
	public BigDecimal getMean(List<BigDecimal> forRange) {
		BigDecimal sum = new BigDecimal(0);

		for (BigDecimal element : forRange) {
			sum = sum.add(element);
		}

		BigDecimal avg = sum.divide(new BigDecimal(forRange.size()));
		avg = avg.setScale(4, BigDecimal.ROUND_HALF_UP);
		return avg;
	}
	
}
