package tween.oaks.profitcalculator.mocks;

import com.tradable.api.entities.Instrument;
import com.tradable.api.services.instrument.InstrumentService;
import tween.oaks.profitcalculator.InstrumentComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

public class LocalInstrumentComboBox implements InstrumentComboBox {

    private final JComboBox<String> comboBox;

    public LocalInstrumentComboBox(InstrumentService instrumentService) {
        Collection<Instrument> instruments = instrumentService.getInstruments();
        String[] symbols = new String[instruments.size()];
        int i = 0;
        for (Instrument instrument : instruments) {
            symbols[i] = instrument.getSymbol();
            i++;
        }
        comboBox = new JComboBox<>(symbols);
    }

    @Override
    public void addActionListener(ActionListener listener) {
        comboBox.addActionListener(listener);
    }

    @Override
    public String getSelectedItem() {
        return (String) comboBox.getSelectedItem();
    }

    @Override
    public void setSelectedSymbol(String symbol) {
        comboBox.setSelectedItem(symbol);
    }

    @Override
    public void destroy() {
    }

    @Override
    public Component getComponent() {
        return comboBox;
    }
}
