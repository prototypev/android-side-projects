package prototypev.PermissiveFov.LevelGeneration;

public enum SideType {
    WALL(0, "Wall", '#'), EMPTY(1, "Empty", ' '), DOOR(2, "Door", '+');

    private final int value;
    private final String name;
    private final char symbol;

    private SideType(int value, String name, char symbol) {
        this.value = value;
        this.name = name;
        this.symbol = symbol;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public TileType toTileType() {
        for (TileType tileType : TileType.values()) {
            if (tileType.getValue() == value) {
                return tileType;
            }
        }

        throw new IllegalStateException(String.format("Cannot find matching TileType for SideType: %s", name));
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}