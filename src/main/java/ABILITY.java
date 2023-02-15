public enum ABILITY {
    MID_GLITCH(1003427),
    REMOTE_GLITCH(1003428),
    OVERFLOW_CODE_SMELL(1003437),
    UNDERFLOW_CODE_SMELL(1003438),
    PERFORMANCE_CODE_SMELL(1003439);

    private final int ID;

    ABILITY(final int newID){
        ID = newID;
    }

    public int ID() { return ID; }
}
