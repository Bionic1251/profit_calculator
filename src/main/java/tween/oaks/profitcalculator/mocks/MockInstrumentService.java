package tween.oaks.profitcalculator.mocks;

import com.tradable.api.entities.Instrument;
import com.tradable.api.entities.InstrumentType;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.instrument.InstrumentServiceListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @user: tween
 * @date: 11/6/12
 */
public class MockInstrumentService implements InstrumentService {

    private final Map<Integer, Instrument> instruments;

    {
        HashMap<Integer, Instrument> i = new HashMap<>();
        i.put(0, new DummyInstrument(0, "EURUSD", 5));
        i.put(1, new DummyInstrument(1, "NZDRUB", 5));
        i.put(2, new DummyInstrument(2, "AUDUSD", 5));
        i.put(3, new DummyInstrument(3, "EURJPY", 3));
        i.put(4, new DummyInstrument(4, "GBPUSD", 5));
        i.put(5, new DummyInstrument(5, "EURGBP", 5));
        this.instruments = Collections.unmodifiableMap(i);
    }

    @Override
    public void addListener(InstrumentServiceListener instrumentServiceListener) {

    }

    @Override
    public void removeListener(InstrumentServiceListener instrumentServiceListener) {

    }

    @Override
    public Collection<Instrument> getInstruments() {
        return instruments.values();
    }

    @Override
    public Instrument getInstrument(int i) {
        return instruments.get(i);
    }

    @Override
    public Instrument getInstrument(String s) {
        for (Instrument i: instruments.values()){
            if (i.getSymbol().equals(s)){
                return i;
            }
        }
        return null;
    }

    private class DummyInstrument implements Instrument {


        private final int id;
        private final String symbol;
        private final int precision;
        private final double priceIncrement;

        private DummyInstrument(int id, String symbol, int precision) {
            this.id = id;
            this.symbol = symbol;
            this.precision = precision;
            priceIncrement = Math.pow(10, -precision);
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public String getDescription() {
            return "no description";
        }

        @Override
        public InstrumentType getType() {
            return InstrumentType.FOREX;
        }

        @Override
        public double getPrecision() {
            return precision;
        }

        @Override
        public Double getPriceIncrement() {
            return priceIncrement;
        }
    }
}
