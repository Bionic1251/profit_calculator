package tween.oaks.profitcalculator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ochtarfear
 * @since 2/16/13
 */
public enum HardCodedQuotes {

    INSTANCE;

    private static class Quote {
        final double bid;
        final double ask;

        private Quote(double bid, double ask) {
            this.bid = bid;
            this.ask = ask;
        }
    }

    private final Map<String, Quote> quotes;

    {
        HashMap<String, Quote> quoteHashMap = new HashMap<>();

        quoteHashMap.put("AUDCAD", new Quote(1.03559, 1.03856));
        quoteHashMap.put("AUDCHF", new Quote(0.94855, 0.95131));
        quoteHashMap.put("AUDJPY", new Quote(96.830, 96.837));
        quoteHashMap.put("AUDNZD", new Quote(1.22081, 1.22110));
        quoteHashMap.put("AUDUSD", new Quote(1.03552, 1.03560));

        quoteHashMap.put("CADJPY", new Quote(92.297, 92.309));

        quoteHashMap.put("EURCHF", new Quote(1.23470, 1.23478));
        quoteHashMap.put("EURGBP", new Quote(0.86826, 0.86832));
        quoteHashMap.put("EURJPY", new Quote(124.850, 125.000));
        quoteHashMap.put("EURSEK", new Quote(8.43560, 8.43830));
        quoteHashMap.put("EURUSD", new Quote(1.33593, 1.33626));

        quoteHashMap.put("GBPUSD", new Quote(1.55160, 1.55160));
        quoteHashMap.put("SN1USD", new Quote(2.00084, 2.00094));

        quoteHashMap.put("USDCAD", new Quote(1.01247, 1.01257));
        quoteHashMap.put("USDDKK", new Quote(5.55915, 5.55981));
        quoteHashMap.put("USDJPY", new Quote(93.500, 93.53));

        quoteHashMap.put("XAGUSD", new Quote(29.455, 29.497));
        quoteHashMap.put("XAUUSD", new Quote(1607.070, 1607.320));

        quotes = Collections.unmodifiableMap(quoteHashMap);
    }

    public double getBid(String symbol){
        Quote quote = quotes.get(symbol);
        return quote != null? quote.bid: 0;
    }

    public double getAsk(String symbol){
        Quote quote = quotes.get(symbol);
        return quote != null? quote.ask: 0;
    }
}
