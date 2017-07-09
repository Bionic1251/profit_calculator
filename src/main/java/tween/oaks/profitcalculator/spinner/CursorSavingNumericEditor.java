package tween.oaks.profitcalculator.spinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
* @author ochtarfear
* @since 2/16/13
*/
class CursorSavingNumericEditor extends JSpinner.NumberEditor {

    public CursorSavingNumericEditor(JSpinner spinner) {
        super(spinner);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int caretPosition = getTextField().getCaretPosition();
        int length = getTextField().getText().length();
        super.stateChanged(e);
        caretPosition += getTextField().getText().length() - length;
        getTextField().setCaretPosition(caretPosition);
    }

}
