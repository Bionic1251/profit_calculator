package tween.oaks.profitcalculator;

import com.tradable.api.entities.Instrument;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.ui.widgets.autocomplete.model.AutoCompleteModel;
import com.tradable.api.ui.widgets.selector.SelectionListener;
import com.tradable.api.widgets.InstrumentSelector;

import java.awt.*;
import java.awt.event.ActionListener;

public class TradableInstrumentComboBox implements InstrumentComboBox {

    private final InstrumentSelector instrumentSelector = new InstrumentSelector();

    public TradableInstrumentComboBox(InstrumentService instrumentService, AutoCompleteModel autoCompleteModel) {
        instrumentSelector.setInstrumentService(instrumentService);
        instrumentSelector.setAutoCompleteModel(autoCompleteModel);
        instrumentSelector.initialize();
    }

    @Override
    public void addActionListener(final ActionListener listener) {
        instrumentSelector.addSelectionListener(new SelectionListener<Instrument>() {
            @Override
            public void selectionChanged(Instrument instrument) {
                listener.actionPerformed(null);
            }
        });
    }

    @Override
    public String getSelectedItem() {
        return instrumentSelector.getSelectedValueAsText();
    }

    @Override
    public void setSelectedSymbol(String symbol) {
        //todo is this correct??
        instrumentSelector.setSelectedValueAsText(symbol);
    }

    @Override
    public void destroy() {
        instrumentSelector.destroy();
    }

    @Override
    public Component getComponent() {
        return instrumentSelector;
    }
}
