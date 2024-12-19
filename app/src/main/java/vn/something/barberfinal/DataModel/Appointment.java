package vn.something.barberfinal.DataModel;

import java.io.Serializable;

public class Appointment implements Serializable {
    private String referencePicture;
    private String date;
    private String note;
    private String time;
    //PENDING,UPCOMING,FINISHED,CANCELLED
    private String status;
    private String customerName;
    private String serviceId;
    private String messengerUserId;
    private String customerPhone;
    private String appointmentId;// For Firebase unique ID
    private String shopId;


    public Appointment() {
    }

    public Appointment(String shopId,String referencePicture, String date, String note, String time,
                       String customerName, String service,
                       String messengerUserId, String customerPhone) {
        this.referencePicture = referencePicture;
        this.date = date;
        this.note = note;
        this.time = time;
        this.status = "PENDING";
        this.customerName = customerName;
        this.serviceId = service;
        this.messengerUserId = messengerUserId;
        this.customerPhone = customerPhone;
        this.shopId = shopId;
    }

    // Getters and Setters
    public String getReferencePicture() {
        return referencePicture;
    }

    public void setReferencePicture(String referencePicture) {
        this.referencePicture = referencePicture;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMessengerUserId() {
        return messengerUserId;
    }

    public void setMessengerUserId(String messengerUserId) {
        this.messengerUserId = messengerUserId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
    public String getShortId(){
        if(this.appointmentId != null){
            return this.appointmentId.substring(0,4).toUpperCase();

        }
        return "noid?";
    }
}