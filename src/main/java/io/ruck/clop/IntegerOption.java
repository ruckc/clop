package io.ruck.clop;

/**
 *
 * @author ruckc
 */
public class IntegerOption extends AbstractOption<Integer> {

    public IntegerOption(String description, String shortName, String longName) {
        super(Integer.class, OptionType.SINGLE_VALUE, description, shortName, longName);
    }

    @Override
    public Integer parseValue(String value) {
        return Integer.parseInt(value);
    }
}
