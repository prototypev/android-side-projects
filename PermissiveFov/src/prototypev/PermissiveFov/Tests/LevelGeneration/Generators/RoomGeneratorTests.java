package prototypev.PermissiveFov.Tests.LevelGeneration.Generators;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.Generators.MazeGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.RoomGenerator;
import prototypev.PermissiveFov.Randomizer;
import prototypev.PermissiveFov.Tests.TestBase;

import java.util.List;

import static org.junit.Assert.*;
import static prototypev.PermissiveFov.Tests.TestHelper.createCorridorInAllDirections;

public class RoomGeneratorTests extends TestBase {
    private int left;
    private int top;

    @Test
    public void createRooms_NormalCase_ExpectsValidRoomDimensions() {
        int minWidth = 2;
        int maxWidth = 5;
        int minHeight = 2;
        int maxHeight = 5;
        int numRooms = 5;

        MazeGenerator mazeGenerator = new MazeGenerator(30, 70);
        Room container = mazeGenerator.generate(0, 0, 15, 15);

        RoomGenerator roomGenerator = new RoomGenerator(minWidth, maxWidth, minHeight, maxHeight);
        roomGenerator.createRooms(container, numRooms);

        List<Room> rooms = container.getRooms();
        assertEquals("Number of rooms placed do not match!", numRooms, rooms.size());

        for (Room room : rooms) {
            assertThat("Invalid room width generated!", room.width, Matchers.greaterThanOrEqualTo(minWidth));
            assertThat("Invalid room width generated!", room.width, Matchers.lessThanOrEqualTo(maxWidth));

            assertThat("Invalid room height generated!", room.height, Matchers.greaterThanOrEqualTo(minHeight));
            assertThat("Invalid room height generated!", room.height, Matchers.lessThanOrEqualTo(maxHeight));

            int roomX = room.getLeft();
            int roomY = room.getTop();
            assertFalse("The room should be inside the container!", container.isOutOfBounds(roomX, roomY));
            assertFalse("The room should be inside the container!", container.isOutOfBounds(roomX + room.width - 1, roomY + room.height - 1));
        }

        System.out.println(container);
    }

    @Test
    public void getRoomPlacementScore_NormalCase_ExpectCorrectScore() {
        Room container = Room.createFilledRoom(top, left, 3, 3);

        createCorridorInAllDirections(container, left + 1, top + 1);

        /*
         * The scores for the cells in the container are as follows:
         * (0, 0):
         * +1 for adjacent corridor cell at (1, 0)
         * +1 for adjacent corridor cell at (0, 1)
         * Total: 2
         *
         * (0, 1):
         * +3 for being a corridor itself
         * +1 for adjacent corridor cell at (1, 1)
         * Total: 4
         *
         * (1, 0):
         * +3 for being a corridor itself
         * +1 for adjacent corridor cell at (1, 1)
         * Total: 4
         *
         * (1, 1):
         * +3 for being a corridor itself
         * +1 for adjacent corridor cell at (1, 0)
         * +1 for adjacent corridor cell at (0, 1)
         * +1 for adjacent corridor cell at (2, 1)
         * +1 for adjacent corridor cell at (1, 2)
         * Total: 7
         *
         * Our placement score should be 17
         */

        Room room = Room.createFilledRoom(top, left, 2, 2);

        int score = RoomGenerator.getRoomPlacementScore(container, room, left, top);
        assertEquals("Room placement score is incorrect!", 17, score);
    }

    @Before
    public void testSetup() {
        top = Randomizer.getInstance().nextInt(2, 10);
        left = Randomizer.getInstance().nextInt(2, 10);
    }
}
