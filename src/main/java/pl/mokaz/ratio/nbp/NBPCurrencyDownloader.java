package pl.mokaz.ratio.nbp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.Unmarshaller;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import pl.mokaz.ratio.predicates.ContainsCurrencyCode;
import pl.mokaz.ratio.predicates.EndsWith;
import pl.mokaz.ratio.predicates.StartsWith;
import pl.mokaz.ratio.schema.JAXBHelper;
import pl.mokaz.ratio.schema.TabelaKursow;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;
import pl.mokaz.ratio.util.DateUtil;

public class NBPCurrencyDownloader {

	private static final String NBP_URL = "http://www.nbp.pl/kursy/xml/";
	private static final String ALL_RATIO = "dir.txt";
	private static final String PREFIX = "c";

	private DateUtil dateUtil = new DateUtil();

	public TabelaKursow download(String fileName) throws Exception {
		Unmarshaller unmarshaller = JAXBHelper.createUnmarshaler();
		URL url = new URL(NBP_URL + fileName + ".xml");
		TabelaKursow result = (TabelaKursow) unmarshaller.unmarshal(url);

		return result;
	}

	private List<String> getAllFilesName() throws IOException, MalformedURLException {
		List<String> readLines = Resources.readLines(new URL(NBP_URL + ALL_RATIO), Charsets.UTF_8);
		return readLines;
	}

	private Optional<String> findFileName(String date) throws Exception {

		String seekDate = dateUtil.convertDate(date);

		Optional<String> tryFind = getFileName(seekDate);

		return tryFind;
	}

	private Optional<String> getFileName(String seekDate) throws IOException, MalformedURLException {
		List<String> allFiles = getAllFilesName();

		Optional<String> tryFind = Iterables.tryFind(allFiles,
				Predicates.and(new EndsWith(seekDate), new StartsWith(PREFIX)));
		return tryFind;
	}

	public Optional<TabelaKursow> get(String date) throws Exception {
		Optional<String> findFileName = findFileName(date);

		if (findFileName.isPresent()) {
			return Optional.fromNullable(download(findFileName.get()));
		}

		return Optional.absent();
	}

	public Pozycja get(String date, String currency) throws Exception {
		TabelaKursow tabelaKursow = get(date).get();

		return getPozycja(currency, tabelaKursow);
	}

	public Pozycja getPozycja(String currency, TabelaKursow tabelaKursow) {
		Optional<Pozycja> tryFind = Iterables.tryFind(tabelaKursow.getPozycja(), new ContainsCurrencyCode(currency));
		return tryFind.get();
	}

	public List<Pozycja> getForCurrency(String currency, List<String> dates) throws Exception {
		List<Pozycja> result = Lists.newArrayList();
		for (String date : dates) {
			Optional<TabelaKursow> optional = get(date);
			if (optional.isPresent()) {
				result.add(getPozycja(currency, optional.get()));
			}
		}
		return result;
	}

}
