package io.ruck.clop;

/**
 *
 * @author ruckc
 */
public class StringOption extends AbstractOption<String> {

    public StringOption(String description, String shortName, String longName) {
        super(String.class, OptionType.SINGLE_VALUE, description, shortName, longName);
    }

    @Override
    public String parseValue(String value) {
        return value;
    }
}
