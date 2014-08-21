package prototypev.PermissiveFov.Tests.LevelGeneration.Entities;

import org.junit.Test;
import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.SideType;
import prototypev.PermissiveFov.Tests.TestBase;

import java.text.MessageFormat;

import static org.junit.Assert.*;

public class CellTests extends TestBase {
    @Test
    public void getDeadEndCorridorDirection_DefaultCase_ExpectsCorrectValues() {
        for (DirectionType direction : DirectionType.values()) {
            Cell cell = new Cell();
            cell.setSide(direction, SideType.EMPTY);
            assertEquals(MessageFormat.format("Dead end cell with {0} side empty should return {0}!", direction.getName()), direction, cell.getDeadEndCorridorDirection());
        }
    }

    @Test
    public void getDeadEndCorridorDirection_NonDeadEnd_ExpectsException() {
        Cell cell = new Cell();

        exception.expect(IllegalStateException.class);
        cell.getDeadEndCorridorDirection();
    }

    @Test
    public void isDeadEnd_NotOneSideEmpty_ExpectsFalse() {
        Cell cell = new Cell();
        assertFalse("A brand new cell should not be a dead end!", cell.isDeadEnd());

        cell.setSide(DirectionType.NORTH, SideType.EMPTY);
        cell.setSide(DirectionType.EAST, SideType.EMPTY);
        assertFalse("A cell with 2 empty sides should not be a dead end!", cell.isDeadEnd());

        cell.setSide(DirectionType.WEST, SideType.EMPTY);
        assertFalse("A cell with 3 empty sides should not be a dead end!", cell.isDeadEnd());

        cell.setSide(DirectionType.SOUTH, SideType.EMPTY);
        assertFalse("A cell with all empty sides should not be a dead end!", cell.isDeadEnd());
    }

    @Test
    public void isDeadEnd_OneSideEmpty_ExpectsTrue() {
        for (DirectionType direction : DirectionType.values()) {
            Cell cell = new Cell();
            cell.setSide(direction, SideType.EMPTY);
            assertTrue("Cell with only one side empty should be a dead end!", cell.isDeadEnd());
        }
    }

    @Test
    public void newCell_InvalidArguments_ExpectsException() {
        exception.expect(IllegalArgumentException.class);
        new Cell(-1, -1);
    }
}
