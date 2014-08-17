package prototypev.PermissiveFov.LevelGeneration;

public enum TileType {
    UNDEFINED(-1, "", ' '), WALL(0, "TILE_WALL", '#'), EMPTY(1, "TILE_EMPTY", '.'), DOOR(2, "TILE_DOOR", '+');

    private final int value;
    private final String name;
    private final char symbol;

    private TileType(int value, String name, char symbol) {
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

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}