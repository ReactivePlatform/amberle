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

final class LoggingHelper {
  private static final String DELIMITER = "{}";

  private LoggingHelper() {
    // this should not be called
  }

  static String format(String template, Object... fields) {
    StringBuilder stringBuilder = new StringBuilder(64);
    int pos = 0;
    int startIndex = 0;
    while (pos < fields.length) {
      int index = template.indexOf(DELIMITER, startIndex);
      if (index == -1) {
        stringBuilder
            .append(template.substring(startIndex, template.length()))
            .append(" WARNING:fields left: ")
            .append(fields.length - pos)
            .append('.');
        pos = fields.length;
        startIndex = template.length();
      } else {
        stringBuilder.append(template.substring(startIndex, index)).append(fields[pos]);
        startIndex = index + 2;
        pos += 1;
      }
    }
    return stringBuilder.append(template.substring(startIndex, template.length())).toString();
  }

  static void println(String template, Object... fields) {
    System.out.println(format(template, fields));
  }
}
