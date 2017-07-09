package tween.oaks.profitcalculator.spinner;


import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * @author ochtarfear
 * @since 1/26/13
 */
public class FloatingPointSpinner {
    private final JSpinner spinner;
    private volatile int fractionDigits = 0;
    private volatile int cursorPosition = -1;

    public FloatingPointSpinner(int columns, final Comparable maximum) {

        VariableStepSpinnerNumberModel model = new VariableStepSpinnerNumberModel(maximum);
        spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = new CursorSavingNumericEditor(spinner);

        final JFormattedTextField textField = editor.getTextField();
        final NumberFormatter formatter = new NumberFormatter() {
            @Override
            public void install(JFormattedTextField ftf) {
                super.install(ftf);
                textField.setCaretPosition(cursorPosition >= 0 ? cursorPosition : textField.getText().length());
            }

        };

        formatter.setMaximum(maximum);
        formatter.setMinimum(0.0);
        formatter.setCommitsOnValidEdit(true);
        formatter.setAllowsInvalid(false);

        textField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
            @Override
            public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                return formatter;
            }
        });

        model.setStepProvider(new StepProvider() {
            @Override
            public Double getStep() {
                int b = textField.getText().length() - textField.getCaretPosition();
                b -= fractionDigits > 0 ? fractionDigits + 1 : 0;
                if (b >= 0) {
                    b -= b / 4; // to allow for thousands separation
                } else {
                    b += 1; // to allow for fractal part separation
                }
                return Math.pow(10, b);
            }
        });

        textField.setColumns(columns);
        spinner.setEditor(editor);

        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                boolean hasFraction = fractionDigits > 0;
                boolean keyDelete = e.getKeyChar() == KeyEvent.VK_DELETE;
                boolean keyBackspace = e.getKeyChar() == KeyEvent.VK_DELETE;
                boolean cursorOnDecimalMark = (textField.getText().length() - textField.getCaretPosition()) == fractionDigits + 1;
                boolean cursorAfterDecimalMark = (textField.getText().length() - textField.getCaretPosition()) == fractionDigits;
                boolean deleteDecimalMark = hasFraction && (keyDelete && cursorOnDecimalMark || keyBackspace && cursorAfterDecimalMark);

                if (deleteDecimalMark) {
                    e.consume();  // ignore event
                }
            }
        });
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                cursorPosition = textField.getCaretPosition();
            }
        });
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                cursorPosition = textField.viewToModel(e.getPoint());
            }
        });
    }

    public Component getVisualComponent() {
        return spinner;
    }

    public void setEnabled(boolean value) {
        spinner.setEnabled(value);
    }

    public void addChangeListener(ChangeListener changeListener) {
        spinner.addChangeListener(changeListener);
    }

    public void setFractionDigits(int fractionDigits) {
        if (fractionDigits < 0) {
            throw new IllegalArgumentException("fraction digits number must be non-negative");
        }
        this.fractionDigits = fractionDigits;
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMaximumFractionDigits(fractionDigits);
        format.setMinimumFractionDigits(fractionDigits);
    }

    public static double roundToDecimals(double d, int c) {
        int temp = (int) ((d * Math.pow(10, c)));
        return (((double) temp) / Math.pow(10, c));
    }

    public void setValue(Double value) {
        spinner.setValue(roundToDecimals(value, fractionDigits));
        JFormattedTextField textField = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        textField.setCaretPosition(textField.getText().length());
    }

    public Double getValue() {
        return ((Number) spinner.getValue()).doubleValue();
    }

    public void addFocusListener(FocusListener focusListener) {
        ((JSpinner.NumberEditor) spinner.getEditor()).getTextField().addFocusListener(focusListener);
    }

    public void addButtonsMouseListener(MouseListener mouseListener) {
        for (Component component : spinner.getComponents()) {
            if (!(component instanceof CursorSavingNumericEditor))
                component.addMouseListener(mouseListener);
        }
    }

    public void resetCursorPosition() {
        cursorPosition = -1;
    }

}
