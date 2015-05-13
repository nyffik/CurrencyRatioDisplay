package pl.mokaz.ratio.serdes;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

import pl.mokaz.ratio.exceptions.RatioNotAvailableForDate;
import pl.mokaz.ratio.schema.TabelaKursow;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;

public class DeserializationTest {

	private Deserializer deserializer;

	@Before
	public void setUp() {
		deserializer = new Deserializer();
	}

	@Test
	public void shouldDownloadDataFromFile() throws Exception {

		TabelaKursow tabelaKursow = deserializer.download("c073z070413");

		assertThat(tabelaKursow.getDataPublikacji().getDay()).isEqualTo(13);
		assertThat(tabelaKursow.getDataPublikacji().getMonth()).isEqualTo(4);
		assertThat(tabelaKursow.getDataPublikacji().getYear()).isEqualTo(2007);
		assertThat(tabelaKursow.getPozycja().get(0).getKodWaluty()).isEqualTo("USD");
	}

	@Test
	public void shouldDownloadDataFromFoundFileByDate() throws Exception {
		
		String date = "2007-04-13";
		
		TabelaKursow tabelaKursow = deserializer.get(date).get();
		
		assertThat(tabelaKursow.getDataPublikacji().getDay()).isEqualTo(13);
		assertThat(tabelaKursow.getDataPublikacji().getMonth()).isEqualTo(4);
		assertThat(tabelaKursow.getDataPublikacji().getYear()).isEqualTo(2007);
		assertThat(tabelaKursow.getPozycja().get(0).getKodWaluty()).isEqualTo("USD");
	}
	
	@Test
	public void shouldDownloadDataFromFoundFileByDateAndCurrency() throws Exception {
		
		String date = "2007-04-13";
		String currency = "EUR";
		
		Pozycja pozycja = deserializer.get(date,currency);
		
		assertThat(pozycja.getKodWaluty()).isEqualTo("EUR");
		assertThat(pozycja.getKursKupna()).isEqualTo(new BigDecimal("3.7976"));
		assertThat(pozycja.getKursSprzedazy()).isEqualTo(new BigDecimal("3.8744"));
		assertThat(pozycja.getNazwaWaluty()).isEqualTo("euro");
		assertThat(pozycja.getPrzelicznik()).isEqualTo(1);
	}
	
	@Test
	public void shouldDownloadDataFromFoundFileByDateRange() throws Exception {
		
		String beginDate = "2013-01-28";
		String endDate = "2013-01-31";
		
		List<TabelaKursow> results = deserializer.getForRange(beginDate, endDate);
		assertThat(results).hasSize(4);
		
	}
	
	@Test
	public void shouldCalculateAverage() throws Exception {
		
		String beginDate = "2013-01-28";
		String endDate = "2013-01-31";
		String currency = "EUR";
		
		List<Pozycja> results = deserializer.getForRange(beginDate, endDate, currency);
		assertThat(results).hasSize(4);
		BigDecimal average = deserializer.calculateAverage(beginDate, endDate, currency);
		
		assertThat(average).isEqualTo("4.1505");
	}
	
	@Test
	public void shouldCalculateStandardDevation() throws Exception {
		
		String beginDate = "2013-01-28";
		String endDate = "2013-01-31";
		String currency = "EUR";
		
		List<Pozycja> results = deserializer.getForRange(beginDate, endDate, currency);
		assertThat(results).hasSize(4);
		BigDecimal average = deserializer.calculateStandardDevation(beginDate, endDate, currency);

		assertThat(average).isEqualTo("0.0125");//TODO fix it
	}
	
	@Test
	public void shouldNotDownloadWhenFileNotExist() throws Exception {
		String date = "1990-04-13";		
		Optional<TabelaKursow> optional = deserializer.get(date);
		assertThat(optional.isPresent()).isFalse();
	}

}
