package pl.mokaz.ratio;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import pl.mokaz.ratio.NBPCalculator;
import pl.mokaz.ratio.api.RatioCalculator;
import pl.mokaz.ratio.function.PozycjaToKursKupna;
import pl.mokaz.ratio.function.PozycjaToKursSprzedazy;
import pl.mokaz.ratio.nbp.NBPCurrencyDownloader;
import pl.mokaz.ratio.schema.TabelaKursow;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;

import com.google.common.base.Optional;

public class CalculatorRatioTest {

	private RatioCalculator<BigDecimal, Pozycja> calculator;
	private NBPCurrencyDownloader downloader;

	@Before
	public void setUp() {
		calculator = new NBPCalculator();
		downloader = new NBPCurrencyDownloader();
	}

	@Test
	public void shouldDownloadDataFromFile() throws Exception {

		TabelaKursow tabelaKursow = downloader.download("c073z070413");

		assertThat(tabelaKursow.getDataPublikacji().getDay()).isEqualTo(13);
		assertThat(tabelaKursow.getDataPublikacji().getMonth()).isEqualTo(4);
		assertThat(tabelaKursow.getDataPublikacji().getYear()).isEqualTo(2007);
		assertThat(tabelaKursow.getPozycja().get(0).getKodWaluty()).isEqualTo("USD");
	}

	@Test
	public void shouldDownloadDataFromFoundFileByDate() throws Exception {

		String date = "2007-04-13";

		TabelaKursow tabelaKursow = downloader.get(date).get();

		assertThat(tabelaKursow.getDataPublikacji().getDay()).isEqualTo(13);
		assertThat(tabelaKursow.getDataPublikacji().getMonth()).isEqualTo(4);
		assertThat(tabelaKursow.getDataPublikacji().getYear()).isEqualTo(2007);
		assertThat(tabelaKursow.getPozycja().get(0).getKodWaluty()).isEqualTo("USD");
	}

	@Test
	public void shouldDownloadDataFromFoundFileByDateAndCurrency() throws Exception {

		String date = "2007-04-13";
		String currency = "EUR";

		Pozycja pozycja = downloader.get(date, currency);

		assertThat(pozycja.getKodWaluty()).isEqualTo("EUR");
		assertThat(pozycja.getKursKupna()).isEqualTo(new BigDecimal("3.7976"));
		assertThat(pozycja.getKursSprzedazy()).isEqualTo(new BigDecimal("3.8744"));
		assertThat(pozycja.getNazwaWaluty()).isEqualTo("euro");
		assertThat(pozycja.getPrzelicznik()).isEqualTo(1);
	}

	@Test
	public void shouldCalculateAverage() throws Exception {
		
		String beginDate = "2013-01-28";
		String endDate = "2013-01-31";
		String currency = "EUR";
		
		BigDecimal average = calculator.calculateAverageFor(beginDate, endDate, currency, new PozycjaToKursKupna());
		
		assertThat(average).isEqualTo("4.1505");
	}
	@Test
	public void shouldCalculateAverageCurrent() throws Exception {

		String beginDate = "2015-05-10";
		String endDate = "2015-05-14";
		String currency = "EUR";

		BigDecimal average = calculator.calculateAverageFor(beginDate, endDate, currency, new PozycjaToKursKupna());

		assertThat(average).isEqualTo("4.0352");
	}

	@Test
	public void shouldCalculateStandardDevation() throws Exception {

		String beginDate = "2013-01-28";
		String endDate = "2013-01-31";
		String currency = "EUR";

		BigDecimal average = calculator.calculateStandardDevationFor(beginDate, endDate, currency,
				new PozycjaToKursSprzedazy());

		assertThat(average).isEqualTo("0.0125");
	}

	@Test
	public void shouldNotDownloadWhenFileNotExist() throws Exception {
		String date = "1990-04-13";
		Optional<TabelaKursow> optional = downloader.get(date);
		assertThat(optional.isPresent()).isFalse();
	}

}
