package io.ruck.clop;

/**
 *
 * @author ruckc
 */
public class BooleanOption extends AbstractOption<Boolean> {

    public BooleanOption(String description, String shortName, String longName) {
        super(Boolean.class, OptionType.BOOLEAN, description, shortName, longName);
        setDefaultValue(Boolean.FALSE);
    }

    @Override
    public Boolean parseValue(String value) {
        return Boolean.parseBoolean(value);
    }

}
