package prototypev.PermissiveFov;

import java.security.SecureRandom;

public class Randomizer extends SecureRandom {
    private static Randomizer instance;

    private Randomizer() {
    }

    public static Randomizer getInstance() {
        if (instance == null) {
            instance = new Randomizer();
        }

        return instance;
    }

    /**
     * @param clazz   The class of the enum.
     * @param <TEnum> The type of the enum.
     * @return A pseudo-random uniformly distributed enum value of the specified type.
     */
    public <TEnum extends Enum<?>> TEnum getRandomEnum(Class<TEnum> clazz) {
        int x = nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**
     * @param minimum The lower bounds of the range.
     * @param maximum The upper bounds of the range.
     * @return A pseudo-random uniformly distributed {@code int} in the closed range [minimum, maximum].
     */
    public int nextInt(int minimum, int maximum) {
        int range = maximum - minimum + 1;
        return nextInt(range) + minimum;
    }
}
