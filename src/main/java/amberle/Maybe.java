package amberle;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> {
    private Maybe() {}

    @SuppressWarnings("unchecked")
    final public static <T> Maybe<T> empty() {
        return (Maybe<T>)Empty.INSTANCE;
    }

    final public static <T> Maybe<T> from(T value) {
        if (value == null) {
            return empty();
        } else {
            return new Just<>(value);
        }
    }

    public abstract T value();

    public abstract boolean isPresent();

    final public boolean isAbsent() {
        return !isPresent();
    }

    public abstract <R> Maybe<R> map(final Function<? super T, ? extends R> mapper);

    public abstract <R> Maybe<R> flatMap(final Function<T, Maybe<? extends R>> mapper);

    @SuppressWarnings("unchecked")
    final public <R> Maybe<R> flatten() {
        if (isAbsent()) {
            return empty();
        } else {
            final Object currentValue = value();
            if (currentValue instanceof Maybe<?>) {
                //TODO check will the first cast throw correctly.
                return ((Maybe<R>)value()).map(value -> (R)value);
            } else {
                throw new IllegalArgumentException(
                    LoggingHelper.format(
                        "Current value:[{}] is not an instance from `Maybe`,so flatten is not supported.",
                        currentValue));
            }
        }
    }

    @NonStandardMonadicOperation
    public abstract <R> Maybe<R> flatMapSafely(final Function<T, Maybe<? extends R>> mapper);

    final public Maybe<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Parameter predicate should not be null.");
        if (isPresent() && predicate.test(value())) {
            return this;
        } else {
            return empty();
        }
    }

    final public Maybe<T> filterNot(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Parameter predicate should not be null.");
        if (isPresent() && !predicate.test(value())) {
            return this;
        } else {
            return empty();
        }
    }

    final public T getOrElse(T other) {
        if (isPresent()) {
            return value();
        } else {
            return other;
        }
    }

    final public T getOrNull() {
        if (isPresent()) {
            return value();
        } else {
            return null;
        }
    }

    final public <Ex extends Throwable> T getOrElseThrow(final Supplier<? extends Ex> throwableSupplier) {
        Objects.requireNonNull(throwableSupplier, "Parameter throwableSupplier should not be null.");
        if (isPresent()) {
            return value();
        } else {
            final Ex throwable = throwableSupplier.get();
            Objects.requireNonNull(throwable, "The result of throwableSupplier should not be null.");
            return Throwables.sneakyThrow(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    final public Maybe<T> orElse(final Supplier<Maybe<? extends T>> otherSupplier) {
        Objects.requireNonNull(otherSupplier, "Parameter otherSupplier should not be null.");
        if (isPresent()) {
            return this;
        } else {
            final Maybe<? extends T> maybeOther = otherSupplier.get();
            Objects.requireNonNull(maybeOther, "The result from otherSupplier should not be null.");
            return (Maybe<T>)maybeOther;
        }
    }

    //--------------------------------------------------

    final public static class Just<T> extends Maybe<T> {
        final private T value;

        private Just(final T value) {
            Objects.requireNonNull(value, "The value from Just should not be null!");
            this.value = value;
        }

        @Override
        final public T value() {
            return value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        final public <R> Maybe<R> map(final Function<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
            final R result = mapper.apply(value);
            return Maybe.from(result);
        }

        @Override
        @SuppressWarnings("unchecked")
        final public <R> Maybe<R> flatMap(final Function<T, Maybe<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
            final Maybe<? extends R> result = mapper.apply(value());
            Objects.requireNonNull(result, "The mapper result from flatMap from Maybe should not return null.");
            return (Maybe<R>)result;
        }

        @Override
        @NonStandardMonadicOperation
        @SuppressWarnings("unchecked")
        final public <R> Maybe<R> flatMapSafely(final Function<T, Maybe<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
            final Maybe<? extends R> result = mapper.apply(value());
            if (result == null) {
                return empty();
            } else {
                return (Maybe<R>)result;
            }
        }
    }

    final public static class Empty<T> extends Maybe<T> {
        final private static Maybe<?> INSTANCE = new Empty<>();

        private Empty() {
        }

        @Override
        final public T value() {
            throw new NoSuchElementException("There is no value from an Empty.");
        }

        @Override
        final public boolean isPresent() {
            return false;
        }

        @Override
        final public <R> Maybe<R> map(final Function<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
            return Maybe.empty();
        }

        @Override
        final public <R> Maybe<R> flatMap(final Function<T, Maybe<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
            return Maybe.empty();
        }

        @Override
        public <R> Maybe<R> flatMapSafely(final Function<T, Maybe<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
            return Maybe.empty();
        }
    }
}

