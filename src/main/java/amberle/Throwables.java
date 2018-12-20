package amberle;

import java.util.Objects;

final class Throwables {
    private Throwables() {
    }

    public static boolean isFatal(final Throwable throwable) {
        Objects.requireNonNull(throwable, "Parameter throwable should not be null.");
        return throwable instanceof VirtualMachineError ||
            throwable instanceof ThreadDeath ||
            throwable instanceof InterruptedException ||
            throwable instanceof LinkageError;

    }

    public static boolean isNonFatal(final Throwable throwable) {
        return !isFatal(throwable);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable, R> R sneakyThrow(final Throwable throwable) throws T {
        throw (T)throwable;
    }
}
