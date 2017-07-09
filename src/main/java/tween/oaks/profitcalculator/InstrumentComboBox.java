package tween.oaks.profitcalculator;

import java.awt.*;
import java.awt.event.ActionListener;

public interface InstrumentComboBox {
    void addActionListener(ActionListener listener);
    String getSelectedItem();
    void setSelectedSymbol(String symbol);
    void destroy();

    Component getComponent();
}
