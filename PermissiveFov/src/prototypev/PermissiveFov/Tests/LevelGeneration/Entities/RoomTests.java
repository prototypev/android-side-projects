package prototypev.PermissiveFov.Tests.LevelGeneration.Entities;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.SideType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RoomTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void newRoom_InvalidArguments_ExpectsException() {
        exception.expect(IllegalArgumentException.class);
        Room.createFilledRoom(-1, -1, 0, 0);
    }

    @Test
    public void isOutOfBounds_CellInRoom_ExpectsFalse() {
        Room room = Room.createFilledRoom(0, 0, 2, 2);

        assertFalse("(0, 0) should be inside room!", room.isOutOfBounds(0, 0));
        assertFalse("(1, 1) should be inside room!", room.isOutOfBounds(1, 1));
    }

    @Test
    public void isOutOfBounds_CellOutsideRoom_ExpectsTrue() {
        Room room = Room.createFilledRoom(1, 1, 2, 2);

        assertTrue("(0, 0) should not be inside room!", room.isOutOfBounds(0, 0));
        assertTrue("(3, 3) should not be inside room!", room.isOutOfBounds(3, 3));
    }

    @Test
    public void hasAdjacentCell_OutOfBounds_ExpectsException() {
        Room room = Room.createFilledRoom(0, 0, 1, 1);

        exception.expect(IllegalStateException.class);
        room.hasAdjacentCell(-1, -1, DirectionType.NORTH);
    }

    @Test
    public void hasAdjacentCell_CellBesideBoundary_ExpectTrue() {
        Room room = Room.createFilledRoom(0, 0, 3, 3);

        for (DirectionType direction : DirectionType.values()) {
            assertTrue(String.format("Cell (1, 1) should have an adjacent cell to the %s!", direction.getName()), room.hasAdjacentCell(1, 1, direction));
        }
    }

    @Test
    public void hasAdjacentCell_CellOnBoundary_ExpectsFalse() {
        Room room = Room.createFilledRoom(0, 0, 1, 1);
        for (DirectionType direction : DirectionType.values()) {
            assertFalse(String.format("Cell (0, 0) should not have an adjacent cell to the %s!", direction.getName()), room.hasAdjacentCell(0, 0, direction));
        }
    }

    @Test
    public void setCellSide_MakeNorthCorridor_ExpectsCorrectLayout() {
        Room room = Room.createFilledRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.NORTH, SideType.EMPTY);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Room after corridor is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     X
         *	0 |	    X.X
         *	  |	     O
         *    |
         *	  |	 X   O   X
         *	1 |	X.X X.X X.X
         *	  |	 X   X   X
         *    |
         *	  |	     X
         *	2 |	    X.X
         *	  |	     X
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.EMPTY);
        expectedSides.put(DirectionType.WEST, SideType.WALL);
        expectedSides.put(DirectionType.SOUTH, SideType.WALL);
        expectedSides.put(DirectionType.EAST, SideType.WALL);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.NORTH, SideType.WALL);
        assertSides(room, 0, 1, expectedSides);
        assertSides(room, 1, 2, expectedSides);
        assertSides(room, 2, 1, expectedSides);

        expectedSides.put(DirectionType.SOUTH, SideType.EMPTY);
        assertSides(room, 1, 0, expectedSides);
    }

    @Test
    public void setCellSide_MakeSouthCorridor_ExpectsCorrectLayout() {
        Room room = Room.createFilledRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.SOUTH, SideType.EMPTY);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after corridor is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     X
         *	0 |	    X.X
         *	  |	     X
         *    |
         *	  |	 X   X   X
         *	1 |	X.X X.X X.X
         *	  |	 X   O   X
         *    |
         *	  |	     O
         *	2 |	    X.X
         *	  |	     X
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.WALL);
        expectedSides.put(DirectionType.WEST, SideType.WALL);
        expectedSides.put(DirectionType.SOUTH, SideType.EMPTY);
        expectedSides.put(DirectionType.EAST, SideType.WALL);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.SOUTH, SideType.WALL);
        assertSides(room, 0, 1, expectedSides);
        assertSides(room, 1, 0, expectedSides);
        assertSides(room, 2, 1, expectedSides);

        expectedSides.put(DirectionType.NORTH, SideType.EMPTY);
        assertSides(room, 1, 2, expectedSides);
    }

    @Test
    public void setCellSide_MakeWestCorridor_ExpectsCorrectLayout() {
        Room room = Room.createFilledRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.WEST, SideType.EMPTY);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after corridor is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     X
         *	0 |	    X.X
         *	  |	     X
         *    |
         *	  |	 X   X   X
         *	1 |	X.O O.X X.X
         *	  |	 X   X   X
         *    |
         *	  |	     X
         *	2 |	    X.X
         *	  |	     X
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.WALL);
        expectedSides.put(DirectionType.WEST, SideType.EMPTY);
        expectedSides.put(DirectionType.SOUTH, SideType.WALL);
        expectedSides.put(DirectionType.EAST, SideType.WALL);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.WEST, SideType.WALL);
        assertSides(room, 1, 0, expectedSides);
        assertSides(room, 1, 2, expectedSides);
        assertSides(room, 2, 1, expectedSides);

        expectedSides.put(DirectionType.EAST, SideType.EMPTY);
        assertSides(room, 0, 1, expectedSides);
    }

    @Test
    public void setCellSide_MakeEastCorridor_ExpectsCorrectLayout() {
        Room room = Room.createFilledRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.EAST, SideType.EMPTY);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after corridor is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     X
         *	0 |	    X.X
         *	  |	     X
         *    |
         *	  |	 X   X   X
         *	1 |	X.X X.O O.X
         *	  |	 X   X   X
         *    |
         *	  |	     X
         *	2 |	    X.X
         *	  |	     X
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.WALL);
        expectedSides.put(DirectionType.WEST, SideType.WALL);
        expectedSides.put(DirectionType.SOUTH, SideType.WALL);
        expectedSides.put(DirectionType.EAST, SideType.EMPTY);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.EAST, SideType.WALL);
        assertSides(room, 1, 0, expectedSides);
        assertSides(room, 1, 2, expectedSides);
        assertSides(room, 0, 1, expectedSides);

        expectedSides.put(DirectionType.WEST, SideType.EMPTY);
        assertSides(room, 2, 1, expectedSides);
    }

    @Test
    public void setCellSide_MakeNorthWall_ExpectsCorrectLayout() {
        Room room = Room.createEmptyRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.NORTH, SideType.WALL);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after wall is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     O
         *	0 |	    O.O
         *	  |	     X
         *    |
         *	  |	 O   X   O
         *	1 |	O.O O.O O.O
         *	  |	 O   O   O
         *    |
         *	  |	     O
         *	2 |	    O.O
         *	  |	     O
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.WALL);
        expectedSides.put(DirectionType.WEST, SideType.EMPTY);
        expectedSides.put(DirectionType.SOUTH, SideType.EMPTY);
        expectedSides.put(DirectionType.EAST, SideType.EMPTY);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.NORTH, SideType.EMPTY);
        assertSides(room, 0, 1, expectedSides);
        assertSides(room, 1, 2, expectedSides);
        assertSides(room, 2, 1, expectedSides);

        expectedSides.put(DirectionType.SOUTH, SideType.WALL);
        assertSides(room, 1, 0, expectedSides);
    }

    @Test
    public void setCellSide_MakeSouthWall_ExpectsCorrectLayout() {
        Room room = Room.createEmptyRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.SOUTH, SideType.WALL);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after wall is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     O
         *	0 |	    O.O
         *	  |	     O
         *    |
         *	  |	 O   O   O
         *	1 |	O.O O.O O.O
         *	  |	 O   X   O
         *    |
         *	  |	     X
         *	2 |	    O.O
         *	  |	     O
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.EMPTY);
        expectedSides.put(DirectionType.WEST, SideType.EMPTY);
        expectedSides.put(DirectionType.SOUTH, SideType.WALL);
        expectedSides.put(DirectionType.EAST, SideType.EMPTY);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.SOUTH, SideType.EMPTY);
        assertSides(room, 0, 1, expectedSides);
        assertSides(room, 1, 0, expectedSides);
        assertSides(room, 2, 1, expectedSides);

        expectedSides.put(DirectionType.NORTH, SideType.WALL);
        assertSides(room, 1, 2, expectedSides);
    }

    @Test
    public void setCellSide_MakeWestWall_ExpectsCorrectLayout() {
        Room room = Room.createEmptyRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.WEST, SideType.WALL);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after wall is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     O
         *	0 |	    O.O
         *	  |	     O
         *    |
         *	  |	 O   O   O
         *	1 |	O.X X.O O.O
         *	  |	 O   O   O
         *    |
         *	  |	     O
         *	2 |	    O.O
         *	  |	     O
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.EMPTY);
        expectedSides.put(DirectionType.WEST, SideType.WALL);
        expectedSides.put(DirectionType.SOUTH, SideType.EMPTY);
        expectedSides.put(DirectionType.EAST, SideType.EMPTY);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.WEST, SideType.EMPTY);
        assertSides(room, 1, 2, expectedSides);
        assertSides(room, 1, 0, expectedSides);
        assertSides(room, 2, 1, expectedSides);

        expectedSides.put(DirectionType.EAST, SideType.WALL);
        assertSides(room, 0, 1, expectedSides);
    }

    @Test
    public void setCellSide_MakeEastWall_ExpectsCorrectLayout() {
        Room room = Room.createEmptyRoom(0, 0, 3, 3);
        room.setCellSide(1, 1, DirectionType.EAST, SideType.WALL);

        /*
         * Legend:
         * X - SIDE_WALL
         * O - SIDE_EMPTY
         * . - Centre of cell
         *
         * Test grid after wall is made should look like this:
         *
         *       0   1   2
         *    --------------
         *	  |	     O
         *	0 |	    O.O
         *	  |	     O
         *    |
         *	  |	 O   O   O
         *	1 |	O.O O.X X.O
         *	  |	 O   O   O
         *    |
         *	  |	     O
         *	2 |	    O.O
         *	  |	     O
         */

        Map<DirectionType, SideType> expectedSides = new HashMap<DirectionType, SideType>();

        expectedSides.put(DirectionType.NORTH, SideType.EMPTY);
        expectedSides.put(DirectionType.WEST, SideType.EMPTY);
        expectedSides.put(DirectionType.SOUTH, SideType.EMPTY);
        expectedSides.put(DirectionType.EAST, SideType.WALL);
        assertSides(room, 1, 1, expectedSides);

        expectedSides.put(DirectionType.EAST, SideType.EMPTY);
        assertSides(room, 1, 2, expectedSides);
        assertSides(room, 1, 0, expectedSides);
        assertSides(room, 0, 1, expectedSides);

        expectedSides.put(DirectionType.WEST, SideType.WALL);
        assertSides(room, 2, 1, expectedSides);
    }

    /**
     * Asserts that all sides of the cell at the specified co-ordinates in the room matches the expectation.
     *
     * @param room          The containing room.
     * @param x             The horizontal component.
     * @param y             The vertical component.
     * @param expectedSides The expected sides.
     */
    private void assertSides(Room room, int x, int y, Map<DirectionType, SideType> expectedSides) {
        Cell cell = room.getCellAt(x, y);

        for (Map.Entry<DirectionType, SideType> entry : expectedSides.entrySet()) {
            DirectionType direction = entry.getKey();
            SideType expectedSide = entry.getValue();
            SideType actualSide = cell.getSide(direction);
            assertEquals(String.format("%s side of (%d, %d) should be %s!", direction.getName(), x, y, expectedSide.getName()), expectedSide, actualSide);
        }
    }
}
