package amberle;

final class LoggingHelper {
    private static final String DELIMITER = "{}";

    private LoggingHelper() {
        //this should not be called
    }

    static String format(String template, Object... fields) {
        StringBuilder stringBuilder = new StringBuilder(64);
        int pos = 0;
        int startIndex = 0;
        while (pos < fields.length) {
            int index = template.indexOf(DELIMITER, startIndex);
            if (index == -1) {
                stringBuilder.append(template.substring(startIndex, template.length()))
                    .append(" WARNING:fields left: ")
                    .append(fields.length - pos)
                    .append('.');
                pos = fields.length;
                startIndex = template.length();
            } else {
                stringBuilder.append(template.substring(startIndex, index))
                    .append(fields[pos]);
                startIndex = index + 2;
                pos += 1;
            }
        }
        return stringBuilder.append(template.substring(startIndex, template.length()))
            .toString();
    }

    static void println(String template, Object... fields) {
        System.out.println(format(template, fields));
    }

}
