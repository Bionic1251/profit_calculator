package tween.oaks.profitcalculator.mocks;

import com.tradable.api.services.marketdata.*;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

/**
 * User: tween
 * Date: 8/26/12
 * Time: 2:01 PM
 */
public class QuoteGenerator implements QuoteTickSubscription {

    private static final double EUR_USD = 1.2940;
    private static final double GBP_USD	= 1.6105;
    private static final double USD_JPY	= 79.64;
    private static final double USD_CHF	= 0.9348;
    private static final double AUD_USD	= 1.0374;
    private static final double EUR_GBP	= 0.8036;
    private static final double USD_CAD	= 0.9971;
    private static final double NZD_USD	= 0.8228;
    private static final double EUR_JPY	= 103.05;
    private static final double GBP_JPY	= 128.26;
    private static final double DEFAULT_SPREAD = 20;
    private static final double DEFAULT_PIP = 0.00001;
    private static final double JPY_PIP = 0.01;
    private static final long DEFAULT_SIZE = 1000;

    private static long getRandomMillis() {
        return (long)(Math.random() * 5800) + 500;
    }

    private static void updateQuote(Tick tick) {

        long timestamp = System.currentTimeMillis();

        double midQuote = (tick.ask.getPrice() + tick.bid.getPrice()) / 2 + (Math.random() * 100 - 50) * tick.pip;

        GeneratedQuote ask = tick.ask;
        double price = midQuote + (DEFAULT_SPREAD + (Math.random() * 10 - 5)) * tick.pip;
        QuoteTickDirection direction = price > ask.getPrice()? QuoteTickDirection.UP: price < ask.getPrice()? QuoteTickDirection.DOWN: QuoteTickDirection.UNKNOWN;
        tick.ask = new GeneratedQuote(tick.symbol, timestamp, (long)(Math.random() * DEFAULT_SIZE * 2), price, direction);


        GeneratedQuote bid = tick.bid;
        price = midQuote - (DEFAULT_SPREAD + (Math.random() * 10 - 5)) * tick.pip;
        direction = price > bid.getPrice()? QuoteTickDirection.UP: price < bid.getPrice()? QuoteTickDirection.DOWN: QuoteTickDirection.UNKNOWN;
        tick.bid = new GeneratedQuote(tick.symbol, timestamp, (long)(Math.random() * DEFAULT_SIZE * 2), price, direction);
    }

    private static class Tick {
        
        final String symbol;
        final double pip;
        volatile GeneratedQuote bid;
        volatile GeneratedQuote ask;

        private Tick(String symbol, double bid, double ask, long size, double pip) {
            this.symbol = symbol;
            this.pip = pip;
            long timestamp = System.currentTimeMillis();
            this.bid = new GeneratedQuote(symbol, timestamp, size, bid, QuoteTickDirection.UNKNOWN);
            this.ask = new GeneratedQuote(symbol, timestamp, size, ask, QuoteTickDirection.UNKNOWN);
        }
    }

    private static class GeneratedQuote implements Quote {

        final String symbol;
        final long timestamp;
        final long size;
        final double price;
        final QuoteTickDirection direction;

        private GeneratedQuote(String symbol, long timestamp, long size, double price, QuoteTickDirection direction) {
            this.symbol = symbol;
            this.timestamp = timestamp;
            this.size = size;
            this.price = price;
            this.direction = direction;
        }

        @Override
        public long getTime() {
            return timestamp;
        }

        @Override
        public long getSize() {
            return size;
        }

        @Override
        public double getPrice() {
            return price;
        }

        @Override
        public QuoteTickDirection getTickDirection() {
            return direction;
        }

        @Override
        public String getSymbol() {
            return symbol;
        }
    }

    private final ScheduledExecutorService generatorThread = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentHashMap<String, Tick> quotes = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<QuoteTickListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public Quote getAsk(String s) {
        return quotes.get(s).ask;
    }

    @Override
    public Quote getBid(String s) {
        return quotes.get(s).bid;
    }

    @Override
    public void setListener(QuoteTickListener quoteTickListener) {
        listeners.add(quoteTickListener);
    }

    @Override
    public void addSymbol(final String s) {
        final Tick tick = getDefaultTick(s);
        updateQuote(tick);
        if (quotes.putIfAbsent(s, tick) == null){
            generatorThread.schedule(new Runnable() {
                @Override
                public void run() {

                    updateQuote(tick);

                    QuoteTickEvent event = new QuoteTickEvent() {
                        @Override
                        public Set<String> getSymbols() {
                            return Collections.singleton(tick.symbol);
                        }
                    };

                    for (QuoteTickListener listener : listeners) {
                        listener.quotesUpdated(event);
                    }

                    generatorThread.schedule(this, getRandomMillis(), TimeUnit.MILLISECONDS);
                }
            }, getRandomMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private Tick getDefaultTick(String s) {
        switch(s){
            case "NZDRUB":
                return new Tick(s, EUR_USD - DEFAULT_SPREAD * DEFAULT_PIP, EUR_USD + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "EURUSD":
                return new Tick(s, EUR_USD - DEFAULT_SPREAD * DEFAULT_PIP, EUR_USD + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "GBPUSD":
                return new Tick(s, GBP_USD - DEFAULT_SPREAD * DEFAULT_PIP, GBP_USD + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "USDJPY":
                return new Tick(s, USD_JPY - DEFAULT_SPREAD * JPY_PIP, USD_JPY + JPY_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, JPY_PIP);
            case "AUDUSD":
                return new Tick(s, AUD_USD - DEFAULT_SPREAD * DEFAULT_PIP, AUD_USD + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "EURGBP":
                return new Tick(s, EUR_GBP - DEFAULT_SPREAD * DEFAULT_PIP, EUR_GBP + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "USDCAD":
                return new Tick(s, USD_CAD - DEFAULT_SPREAD * DEFAULT_PIP, USD_CAD + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "NZDUSD":
                return new Tick(s, NZD_USD - DEFAULT_SPREAD * DEFAULT_PIP, NZD_USD + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "USDCHF":
                return new Tick(s, USD_CHF - DEFAULT_SPREAD * DEFAULT_PIP, USD_CHF + DEFAULT_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, DEFAULT_PIP);
            case "EURJPY":
                return new Tick(s, EUR_JPY - DEFAULT_SPREAD * JPY_PIP, EUR_JPY + JPY_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, JPY_PIP);
            case "GBPJPY":
                return new Tick(s, GBP_JPY - DEFAULT_SPREAD * JPY_PIP, GBP_JPY + JPY_PIP * DEFAULT_SPREAD, DEFAULT_SIZE, JPY_PIP);
            default:
                throw new IllegalArgumentException("Unsupported symbol: " + s);
        }
    }

    @Override
    public void addSymbols(Set<String> strings) {
        for (String string : strings) {
            addSymbol(string);
        }
    }

    @Override
    public void setSymbol(String s) {
        quotes.clear();
        addSymbol(s);
    }

    @Override
    public void setSymbols(Set<String> strings) {
        quotes.clear();
        addSymbols(strings);
    }

    @Override
    public void removeSymbol(String s) {
        quotes.remove(s);
    }

    @Override
    public void removeSymbols(Set<String> strings) {
        for (String s: strings){
            quotes.remove(s);
        }
    }

    @Override
    public void destroy() {
        generatorThread.shutdown();
    }

}
