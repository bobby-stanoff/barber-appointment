package vn.something.barberfinal.DataModel;

import java.io.Serializable;

public class BarberService implements Serializable {
    private String name;
    private String duration;
    private String price;

    public BarberService(String name, String duration, String price) {
        this.name = name;
        this.duration = duration;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
