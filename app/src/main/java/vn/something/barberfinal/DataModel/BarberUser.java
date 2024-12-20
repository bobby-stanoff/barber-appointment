package vn.something.barberfinal.DataModel;

public class BarberUser {
    String PSID;
    //NONE,DAY3,FOREVER
    String blockType;
    int numberOfReservation;

    public BarberUser(String PSID) {
        this.PSID = PSID;
        this.blockType = "NONE";
        this.numberOfReservation = 1;
    }

    public String getPSID() {
        return PSID;
    }

    public void setPSID(String PSID) {
        this.PSID = PSID;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public int getNumberOfReservation() {
        return numberOfReservation;
    }

    public void setNumberOfReservation(int numberOfReservation) {
        this.numberOfReservation = numberOfReservation;
    }
}
