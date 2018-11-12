package io.ruck.clop;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ruckc
 */
public final class Options {

    private static final Logger LOG = LogManager.getLogger();

    private final List<Option<?>> options = new ArrayList<>();
    private final HashMap<String, Option<?>> longOptions = new HashMap<>();
    private final HashMap<String, Option<?>> shortOptions = new HashMap<>();

    public Options() {
    }

    private void addOption(Option<?> opt) {
        this.options.add(opt);
        if (!opt.getLongName().isPresent() && !opt.getShortName().isPresent()) {

        }
        opt.getLongName().ifPresent(name -> longOptions.put("--" + name, opt));
        opt.getShortName().ifPresent(name -> shortOptions.put("-" + name, opt));
    }

    public String generateHelp() {
        int maxWidth = 80;
        int leftMargin = 1;
        String leftMarginStr = new String(new char[leftMargin]).replace('\0', ' ');
        int colSeparator = 4;
        int leftColumn = 0;
        int rightColumn = 0;

        // Figure out max left column width
        for (Option<?> o : options) {
            int leftSize = 0;
            if (o.getLongName().isPresent()) {
                leftSize += o.getLongName().get().length() + 2;
            }
            if (o.getShortName().isPresent()) {
                leftSize += o.getShortName().get().length() + 1;
            }
            if (o.getLongName().isPresent() && o.getShortName().isPresent()) {
                leftSize += 2;
            }
            if (leftColumn < leftSize) {
                leftColumn = leftSize;
            }
        }
        final int lc = leftColumn;

        StringBuilder sb = new StringBuilder();
        options.forEach(opt -> {
            int width = leftMargin;
            sb.append(leftMarginStr);
            if (opt.getShortName().isPresent()) {
                width += 1 + opt.getShortName().get().length();
                sb.append("-").append(opt.getShortName().get());
                if (opt.getLongName().isPresent()) {
                    width += 2;
                    sb.append(", ");
                }
            }
            if (opt.getLongName().isPresent()) {
                width += 2 + opt.getLongName().get().length();
                sb.append("--").append(opt.getLongName().get());
            }
            sb.append(new String(new char[(lc - width) + colSeparator]).replace('\0', ' '));
            // TODO: do word wrapping
            sb.append(opt.getDescription());
            sb.append("\n");
        });
        return sb.toString();
    }

    public OptionBuilder<String> stringOption(String description) {
        return new OptionBuilder<>(StringOption.class, description);
    }

    public OptionBuilder<Integer> integerOption(String description) {
        return new OptionBuilder<>(IntegerOption.class, description);
    }

    public OptionBuilder<InetAddress> inetOption(String description) {
        return new OptionBuilder<>(InetAddressOption.class, description);
    }

    public OptionBuilder<Boolean> booleanOption(String description) {
        return new OptionBuilder<>(BooleanOption.class, description);
    }

    public void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String opt = args[i];
            LOG.trace("working " + i + " option: " + opt);
            Option<?> option = findOption(opt);
            LOG.trace("   found: " + option);
            if (option != null) {
                switch (option.getType()) {
                    case SINGLE_VALUE:
                        String val = args[++i];
                        LOG.trace("  parsing " + val);
                        option.parseAndSetValue(val);
                        break;
                    case BOOLEAN:
                        option.parseAndSetValue("true");
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + option.getType() + " for option: " + opt);
                }
            } else {
                LOG.warn("Unknown option " + opt);
            }
        }
    }

    private Option<?> findOption(String val) {
        LOG.trace("findOption(" + val + ")");
        boolean longOption = val.startsWith("--");
        boolean shortOption = val.startsWith("-");
        if (longOption) {
            LOG.trace("Checking for " + val + " in " + longOptions.keySet());
            return longOptions.get(val);
        }
        if (shortOption) {
            LOG.trace("Checking for " + val + " in " + shortOptions.keySet());
            return shortOptions.get(val);
        }
        throw new IllegalArgumentException(val);
    }

    public class OptionBuilder<T> {

        private final Class<? extends Option<T>> cls;
        private final List<Validator<T>> validators = new ArrayList<>();
        private final String description;
        private String shortName = null;
        private String longName = null;
        private T defaultValue = null;

        private OptionBuilder(Class<? extends Option<T>> cls, String description) {
            this.cls = cls;
            this.description = description;
        }

        public OptionBuilder<T> shortName(String name) {
            this.shortName = name;
            return this;
        }

        public OptionBuilder<T> longName(String name) {
            this.longName = name;
            return this;
        }

        public OptionBuilder<T> defaultValue(T value) {
            this.defaultValue = value;
            return this;
        }

        public OptionBuilder<T> validator(Validator<T> v) {
            validators.add(v);
            return this;
        }

        public Option<T> build() {
            try {
                Option<T> o = cls.getConstructor(String.class, String.class, String.class).newInstance(description, shortName, longName);
                o.setDefaultValue(defaultValue);
                validators.stream().forEach(o::addValidator);
                addOption(o);
                return o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
