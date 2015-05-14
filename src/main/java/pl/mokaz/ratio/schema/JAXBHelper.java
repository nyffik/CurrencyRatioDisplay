package pl.mokaz.ratio.schema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class JAXBHelper {

	public static Unmarshaller createUnmarshaler() throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("pl.mokaz.ratio.schema");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return unmarshaller;
	}

}
