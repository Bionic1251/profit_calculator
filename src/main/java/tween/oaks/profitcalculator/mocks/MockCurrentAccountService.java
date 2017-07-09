package tween.oaks.profitcalculator.mocks;

import com.tradable.api.entities.*;
import com.tradable.api.services.account.CurrentAccountService;
import com.tradable.api.services.account.CurrentAccountServiceListener;

import java.util.List;

public class MockCurrentAccountService implements CurrentAccountService {
    @Override
    public Account getCurrentAccount() {
        Account account = new Account() {
            @Override
            public int getAccountId() {
                return 0;
            }

            @Override
            public Instrument getCurrency() {
                Instrument instrument = new Instrument() {
                    @Override
                    public int getId() {
                        return 0;
                    }

                    @Override
                    public String getSymbol() {
                        return "USD$";
                    }

                    @Override
                    public String getDescription() {
                        return "Some text";
                    }

                    @Override
                    public InstrumentType getType() {
                        return null;
                    }

                    @Override
                    public double getPrecision() {
                        return 5;
                    }

                    @Override
                    public Double getPriceIncrement() {
                        return null;
                    }
                };
                return instrument;
            }

            @Override
            public List<Order> getOrders() {
                return null;
            }

            @Override
            public List<Position> getPositions() {
                return null;
            }

            @Override
            public Order getOrder(int i) {
                return null;
            }

            @Override
            public List<Position> getPositions(int i) {
                return null;
            }

            @Override
            public Position getPosition(int i, String s) {
                return null;
            }

            @Override
            public NetPosition getNetPosition(int i) {
                return null;
            }
        };
        return account;
    }

    @Override
    public void addListener(CurrentAccountServiceListener currentAccountServiceListener) {
    }

    @Override
    public void removeListener(CurrentAccountServiceListener currentAccountServiceListener) {
    }
}
