package tween.oaks.profitcalculator.spinner;

import javax.swing.*;

/**
* @author ochtarfear
* @since 2/16/13
*/
class VariableStepSpinnerNumberModel extends SpinnerNumberModel {

    private volatile StepProvider stepProvider;

    public VariableStepSpinnerNumberModel(Comparable maximum) {
        super(0.0, 0.0, maximum, 0);
    }

    @Override
    public Object getNextValue() {
        Number value = (Number) getValue();
        double v = value.doubleValue() + stepProvider.getStep();
        return getMaximum().compareTo(v) < 0 ? null : v;
    }

    @Override
    public Object getPreviousValue() {
        Number value = (Number) getValue();
        Double v = value.doubleValue() - stepProvider.getStep();
        return v < 0 ? null : v;
    }

    void setStepProvider(StepProvider stepProvider) {
        this.stepProvider = stepProvider;
    }

}
