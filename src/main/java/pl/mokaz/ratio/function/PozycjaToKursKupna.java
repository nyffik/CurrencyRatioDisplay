package pl.mokaz.ratio.function;

import java.math.BigDecimal;
import pl.mokaz.ratio.schema.TabelaKursow.Pozycja;
import com.google.common.base.Function;

public final class PozycjaToKursKupna implements Function<Pozycja, BigDecimal> {
	@Override
	public BigDecimal apply(Pozycja input) {
		return input.getKursKupna();
	}
}
