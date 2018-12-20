/*
 * Copyright 2018 xyz.reactiveplatform.amberle
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

import java.util.Objects;

final class Throwables {
  private Throwables() {}

  public static boolean isFatal(final Throwable throwable) {
    Objects.requireNonNull(throwable, "Parameter throwable should not be null.");
    return throwable instanceof VirtualMachineError
        || throwable instanceof ThreadDeath
        || throwable instanceof InterruptedException
        || throwable instanceof LinkageError;
  }

  public static boolean isNonFatal(final Throwable throwable) {
    return !isFatal(throwable);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable, R> R sneakyThrow(final Throwable throwable) throws T {
    throw (T) throwable;
  }
}
