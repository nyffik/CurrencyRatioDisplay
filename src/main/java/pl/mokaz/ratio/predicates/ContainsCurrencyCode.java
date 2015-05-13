package pl.mokaz.ratio.predicates;

import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;

import com.google.common.base.Predicate;

public final class ContainsCurrencyCode implements Predicate<Pozycja> {
	
	private final String currency;
	
	public ContainsCurrencyCode(String currency) {
		this.currency = currency;
	}

	@Override
	public boolean apply(Pozycja input) {
		return input.getKodWaluty().equals(currency);
	}
}
