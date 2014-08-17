package prototypev.PermissiveFov.Tests.LevelGeneration.Generators;

import org.junit.Test;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.Generators.LevelGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.MazeGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.RoomGenerator;
import prototypev.PermissiveFov.Tests.TestBase;

import static org.junit.Assert.assertEquals;

public class LevelGeneratorTests extends TestBase {
    @Test
    public void generate_NormalCase_ExpectsCorrectLayout() {
        MazeGenerator mazeGenerator = new MazeGenerator(30, 70);
        RoomGenerator roomGenerator = new RoomGenerator(2, 3, 2, 3);

        Level level = LevelGenerator.generate(15, 15, mazeGenerator, roomGenerator, 5);
        assertEquals("Number of tiles generated is incorrect!", 15 * 2 + 1, level.width);
        assertEquals("Number of tiles generated is incorrect!", 15 * 2 + 1, level.height);
    }
}
