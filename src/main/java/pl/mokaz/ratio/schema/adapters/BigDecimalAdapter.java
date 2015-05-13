package pl.mokaz.ratio.schema.adapters;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

	@Override
	public String marshal(BigDecimal value) throws Exception {
		if (value != null) {
			return value.toString().replace('.', ',');
		}
		return null;
	}

	@Override
	public BigDecimal unmarshal(String s) throws Exception {
		String replaced = s.replace(',', '.');
		return new BigDecimal(replaced);
	}
}
