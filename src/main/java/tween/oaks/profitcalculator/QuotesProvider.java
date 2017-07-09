package tween.oaks.profitcalculator;

import com.tradable.api.services.marketdata.Quote;
import com.tradable.api.services.marketdata.QuoteTickEvent;
import com.tradable.api.services.marketdata.QuoteTickListener;
import com.tradable.api.services.marketdata.QuoteTickSubscription;

import java.util.Set;

/**
 * @author ochtarfear
 * @since 2/10/13
 */
public class QuotesProvider {

    private final QuoteTickSubscription quoteSubscription;

    private volatile QuoteTickListener listener;

    public QuotesProvider(QuoteTickSubscription quoteSubscription) {
        this.quoteSubscription = quoteSubscription;
    }

    public void addSymbols(Set<String> symbols){
        this.quoteSubscription.setListener(new QuoteTickListener() {
            @Override
            public void quotesUpdated(QuoteTickEvent quoteTickEvent) {
                if (listener != null){
                    listener.quotesUpdated(quoteTickEvent);
                }
            }
        });

        this.quoteSubscription.addSymbols(symbols);
    }

    public void setListener(QuoteTickListener listener) {
        this.listener = listener;
    }

    public double getBid(String symbol) {
        Quote bid = quoteSubscription.getBid(symbol);
        if (bid != null){
            return bid.getPrice();
        }
        return HardCodedQuotes.INSTANCE.getBid(symbol);
    }

    public double getAsk(String symbol) {
        Quote ask = quoteSubscription.getAsk(symbol);
        if (ask != null){
            return ask.getPrice();
        }
        return HardCodedQuotes.INSTANCE.getAsk(symbol);
    }
}
