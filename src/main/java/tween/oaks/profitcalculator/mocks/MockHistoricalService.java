package tween.oaks.profitcalculator.mocks;

import com.tradable.api.services.historicmarketdata.*;

/**
 * @author ochtarfear
 * @since 2/10/13
 */
public class MockHistoricalService implements HistoricMarketDataService {
    @Override
    public HistoricMarketDataSubscription<Candle> createCandlesubscription() {
        return new CandleHistoricMarketDataSubscription();
    }

    private static class CandleHistoricMarketDataSubscription implements HistoricMarketDataSubscription<Candle> {

        private volatile String symbol;

        @Override
        public void setSymbol(HistoricSymbol historicSymbol, long l) {
            setSymbol(historicSymbol);
        }

        @Override
        public void setSymbol(HistoricSymbol historicSymbol) {
            this.symbol = symbol;
        }

        @Override
        public void setListener(HistoricSubscriptionListener<Candle> candleHistoricSubscriptionListener) {
            // implement the bitch
        }

        @Override
        public void destroy() {
            // implement the bitch
        }
    }
}
