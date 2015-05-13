package pl.mokaz.ratio.serdes;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import pl.mokaz.ratio.predicates.ContainsCurrencyCode;
import pl.mokaz.ratio.predicates.EndsWith;
import pl.mokaz.ratio.predicates.StartsWith;
import pl.mokaz.ratio.schema.TabelaKursow;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

public class Deserializer {
	




	private final class PozycjaToKursKupna implements Function<Pozycja, BigDecimal> {
		@Override
		public BigDecimal apply(Pozycja input) {
			return input.getKursKupna();
		}
	}
	private final class PozycjaToKursSprzedazy implements Function<Pozycja, BigDecimal> {
		@Override
		public BigDecimal apply(Pozycja input) {
			return input.getKursSprzedazy();
		}
	}

	private final class LocalDateToString implements Function<LocalDate, String> {
		@Override
		public String apply(LocalDate input) {
			// TODO Auto-generated method stub
			return input.toString(DEFAULT_PATTERN);
		}
	}

	private static final String SEEK_PATTERN = "yyMMdd";
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd";
	private static final String NBP_URL = "http://www.nbp.pl/kursy/xml/";
	private static final String ALL_RATIO = "dir.txt";
	private static final String PREFIX = "c";

	public TabelaKursow download(String fileName) throws Exception {
		Unmarshaller unmarshaller = createUnmarshaler();
		URL url = new URL(NBP_URL + fileName + ".xml");
		TabelaKursow result = (TabelaKursow) unmarshaller.unmarshal(url);

		return result;
	}

	public Unmarshaller createUnmarshaler() throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("pl.mokaz.ratio.schema");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return unmarshaller;
	}

	private Optional<String> findFileName(String date) throws Exception {
		
		String seekDate = convertDate(date);
		
		Optional<String> tryFind = getFileName(seekDate);
		
		return tryFind;
	}

	private Optional<String> getFileName(String seekDate) throws IOException, MalformedURLException {
		List<String> allFiles = getAllFilesName();
		
		Optional<String> tryFind = Iterables.tryFind(allFiles, Predicates.and(new EndsWith(seekDate),new StartsWith(PREFIX)));
		return tryFind;
	}
	
	public Optional<TabelaKursow> get(String date) throws Exception {
		Optional<String> findFileName = findFileName(date);
		
		if(findFileName.isPresent()) {
		return	Optional.fromNullable(download(findFileName.get()));
		}
		
		return Optional.absent();
	}

	private List<String> getAllFilesName() throws IOException, MalformedURLException {
		List<String> readLines = Resources.readLines(new URL(NBP_URL + ALL_RATIO), Charsets.UTF_8);
		return readLines;
	}

	private String convertDate(String date) {
		LocalDate localDate = LocalDate.parse(date);
		
		String string = localDate.toString(SEEK_PATTERN);
		return string;
	}

	public Pozycja get(String date, String currency) throws Exception {
		TabelaKursow tabelaKursow = get(date).get();
		
		return getPozycja(currency, tabelaKursow);
	}

	private Pozycja getPozycja(String currency, TabelaKursow tabelaKursow) {
		Optional<Pozycja> tryFind = Iterables.tryFind(tabelaKursow.getPozycja(), new ContainsCurrencyCode(currency));
		return tryFind.get();
	}
	
	public List<TabelaKursow> getForRange(String beginDate, String endDate) throws Exception {
		
		List<String> dates = getDates(beginDate, endDate);
		
		List<TabelaKursow> result = Lists.newArrayList();
		for (String date : dates) {
			Optional<TabelaKursow> optional = get(date);
			if(optional.isPresent()) {
				result.add(optional.get());
			}
		}
		
		//	TabelaKursow tabelaKursow = get(date);
		
		//Optional<Pozycja> tryFind = Iterables.tryFind(tabelaKursow.getPozycja(), new ContainsCurrencyCode(currency));
		return result;
	}
	public List<Pozycja> getForRange(String beginDate, String endDate, String currency) throws Exception {
		
		List<String> dates = getDates(beginDate, endDate);
		
		List<Pozycja> result = Lists.newArrayList();
		for (String date : dates) {
			Optional<TabelaKursow> optional = get(date);
			if(optional.isPresent()) {
				result.add(getPozycja(currency, optional.get()));
			}
		}
		
	//	TabelaKursow tabelaKursow = get(date);
		
		//Optional<Pozycja> tryFind = Iterables.tryFind(tabelaKursow.getPozycja(), new ContainsCurrencyCode(currency));
		return result;
	}
	
	public BigDecimal calculateAverage(String beginDate, String endDate, String currency) throws Exception {
		List<Pozycja> forRange = getForRange(beginDate, endDate, currency);
		
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
		List<BigDecimal> list = FluentIterable.from(forRange).transform(new PozycjaToKursKupna()).toList();
		
		for (BigDecimal bigDecimal : list) {
			descriptiveStatistics.addValue(bigDecimal.doubleValue());
		}
		
		
		BigDecimal avg = new BigDecimal(descriptiveStatistics.getMean()).setScale(4, BigDecimal.ROUND_HALF_UP);
		
				//getAverage(list);
		return avg;
	}

	private BigDecimal getAverage(List<BigDecimal> forRange) {
		BigDecimal sum = new BigDecimal(0);
		
		
		for (BigDecimal element : forRange) {
			sum = sum.add(element);
		}
		
		BigDecimal avg = sum.divide(new BigDecimal(forRange.size()));
		avg = avg.setScale(4, BigDecimal.ROUND_HALF_UP);
		return avg;
	}
	
	public BigDecimal calculateStandardDevation(String beginDate, String endDate, String currency) throws Exception {
		List<Pozycja> forRange = getForRange(beginDate, endDate, currency);
		
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
		List<BigDecimal> list = FluentIterable.from(forRange).transform(new PozycjaToKursSprzedazy()).toList();
		
		for (BigDecimal bigDecimal : list) {
			descriptiveStatistics.addValue(bigDecimal.doubleValue());
		}
		
		
		BigDecimal avg = new BigDecimal(descriptiveStatistics.getPopulationVariance()).setScale(4, BigDecimal.ROUND_HALF_UP);
		
		
/*		BigDecimal n = new BigDecimal(0);
		BigDecimal mean = new BigDecimal(0);
		BigDecimal m2 = new BigDecimal(0);
		for (BigDecimal bigDecimal : list) {
			n = n.add(BigDecimal.ONE);
			BigDecimal delta = bigDecimal.subtract(mean);
			mean = mean.add(delta.divide(n));
			m2 =m2.add(delta.multiply(bigDecimal.subtract(mean)));
		}*/
		
				//getAverage(list);
		//BigDecimal avg = m2.divide(n.subtract(BigDecimal.ONE));
		return avg;
	}

	private List<String> getDates(String beginDate, String endDate) {
		LocalDate begin = LocalDate.parse(beginDate);
		LocalDate end = LocalDate.parse(endDate);
		
		int days = Days.daysBetween(begin, end).getDays();
		
		List<LocalDate> dates = Lists.newArrayList();
		
		for (int i=0; i < days+1; i++) {
		    LocalDate date = begin.plusDays(i);
		    dates.add(date);
		}
		return transformDatesToString(dates);
	}

	private List<String> transformDatesToString(List<LocalDate> dates) {
		return FluentIterable.from(dates).transform(new LocalDateToString()).toList();
	}

}
