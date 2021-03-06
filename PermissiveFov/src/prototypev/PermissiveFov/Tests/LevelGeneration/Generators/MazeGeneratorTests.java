package prototypev.PermissiveFov.Tests.LevelGeneration.Generators;

import org.junit.Before;
import org.junit.Test;
import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.Generators.MazeGenerator;
import prototypev.PermissiveFov.Randomizer;
import prototypev.PermissiveFov.Tests.TestBase;

import java.text.MessageFormat;
import java.util.List;

import static org.junit.Assert.*;

public class MazeGeneratorTests extends TestBase {
    private int left;
    private int top;

    @Test
    public void generate_VarySparseness_ExpectsMatchingPercentCellsAsSolidRock() {
        for (int sparseness = 0; sparseness <= 100; sparseness++) {
            Room room = generateRoom(50, sparseness);

            int currentRockCellCount = 0;

            for (int y = 0; y < room.height; y++) {
                for (int x = 0; x < room.width; x++) {
                    Cell cell = room.getCellAt(left + x, top + y);

                    if (cell.getWallCount() == DirectionType.size) {
                        currentRockCellCount++;
                    }
                }
            }

            int targetRockCellCount = (int) Math.ceil(sparseness * room.width * room.height / 100f);
            assertEquals(MessageFormat.format("In a {0}% sparse map, {0}% of the cells should have all sides as walls!", sparseness), targetRockCellCount, currentRockCellCount);
        }
    }

    @Test
    public void newMazeGenerator_InvalidArguments_ExpectsException() {
        exception.expect(IllegalArgumentException.class);
        new MazeGenerator(-1, -1);
    }

    @Test
    public void removeDeadEnds_WithMaxModifier_ExpectsAllDeadEndsRemoved() {
        MazeGenerator generator = new MazeGenerator(30, 70);

        Room room = generator.generate(top, left, 15, 15);
        assertTrue("All cells should be visited after generating the maze!", room.isAllCellsVisited());

        List<Cell> deadEndCells = room.getDeadEndCells();
        assertFalse("No dead ends generated!", deadEndCells.isEmpty());

        // Remove the dead-ends with a deadEndRemovalModifier of 100
        generator.removeDeadEnds(room, 100);

        // We expect the map to have no dead ends when we’re done
        deadEndCells = room.getDeadEndCells();
        assertTrue("All dead ends should have been removed!", deadEndCells.isEmpty());
    }

    @Before
    public void testSetup() {
        top = Randomizer.getInstance().nextInt(2, 10);
        left = Randomizer.getInstance().nextInt(2, 10);
    }

    /**
     * @param randomness A value between 0 - 100 indicating the degree of randomness.
     * @param sparseness A value between 0 - 100 indicating the degree of sparseness.
     * @return The generated room.
     */
    private Room generateRoom(int randomness, int sparseness) {
        MazeGenerator generator = new MazeGenerator(randomness, sparseness);

        Room room = generator.generate(top, left, 15, 15);
        assertTrue("All cells should be visited after generating the maze!", room.isAllCellsVisited());

        System.out.print("randomness=");
        System.out.println(randomness);
        System.out.print("sparseness=");
        System.out.println(sparseness);
        System.out.println(room);

        return room;
    }
}
