package tween.oaks.profitcalculator;

import com.tradable.api.services.historicmarketdata.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author ochtarfear
 * @since 2/10/13
 */
public class QuotesSummary {

    private final HistoricMarketDataService historicMarketDataService;

    private final ConcurrentHashMap<String, Future<SymbolSummary>> symbolsSummaries = new ConcurrentHashMap<>();

    public QuotesSummary(HistoricMarketDataService historicMarketDataService) {
        this.historicMarketDataService = historicMarketDataService;
    }

    public SymbolSummary getSummaries(final List<String> symbols) throws ExecutionException, InterruptedException {
       /* Future<SymbolSummary> symbolSummaryFuture = symbolsSummaries.get(symbol);
        if (symbolSummaryFuture == null){
            Callable<SymbolSummary> callable = new Callable<SymbolSummary>() {
                @Override
                public SymbolSummary call() throws Exception {
                    SymbolSummary symbolSummary = new SymbolSummary(historicMarketDataService, symbol);
                    symbolSummary.init();
                    return symbolSummary;
                }
            };
            FutureTask<SymbolSummary> ft = new FutureTask<>(callable);
            symbolSummaryFuture = symbolsSummaries.putIfAbsent(symbol, ft);
            if (symbolSummaryFuture == null){
                symbolSummaryFuture = ft;
                ft.run();
            }
        }*/
        return null;//symbolSummaryFuture.get();
    }

    public static class SymbolSummary {
        private final String symbol;
        private volatile double lastPrice = 100.0;
        private final HistoricMarketDataSubscription<Candle> subscription;

        private SymbolSummary(HistoricMarketDataService service, String symbol) {
            this.symbol = symbol;
            this.subscription = service.createCandlesubscription();
        }

        private boolean init() throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            this.subscription.setListener(new HistoricSubscriptionListener<Candle>() {
                @Override
                public void historicDataUpdated(List<Candle> candles) {
                    Collections.sort(candles, new Comparator<Candle>() {
                        @Override
                        public int compare(Candle c1, Candle c2) {
                            return (int) (c2.getTime() - c1.getTime());
                        }
                    });
                    lastPrice = candles.get(0).getClose();
                    latch.countDown();
                }
            });
            subscription.setSymbol(new HistoricSymbol(symbol, PriceType.BID, AggregationTimeUnit.DAY, 1));
            return latch.await(5, TimeUnit.SECONDS);
        }

        public double getLastPrice() {
            return lastPrice;
        }
    }
}
