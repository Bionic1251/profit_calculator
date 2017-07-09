package tween.oaks.profitcalculator;

import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleCategory;
import com.tradable.api.component.WorkspaceModuleFactory;
import com.tradable.api.services.account.CurrentAccountService;
import com.tradable.api.services.conversion.ConversionRateService;
import com.tradable.api.services.historicmarketdata.HistoricMarketDataService;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.marketdata.QuoteTickService;
import com.tradable.api.services.marketdata.QuoteTickSubscription;
import com.tradable.api.ui.widgets.autocomplete.model.AutoCompleteModel;
import org.springframework.beans.factory.annotation.Autowired;
import tween.oaks.profitcalculator.mocks.LocalInstrumentComboBox;

/**
 * @user: ochtarfear
 * @date: 1/6/13
 */
public class ProfitCalculatorFactory implements WorkspaceModuleFactory {

    @Autowired
    private CurrentAccountService currentAccountService;
    @Autowired
    private ConversionRateService conversionRateService;
    @Autowired
    private QuoteTickService quoteTickerService;
    @Autowired
    protected InstrumentService instrumentService;
    @Autowired
    protected AutoCompleteModel autoCompleteModel;

    @Override
    public WorkspaceModuleCategory getCategory() {
        return WorkspaceModuleCategory.ANALYSIS;
    }

    @Override
    public String getDisplayName() {
        return AppSettings.APP_NAME;
    }

    @Override
    public String getFactoryId() {
        return "profit-calculator.factory";
    }

    @Override
    public WorkspaceModule createModule() {
        InstrumentComboBox instrumentComboBox = getInstrumentComboBox();
        QuoteTickSubscription quoteTickSubscription = quoteTickerService.createSubscription();
        ProfitCalculator profitCalculator = new ProfitCalculator(quoteTickSubscription, instrumentService, conversionRateService, currentAccountService, instrumentComboBox);
        profitCalculator.initialize();
        return profitCalculator;
    }

    private InstrumentComboBox getInstrumentComboBox() {
        TradableInstrumentComboBox instrumentComboBox = new TradableInstrumentComboBox(instrumentService, autoCompleteModel);
        return instrumentComboBox;
    }
}
