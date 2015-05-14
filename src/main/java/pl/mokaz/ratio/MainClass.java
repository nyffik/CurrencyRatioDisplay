package pl.mokaz.ratio;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import pl.mokaz.ratio.api.RatioCalculator;
import pl.mokaz.ratio.function.PozycjaToKursKupna;
import pl.mokaz.ratio.function.PozycjaToKursSprzedazy;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;

import com.google.common.base.Preconditions;

public class MainClass {

	private static final List<String> ALLOWED_CURRENCIES = Lists.newArrayList("USD", "EUR", "CHF", "GBP");
	public static void main(String[] args) throws Exception {
		
		String currency = args[0];
		String beginDate = args[1];
		String endDate = args[2];
		
		checkArguments(currency, beginDate, endDate);
		
		RatioCalculator<BigDecimal,Pozycja> calculator = new NBPCalculator();
		BigDecimal averageFor = calculator.calculateAverageFor(beginDate, endDate, currency, new PozycjaToKursKupna() );
		BigDecimal deviation = calculator.calculateStandardDevationFor(beginDate, endDate, currency, new PozycjaToKursSprzedazy() );
		
		System.out.println(String.format("Seek %s currency from %s to %s", currency, beginDate, endDate));
		System.out.println(String.format("average: %s ", averageFor.toString()));
		System.out.println(String.format("standard deviation: %s ", deviation.toString()));

	}
	private static void checkArguments(String currency, String startDate, String endDate) {
		Preconditions.checkArgument(ALLOWED_CURRENCIES.contains(currency));
		Preconditions.checkArgument(LocalDate.parse(startDate) instanceof LocalDate);
		Preconditions.checkArgument(LocalDate.parse(endDate) instanceof LocalDate);
	}

}
