/*
 * Copyright 2018 amberle-core
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package amberle;

import amberle.annotation.NonStandardMonadicOperation;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> {

  private Maybe() {}

  @SuppressWarnings("unchecked")
  public static final <T> Maybe<T> empty() {
    return (Maybe<T>) Empty.INSTANCE;
  }

  public static final <T> Maybe<T> from(T value) {
    if (value == null) {
      return empty();
    } else {
      return new Just<>(value);
    }
  }

  public abstract T value();

  public abstract boolean isPresent();

  public final boolean isDefined() {
    return isPresent();
  }

  public final boolean nonEmpty() {
    return isPresent();
  }

  public final boolean isAbsent() {
    return !isPresent();
  }

  public final boolean isEmpty() {
    return isAbsent();
  }

  public abstract <R> Maybe<R> map(final Function<? super T, ? extends R> mapper);

  public abstract <R> Maybe<R> flatMap(
      final Function<? super T, ? extends Maybe<? extends R>> mapper);

  @SuppressWarnings("unchecked")
  public final <R> Maybe<R> flatten() {
    if (isAbsent()) {
      return empty();
    } else {
      final Object currentValue = value();
      if (currentValue instanceof Maybe<?>) {
        // TODO check will the first cast throw correctly.
        return ((Maybe<R>) value()).map(value -> (R) value);
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

  public final Maybe<T> filter(final Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate, "Parameter predicate should not be null.");
    if (isPresent() && predicate.test(value())) {
      return this;
    } else {
      return empty();
    }
  }

  public final Maybe<T> filterNot(final Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate, "Parameter predicate should not be null.");
    if (isPresent() && !predicate.test(value())) {
      return this;
    } else {
      return empty();
    }
  }

  public final boolean contains(final T other) {
    return isPresent() && value().equals(other);
  }

  public final boolean exists(final Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate, "Parameter predicate should not be null.");
    return isPresent() && predicate.test(value());
  }

  public final boolean forAll(final Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate, "Parameter predicate should not be null.");
    return isAbsent() || predicate.test(value());
  }

  public final void forEach(final Consumer<T> consumer) {
    Objects.requireNonNull(consumer, "Parameter consumer should not be null.");
    if (isPresent()) {
      consumer.accept(value());
    }
  }

  public final T getOrElse(T other) {
    if (isPresent()) {
      return value();
    } else {
      return other;
    }
  }

  public final T getOrNull() {
    if (isPresent()) {
      return value();
    } else {
      return null;
    }
  }

  public final <Ex extends Throwable> T getOrElseThrow(
      final Supplier<? extends Ex> throwableSupplier) {
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
  public final Maybe<T> orElse(final Supplier<Maybe<? extends T>> otherSupplier) {
    Objects.requireNonNull(otherSupplier, "Parameter otherSupplier should not be null.");
    if (isPresent()) {
      return this;
    } else {
      final Maybe<? extends T> maybeOther = otherSupplier.get();
      Objects.requireNonNull(maybeOther, "The result from otherSupplier should not be null.");
      return (Maybe<T>) maybeOther;
    }
  }

  // --------------------------------------------------

  public static final class Just<T> extends Maybe<T> {
    private final T value;

    private Just(final T value) {
      Objects.requireNonNull(value, "The value from Just should not be null!");
      this.value = value;
    }

    @Override
    public final T value() {
      return value;
    }

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public final <R> Maybe<R> map(final Function<? super T, ? extends R> mapper) {
      Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
      final R result = mapper.apply(value);
      return Maybe.from(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <R> Maybe<R> flatMap(
        final Function<? super T, ? extends Maybe<? extends R>> mapper) {
      Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
      final Maybe<? extends R> result = mapper.apply(value());
      Objects.requireNonNull(
          result, "The mapper result from flatMap from Maybe should not return null.");
      return (Maybe<R>) result;
    }

    @Override
    @NonStandardMonadicOperation
    @SuppressWarnings("unchecked")
    public final <R> Maybe<R> flatMapSafely(final Function<T, Maybe<? extends R>> mapper) {
      Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
      final Maybe<? extends R> result = mapper.apply(value());
      if (result == null) {
        return empty();
      } else {
        return (Maybe<R>) result;
      }
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Just)) {
        return false;
      }
      final Just<?> just = (Just<?>) o;
      return value.equals(just.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  public static final class Empty<T> extends Maybe<T> {
    private static final Maybe<?> INSTANCE = new Empty<>();

    private Empty() {}

    @Override
    public final T value() {
      throw new NoSuchElementException("There is no value from an Empty.");
    }

    @Override
    public final boolean isPresent() {
      return false;
    }

    @Override
    public final <R> Maybe<R> map(final Function<? super T, ? extends R> mapper) {
      Objects.requireNonNull(mapper, "Parameter mapper should not be null.");
      return Maybe.empty();
    }

    @Override
    public final <R> Maybe<R> flatMap(
        final Function<? super T, ? extends Maybe<? extends R>> mapper) {
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
