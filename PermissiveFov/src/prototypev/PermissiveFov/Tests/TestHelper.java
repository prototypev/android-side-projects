package prototypev.PermissiveFov.Tests;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.SideType;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Helper class for unit tests.
 */
public class TestHelper {
    private TestHelper() {
    }

    /**
     * Asserts that all sides of the cell at the specified co-ordinates in the room matches the expectation.
     *
     * @param room          The containing room.
     * @param x             The horizontal component.
     * @param y             The vertical component.
     * @param expectedSides The expected sides.
     */
    public static void assertSides(Room room, int x, int y, Map<DirectionType, SideType> expectedSides) {
        Cell cell = room.getCellAt(x, y);

        for (Map.Entry<DirectionType, SideType> entry : expectedSides.entrySet()) {
            DirectionType direction = entry.getKey();
            SideType expectedSide = entry.getValue();
            SideType actualSide = cell.getSide(direction);
            assertEquals(String.format("%s side of (%d, %d) should be %s!", direction.getName(), x, y, expectedSide.getName()), expectedSide, actualSide);
        }
    }

    /**
     * Creates a corridor in all directions in the specified room at the specified co-ordinates.
     *
     * @param room The room.
     * @param x    The horizontal component.
     * @param y    The vertical component.
     */
    public static void createCorridorInAllDirections(Room room, int x, int y) {
        for (DirectionType direction : DirectionType.values()) {
            room.setCellSide(x, y, direction, SideType.EMPTY);
        }

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after corridors are made should look like this:
         *
         *		 x-1  x  x+1
         *     --------------
         *	   |  X   X   X
         * y-1 | X.X X.X X.X
         *     |  X   O   X
         *     |
         *	   |  X   O   X
         *	y  | X.O O.O O.X
         *	   |  X   O   X
         *     |
         *	   |  X   O   X
         * y+1 | X.X X.X X.X
         *	   |  X   X   X
         */
    }
}
