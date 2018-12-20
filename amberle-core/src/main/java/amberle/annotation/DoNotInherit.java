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

/*
 * Copyright (C) 2017-2018 Lightbend Inc. <https://www.lightbend.com>
 */
package amberle.annotation;

import java.lang.annotation.*;

/**
 * Marks APIs that are designed under an closed-world assumption for and are NOT meant to be
 * extended by user-code. It is fine to extend these classes within Akka itself however.
 *
 * <p>This is most useful for binary compatibility purposes when a set of classes and interfaces
 * assume a "closed world" between them, and gain the ability to add methods to the interfaces
 * without breaking binary compatibility for users of this code. Specifically this assumption may be
 * understood intuitively: as all classes that implement this interface are in this compilation unit
 * / artifact, it is impossible to obtain a "old" class with a "new" interface, as they are part of
 * the same dependency.
 *
 * <p>Notable examples of such API include the FlowOps trait in Akka Streams or Akka HTTP model
 * interfaces, which extensively uses inheritance internally, but are not meant for extension by
 * user code.
 */
@Documented
@Retention(RetentionPolicy.CLASS) // to be accessible by MiMa
@Target({ElementType.TYPE})
public @interface DoNotInherit {}
