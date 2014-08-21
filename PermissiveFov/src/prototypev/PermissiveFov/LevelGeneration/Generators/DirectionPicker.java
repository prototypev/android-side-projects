package prototypev.PermissiveFov.LevelGeneration.Generators;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.Randomizer;

import java.util.HashSet;
import java.util.Set;

/**
 * The DirectionPicker is responsible for picking valid directions.
 */
public class DirectionPicker {
    private final Set<DirectionType> directionsPicked = new HashSet<DirectionType>();
    private final int randomness;
    private DirectionType previousDirection;

    /**
     * Creates a new DirectionPicker.
     *
     * @param initialDirection The initial direction.
     * @param randomness       A value between 0 - 100 indicating the degree of randomness.
     */
    public DirectionPicker(DirectionType initialDirection, int randomness) {
        if (randomness < 0 || randomness > 100) {
            throw new IllegalArgumentException("randomness must be between 0 and 100!");
        }

        previousDirection = initialDirection;
        this.randomness = randomness;
    }

    /**
     * Creates a new DirectionPicker, starting off with a random direction.
     *
     * @param randomness A value between 0 - 100 indicating the degree of randomness.
     */
    public DirectionPicker(int randomness) {
        this(getRandomDirection(), randomness);
    }

    public DirectionPicker() {
        this(Randomizer.getInstance().nextInt(0, 100));
    }

    /**
     * @return The next direction available.
     */
    public DirectionType getNextDirection() {
        if (!hasNextDirection()) {
            throw new IllegalStateException("All directions have been exhausted.");
        }

        DirectionType newDirection;
        do {
            boolean mustChangeDirection = mustChangeDirection();
            newDirection = mustChangeDirection ? pickDifferentDirection() : previousDirection;
        } while (directionsPicked.contains(newDirection));

        directionsPicked.add(newDirection);

        return newDirection;
    }

    /**
     * @return true if there is a next direction to pick; otherwise false.
     */
    public boolean hasNextDirection() {
        return directionsPicked.size() < DirectionType.size;
    }

    /**
     * Resets the state of the DirectionPicker.
     *
     * @param initialDirection The initial direction.
     */
    public void reset(DirectionType initialDirection) {
        previousDirection = initialDirection;

        directionsPicked.clear();
    }

    /**
     * @return A random direction.
     */
    private static DirectionType getRandomDirection() {
        return Randomizer.getInstance().getRandomEnum(DirectionType.class);
    }

    /**
     * @return true if the direction must be changed; otherwise false.
     */
    private boolean mustChangeDirection() {
        // If randomness is 0, the corridors go straight until they run into a wall or another corridor.
        // If the randomness is 100, direction change is completely random.

        // If we have already picked a direction, we should always change the direction
        return directionsPicked.size() > 0 || randomness > Randomizer.getInstance().nextInt(100);
    }

    /**
     * @return A direction that is different from the previous direction, if possible.
     */
    private DirectionType pickDifferentDirection() {
        DirectionType newDirection;

        do {
            newDirection = getRandomDirection();
        } while (newDirection == previousDirection && directionsPicked.size() < DirectionType.size - 1);

        return newDirection;
    }
}
