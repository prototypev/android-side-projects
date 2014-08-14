package prototypev.PermissiveFov.Tests.LevelGeneration.Generators;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Generators.DirectionPicker;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DirectionPickerTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void GetNextDirection_ExhaustAllDirections_ExpectsException() {
        DirectionPicker directionPicker = new DirectionPicker();

        exception.expect(IllegalStateException.class);
        for (int i = 0; i <= DirectionType.size; i++) {
            directionPicker.getNextDirection();
        }
    }

    @Test
    public void GetNextDirection_DefaultCase_ExpectsCorrectValues() {
        DirectionPicker directionPicker = new DirectionPicker();

        Set<DirectionType> directionsPicked = new HashSet<DirectionType>();

        for (int i = 0; i < DirectionType.size; i++) {
            assertTrue("Should have next direction!", directionPicker.hasNextDirection());

            DirectionType direction = directionPicker.getNextDirection();
            assertFalse(String.format("Direction %s should not have been picked yet!", direction.getName()), directionsPicked.contains(direction));

            directionsPicked.add(direction);
        }
    }

    @Test
    public void GetNextDirection_Randomness0_ExpectsSameDirectionInitially() {
        DirectionPicker directionPicker = new DirectionPicker(DirectionType.WEST, 0);

        // Ensure that the first direction picked is always the same as the previous direction
        DirectionType nextDirection = directionPicker.getNextDirection();
        assertEquals("First direction picked should be the same if randomness is 0!", DirectionType.WEST, nextDirection);

        // Ensure that all directions thereafter are different from previousDirection
        for (int i = 0; i < DirectionType.size - 1; i++) {
            nextDirection = directionPicker.getNextDirection();
            assertNotEquals("Subsequent directions picked should be different if randomness is 0!", DirectionType.WEST, nextDirection);
        }
    }

    @Test
    public void GetNextDirection_Randomness100_ExpectsDifferentDirectionInitially() {
        DirectionPicker directionPicker = new DirectionPicker(DirectionType.WEST, 100);

        DirectionType nextDirection;

        // Ensure that the first directions picked are different from previousDirection
        for (int i = 0; i < DirectionType.size - 1; i++) {
            nextDirection = directionPicker.getNextDirection();
            assertNotEquals("First directions picked should be different if randomness is 100!", DirectionType.WEST, nextDirection);
        }

        // Ensure that the first direction picked is always the same as the previous direction
        nextDirection = directionPicker.getNextDirection();
        assertEquals("Last direction picked should be the same if randomness is 100!", DirectionType.WEST, nextDirection);

    }
}
