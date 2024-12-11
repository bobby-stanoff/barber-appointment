package vn.something.barberfinal.DataModel;

import java.io.Serializable;
import java.util.ArrayList;

public class BarberShop implements Serializable {
    String name,
            mail,
            avatar_image,
            website,
            address,
            phoneNumber,
            pageId,
            pageAccessToken,
            firebaseUserUid;
    int follower_counts;
    String shopId;//firebaseshopid
    ArrayList<Appointment> appointments;
    ArrayList<BarberService> barberServices;

    public BarberShop(){

    }
    public BarberShop(String name, String mail, String avatar_image, String website, String address, String phoneNumber, int follower_count, String pageId, String pageAccessToken, String firebaseUserUid) {
        this.name = name;
        this.mail = mail != null ? mail : "examplemailll@gmail.com" ;
        this.avatar_image = avatar_image;
        this.website = website;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.pageId = pageId;
        this.pageAccessToken = pageAccessToken;
        this.firebaseUserUid = firebaseUserUid;
        this.follower_counts = follower_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatar_image() {
        return avatar_image;
    }

    public void setAvatar_image(String avatar_image) {
        this.avatar_image = avatar_image;
    }


    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageAccessToken() {
        return pageAccessToken;
    }

    public void setPageAccessToken(String pageAccessToken) {
        this.pageAccessToken = pageAccessToken;
    }

    public String getFirebaseUserUid() {
        return firebaseUserUid;
    }

    public void setFirebaseUserUid(String firebaseUserUid) {
        this.firebaseUserUid = firebaseUserUid;
    }

    public int getFollower_counts() {
        return follower_counts;
    }

    public void setFollower_counts(int follower_counts) {
        this.follower_counts = follower_counts;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

    public ArrayList<BarberService> getBarberServices() {
        return barberServices;
    }

    public void setBarberServices(ArrayList<BarberService> barberServices) {
        this.barberServices = barberServices;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
