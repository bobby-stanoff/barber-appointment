package vn.something.barberfinal.DataModel;

import java.util.Calendar;
import java.util.Date;

public class BarberUser {
    String PSID;
    //NONE,DAY3,FOREVER
    String blockType;
    int numberOfReservation;
    Date expiredBlockDate;

    public BarberUser(){

    }
    public BarberUser(String PSID) {
        this.PSID = PSID;
        this.blockType = "NONE";
        this.numberOfReservation = 1;
        Calendar calendarde = Calendar.getInstance();
        calendarde.set(Calendar.YEAR, 2000);
        Date expireddate = calendarde.getTime();
        this.expiredBlockDate = expireddate;
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

    public Date getExpiredBlockDate() {
        return expiredBlockDate;
    }

    public void setExpiredBlockDate(Date expiredBlockDate) {
        this.expiredBlockDate = expiredBlockDate;
    }
}
