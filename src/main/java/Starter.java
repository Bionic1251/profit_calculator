import com.tradable.api.component.state.DefaultPersistedStateHolder;
import tween.oaks.profitcalculator.*;
import tween.oaks.profitcalculator.mocks.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author ochtarfear
 * @since 1/6/13
 */
public class Starter extends JFrame {

    public Starter() throws HeadlessException {
        setTitle(AppSettings.APP_NAME);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MockInstrumentService instrumentService = new MockInstrumentService();
        LocalInstrumentComboBox instrumentComboBox = new LocalInstrumentComboBox(instrumentService);
        ProfitCalculator pc = new ProfitCalculator(new QuoteGenerator(), instrumentService, null, new MockCurrentAccountService(), instrumentComboBox);
        DefaultPersistedStateHolder defaultPersistedStateHolder = new DefaultPersistedStateHolder();
        defaultPersistedStateHolder.setProperty("instrument", "EURJPY");
        pc.initialize();
        pc.loadPersistedState(defaultPersistedStateHolder);

        getContentPane().add(pc.getVisualComponent());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Starter ex = new Starter();
                ex.setVisible(true);
            }
        });
    }

}
