package prototypev.PermissiveFov.LevelGeneration;

public enum DirectionType {
    NORTH(0, "North"), WEST(1, "West"), SOUTH(2, "South"), EAST(3, "East");

    public static final int size = DirectionType.values().length;

    private final int value;
    private final String name;

    DirectionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public DirectionType getOpposite() {
        switch (this) {
            case NORTH:
                return SOUTH;

            case SOUTH:
                return NORTH;

            case EAST:
                return WEST;

            case WEST:
                return EAST;

            default:
                // Should never happen
                throw new IllegalStateException(String.format("%s has no opposite.", name));
        }
    }
}
