package tween.oaks.profitcalculator;

import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleProperties;
import com.tradable.api.component.state.DefaultPersistedStateHolder;
import com.tradable.api.component.state.PersistedStateHolder;
import com.tradable.api.entities.Account;
import com.tradable.api.entities.Instrument;
import com.tradable.api.services.account.CurrentAccountService;
import com.tradable.api.services.conversion.ConversionRateListener;
import com.tradable.api.services.conversion.ConversionRateService;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.instrument.InstrumentServiceListener;
import com.tradable.api.services.instrument.InstrumentUpdateEvent;
import com.tradable.api.services.marketdata.QuoteTickEvent;
import com.tradable.api.services.marketdata.QuoteTickListener;
import com.tradable.api.services.marketdata.QuoteTickSubscription;
import tween.oaks.profitcalculator.spinner.FloatingPointSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ochtarfear
 * @since 1/6/13
 */
public class ProfitCalculator extends JPanel implements WorkspaceModule {

    private static final Double MAXIMUM_QUANTITY = 100000000.0;
    private static final Double MAXIMUM_PRICE = 100000.0;
    private static final int TOP_INSET = 2;
    private static final int LEFT_INSET = 5;
    private static final int BOTTOM_INSET = 2;
    private static final int RIGHT_INSET = 5;

    private static final int TEXT_FIELD_COLUMNS = 8;

    private static final Double DEFAULT_QUANTITY = 100000.0;
    private static final Double DEFAULT_CLOSE_PRICE_DIFFERENCE_IN_PIPS = 10.0;

    private final InstrumentComboBox instrumentCombo;
    private final FloatingPointSpinner quantitySpinner;
    private final FloatingPointSpinner closePriceSpinner;
    private final JLabel profitLabel;
    private final FloatingPointSpinner openPriceSpinner;
    private final QuotesProvider quotesProvider;
    private final ConversionRateService conversionRateService;
    private final ToggleButtonTradableView buySellToggleButton;
    private final ToggleButtonTradableView liveQuotesToggleButton;

    private final DecimalFormat currencyFormat = new DecimalFormat();
    private final static int CURRENCY_FRACTIONAL_DIGITS = 2;

    private final InstrumentService instrumentService;

    private final Set<String> symbols = new HashSet<>();
    private final CurrentAccountService currentAccountService;
    private volatile String currentSymbol;
    private volatile String chosenSymbol;

    private volatile boolean disabled = false;

    private final ConcurrentHashMap<String, SymbolDefaults> defaults = new ConcurrentHashMap<>();

    private static class SymbolDefaults {
        boolean liveQuote = true;
        boolean buy = true;
        double quantity = DEFAULT_QUANTITY;
        double openPrice = 0;
        double closePrice = 0;

        public SymbolDefaults(boolean liveQuote, boolean buy, double quantity, double openPrice, double closePrice) {
            this.liveQuote = liveQuote;
            this.buy = buy;
            this.quantity = quantity;
            this.openPrice = openPrice;
            this.closePrice = closePrice;
        }

    }

    public ProfitCalculator(QuoteTickSubscription quoteSubscription, InstrumentService instrumentService, ConversionRateService conversionRateService, CurrentAccountService currentAccountService, InstrumentComboBox comboBox) {
        this.currentAccountService = currentAccountService;
        putClientProperty(WorkspaceModuleProperties.COMPONENT_TITLE, AppSettings.APP_NAME);
        putClientProperty(WorkspaceModuleProperties.COMPONENT_RESIZE_ENABLED, false);
        setPreferredSize(new Dimension(250, 250));

        instrumentCombo = comboBox;

        this.instrumentService = instrumentService;
        this.quotesProvider = new QuotesProvider(quoteSubscription);
        this.conversionRateService = conversionRateService;
        setLayout(new GridBagLayout());

        currencyFormat.setMaximumFractionDigits(CURRENCY_FRACTIONAL_DIGITS);
        currencyFormat.setMinimumFractionDigits(CURRENCY_FRACTIONAL_DIGITS);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Symbol:"), constraints);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(instrumentCombo.getComponent(), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Side:"), constraints);
        buySellToggleButton = new ToggleButtonTradableView("SELL", "BUY");
        buySellToggleButton.setSelected(true);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(buySellToggleButton, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Open price:"), constraints);
        openPriceSpinner = new FloatingPointSpinner(TEXT_FIELD_COLUMNS, MAXIMUM_PRICE);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(openPriceSpinner.getVisualComponent(), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Live quote:"), constraints);
        liveQuotesToggleButton = new ToggleButtonTradableView("OFF", "ON");
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(liveQuotesToggleButton, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Quantity:"), constraints);
        quantitySpinner = new FloatingPointSpinner(TEXT_FIELD_COLUMNS, MAXIMUM_QUANTITY);
        quantitySpinner.setFractionDigits(0);
        quantitySpinner.setValue(DEFAULT_QUANTITY);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(quantitySpinner.getVisualComponent(), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Close price:"), constraints);
        closePriceSpinner = new FloatingPointSpinner(TEXT_FIELD_COLUMNS, MAXIMUM_PRICE);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(closePriceSpinner.getVisualComponent(), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(new JLabel("Profit/Loss:"), constraints);
        profitLabel = new JLabel();
        Font defaultFont = UIManager.getFont("Label.font");
        Font font = new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 4);
        profitLabel.setFont(font);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 6;
        constraints.insets = new Insets(TOP_INSET, LEFT_INSET, BOTTOM_INSET, RIGHT_INSET);
        add(profitLabel, constraints);

    }

    public void initialize() {
        Collection<Instrument> instruments = instrumentService.getInstruments();
        addSymbols(instruments);
        instrumentService.addListener(new InstrumentServiceListener() {
            @Override
            public void instrumentsUpdated(InstrumentUpdateEvent instrumentUpdateEvent) {
                if (instrumentUpdateEvent.isReset()){
                    symbols.clear();
                    disableApp();
                }
                addSymbols(instrumentUpdateEvent.getUpdatedInstruments().values());
            }
        });

        instrumentCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String symbol = instrumentCombo.getSelectedItem();
                selectSymbol(symbol);
            }
        });

        liveQuotesToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (liveQuotesToggleButton.isSelected()) {
                    openPriceSpinner.setValue(buySellToggleButton.isSelected() ? getCurrentAsk() : getCurrentBid());
                }
            }
        });

        openPriceSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                recalculateProfit();
            }
        });

        openPriceSpinner.addButtonsMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (liveQuotesToggleButton.isSelected()) {
                    liveQuotesToggleButton.setSelected(false);
                }
            }
        });

        openPriceSpinner.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (liveQuotesToggleButton.isSelected()) {
                    liveQuotesToggleButton.setSelected(false);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        this.quotesProvider.setListener(new QuoteTickListener() {
            @Override
            public void quotesUpdated(QuoteTickEvent quoteTickEvent) {
                if (liveQuotesToggleButton.isSelected()) {
                    double openPriceSpinnerValue = openPriceSpinner.getValue();
                    openPriceSpinner.setValue(buySellToggleButton.isSelected() ? getCurrentAsk() : getCurrentBid());
                    if (closePriceSpinner.getValue() == 0 && openPriceSpinnerValue == 0){
                        // persisted state loading complications
                        double delta = instrumentService.getInstrument(currentSymbol).getPriceIncrement() * DEFAULT_CLOSE_PRICE_DIFFERENCE_IN_PIPS;
                        double closePrice = buySellToggleButton.isSelected() ? getCurrentAsk() + delta : getCurrentBid() - delta;
                        closePriceSpinner.setValue(closePrice);
                    }
                } else {
                    recalculateProfit();
                }
            }
        });

        quantitySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                recalculateProfit();
            }
        });

        closePriceSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                recalculateProfit();
            }
        });

        buySellToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (liveQuotesToggleButton.isSelected()) {
                    openPriceSpinner.setValue(buySellToggleButton.isSelected() ? getCurrentAsk() : getCurrentBid());
                } else {
                    recalculateProfit();
                }
            }
        });

        if (!symbols.isEmpty()) {
            String symbol = symbols.iterator().next();
            instrumentCombo.setSelectedSymbol(symbol);
        } else {
            disableApp();
        }
    }

    private void selectSymbol(String symbol) {
        if (!isSymbolValid(symbol)) {
            disableApp();
            return;
        }
        if (disabled) {
            enableApp();
        }
        if (symbol.equals(currentSymbol)) {
            return;
        }
        if (currentSymbol != null) {
            defaults.put(currentSymbol, new SymbolDefaults(liveQuotesToggleButton.isSelected(), buySellToggleButton.isSelected(), quantitySpinner.getValue(), liveQuotesToggleButton.isSelected() ? 1 : openPriceSpinner.getValue(), closePriceSpinner.getValue()));
        }
        currentSymbol = symbol;
        Instrument instrument = instrumentService.getInstrument(currentSymbol);
        double precision = instrument.getPrecision();
        closePriceSpinner.setFractionDigits((int) precision);
        openPriceSpinner.setFractionDigits((int) precision);
        resetFieldsToDefaults();
    }

    private void addSymbols(Collection<Instrument> instruments) {
        for (Instrument instrument : instruments) {
            symbols.add(instrument.getSymbol());
        }
        this.quotesProvider.addSymbols(symbols);
        if (disabled && chosenSymbol != null && symbols.contains(chosenSymbol)){
            instrumentCombo.setSelectedSymbol(chosenSymbol);
        }
    }

    private void disableApp() {
        disabled = true;
        openPriceSpinner.setEnabled(false);
        closePriceSpinner.setEnabled(false);
        quantitySpinner.setEnabled(false);
        buySellToggleButton.setEnabled(false);
        liveQuotesToggleButton.setEnabled(false);
        profitLabel.setText("");
    }

    private void enableApp() {
        disabled = false;
        openPriceSpinner.setEnabled(true);
        closePriceSpinner.setEnabled(true);
        quantitySpinner.setEnabled(true);
        buySellToggleButton.setEnabled(true);
        liveQuotesToggleButton.setEnabled(true);
        recalculateProfit();
    }

    private void resetFieldsToDefaults() {
        SymbolDefaults symbolDefaults = defaults.get(currentSymbol);
        if (symbolDefaults != null) {
            liveQuotesToggleButton.setSelected(symbolDefaults.liveQuote);
            buySellToggleButton.setSelected(symbolDefaults.buy);
            double openPrice;
            if (liveQuotesToggleButton.isSelected()) {
                openPrice = buySellToggleButton.isSelected() ? getCurrentAsk() : getCurrentBid();
            } else {
                openPrice = symbolDefaults.openPrice;
            }
            openPriceSpinner.setValue(openPrice);
            quantitySpinner.setValue(symbolDefaults.quantity);
            closePriceSpinner.setValue(symbolDefaults.closePrice);
        } else {
            liveQuotesToggleButton.setSelected(true);
            buySellToggleButton.setSelected(true);
            double currentAsk = getCurrentAsk();
            openPriceSpinner.setValue(currentAsk);
            quantitySpinner.setValue(DEFAULT_QUANTITY);
            double delta = instrumentService.getInstrument(currentSymbol).getPriceIncrement() * DEFAULT_CLOSE_PRICE_DIFFERENCE_IN_PIPS;
            closePriceSpinner.setValue(currentAsk + delta);
        }
        openPriceSpinner.resetCursorPosition();
        quantitySpinner.resetCursorPosition();
        closePriceSpinner.resetCursorPosition();
        recalculateProfit();
    }

    private boolean isSymbolValid(String symbol) {
        for (String s : symbols) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }


    private double getCurrentBid() {
        return quotesProvider.getBid(currentSymbol);
    }

    private double getCurrentAsk() {
        return quotesProvider.getAsk(currentSymbol);
    }

    private void recalculateProfit() {
        if (currentSymbol == null || disabled) {
            return;
        }
        boolean isBuy = buySellToggleButton.isSelected();
        double closePrice = closePriceSpinner.getValue();
        double quantity = (isBuy ? 1 : -1) * quantitySpinner.getValue();
        double tradedPrice = openPriceSpinner.getValue();
        String symbol = currentSymbol;
        String profitCurrency = getInstrumentQuoteCurrency(symbol);
        final double profit = (closePrice - tradedPrice) * quantity;

        String accountCurrency = getAccountCurrency();
        if (accountCurrency != null && conversionRateService != null){
            conversionRateService.getRate(profitCurrency, accountCurrency, new ConversionRateListener() {
                @Override
                public void conversionRateAvailable(String pc, String ac, double rate) {
                    if (Double.isNaN(rate)){
                        profitLabel.setText(currencyFormat.format(profit) + " " + pc);
                    } else {
                        profitLabel.setText(currencyFormat.format(rate * profit) + " " + ac);
                    }
                }
            });
        }
    }

    private String getInstrumentQuoteCurrency(String symbol) {
        if (symbol.contains("/")) {
            String[] currencies = symbol.split("/");
            return currencies[currencies.length - 1];
        }
        return symbol.substring(3);
    }

    private String getAccountCurrency() {
        Account currentAccount = currentAccountService.getCurrentAccount();
        if (currentAccount == null){
            // still loading - it'll come
            return null;
        }
        Instrument currency = currentAccount.getCurrency();
        String symbol = currency.getSymbol();
        return symbol.substring(0, 3);
    }

    @Override
    public JComponent getVisualComponent() {
        return this;
    }

    @Override
    public void destroy() {
        if (instrumentCombo != null) {
            instrumentCombo.destroy();
        }
    }

    @Override
    public PersistedStateHolder getPersistedState() {
        DefaultPersistedStateHolder defaultPersistedStateHolder = new DefaultPersistedStateHolder();
        defaultPersistedStateHolder.setProperty("instrument", currentSymbol);
        return defaultPersistedStateHolder;
    }

    @Override
    public void loadPersistedState(PersistedStateHolder persistedStateHolder) {
        String symbol = persistedStateHolder.getProperty("instrument");
        if (symbol != null && !symbol.isEmpty()){
            if (isSymbolValid(symbol)){
                instrumentCombo.setSelectedSymbol(symbol);
            } else {
                chosenSymbol = symbol;
            }
        }
    }

}
