package unnamed.mmo.logging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.auto.service.AutoService;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;
import org.tinylog.writers.AbstractFileBasedWriter;
import org.tinylog.writers.Writer;
import org.tinylog.writers.raw.ByteArrayWriter;

/**
 * Copy of {@link org.tinylog.writers.JsonWriter} with some modifications to join the config with the log
 */
@AutoService(Writer.class)
public final class CustomJsonWriter extends AbstractFileBasedWriter {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String FIELD_PREFIX = "field.";

    private final Charset charset;
    private final ByteArrayWriter writer;

    private StringBuilder builder;
    private final Map<String, Token> jsonProperties;

    public CustomJsonWriter() throws IOException {
        this(Collections.<String, String>emptyMap());
    }

    public CustomJsonWriter(final Map<String, String> properties) throws IOException {
        super(properties);

        String fileName = getFileName();
        boolean append = getBooleanValue("append");
        boolean buffered = getBooleanValue("buffered");
        boolean writingThread = getBooleanValue("writingthread");

        charset = getCharset();
        writer = createByteArrayWriter(fileName, append, buffered, !writingThread, false, charset);

        jsonProperties = createTokens(properties);

        if (writingThread) {
            builder = new StringBuilder();
        }
    }

    @Override
    public void write(final LogEntry logEntry) throws IOException {
        StringBuilder builder;
        if (this.builder == null) {
            builder = new StringBuilder();
        } else {
            builder = this.builder;
            builder.setLength(0);
        }

        addJsonObject(logEntry, builder);

        byte[] data = builder.toString().getBytes(charset);
        writer.write(data, 0, data.length);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    @Override
    public Collection<LogEntryValue> getRequiredLogEntryValues() {
        Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
        values.add(LogEntryValue.CONTEXT);
        for (Token token : jsonProperties.values()) {
            values.addAll(token.getRequiredLogEntryValues());
        }
        return values;
    }

    private void addJsonObject(final LogEntry logEntry, final StringBuilder builder) {
        builder.append("{");

        Map<String, Object> entries = new HashMap<>();
        if (logEntry.getContext() != null)
            entries.putAll(logEntry.getContext());
        entries.putAll(jsonProperties);

        Object[] tokenEntries = entries.values().toArray(new Object[0]);
        String[] fields = entries.keySet().toArray(new String[0]);

        for (int i = 0; i < tokenEntries.length; i++) {
            builder.append("\"").append(fields[i]).append("\":\"");
            int start = builder.length();

            Object entry = tokenEntries[i];
            if (entry instanceof Token token) {
                token.render(logEntry, builder);
            } else {
                builder.append(entry.toString());
            }

            escapeCharacter("\\", "\\\\", builder, start);
            escapeCharacter("\"", "\\\"", builder, start);
            escapeCharacter(NEW_LINE, "\\n", builder, start);
            escapeCharacter("\t", "\\t", builder, start);
            escapeCharacter("\b", "\\b", builder, start);
            escapeCharacter("\f", "\\f", builder, start);
            escapeCharacter("\n", "\\n", builder, start);
            escapeCharacter("\r", "\\r", builder, start);

            builder.append("\"");

            if (i + 1 < entries.size()) {
                builder.append(",");
            }
        }
        builder.append("}").append(NEW_LINE);
    }

    private void escapeCharacter(final String character, final String escapeWith, final StringBuilder stringBuilder,
                                 final int startIndex) {
        for (
                int index = stringBuilder.indexOf(character, startIndex);
                index != -1;
                index = stringBuilder.indexOf(character, index + escapeWith.length())
        ) {
            stringBuilder.replace(index, index + character.length(), escapeWith);
        }
    }

    private static Map<String, Token> createTokens(final Map<String, String> properties) {
        FormatPatternParser parser = new FormatPatternParser(properties.get("exception"));

        Map<String, Token> tokens = new HashMap<>();
        for (Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT).startsWith(FIELD_PREFIX)) {
                tokens.put(entry.getKey().substring(FIELD_PREFIX.length()), parser.parse(entry.getValue()));
            }
        }
        return tokens;
    }

}

