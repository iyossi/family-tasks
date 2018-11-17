package application.bl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class TransactionHelper {
    @Transactional
    public <T> T withTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional
    public void withTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(readOnly = true)
    public <T> T withTransactionRO(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(readOnly = true)
    public void withTransactionRO(Runnable runnable) {
        runnable.run();
    }
}
