package prototypev.PermissiveFov.LevelGeneration;

public enum SideType {
    WALL(0), EMPTY(1), DOOR(2);

    private final int value;

    private SideType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}