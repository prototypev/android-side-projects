package prototypev.PermissiveFov.LevelGeneration;

public enum SideType {
    WALL(0, "Wall"), EMPTY(1, "Empty"), DOOR(2, "Door");

    private final int value;
    private final String name;

    private SideType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}