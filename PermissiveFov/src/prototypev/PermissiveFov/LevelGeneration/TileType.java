package prototypev.PermissiveFov.LevelGeneration;

public enum TileType {
    UNDEFINED(-1, "", ' '), WALL(0, "TILE_WALL", '#'), EMPTY(1, "TILE_EMPTY", '.'), DOOR(2, "TILE_DOOR", '+');
    private final String name;
    private final char symbol;
    private final int value;

    private TileType(int value, String name, char symbol) {
        this.value = value;
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}