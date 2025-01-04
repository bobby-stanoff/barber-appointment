package vn.something.barberfinal.DataModel;

import java.util.HashMap;
import java.util.Map;
public class ScheduleData {
    private Map<String,Appointment> data;
    public ScheduleData(){
        data = new HashMap<>();
    }
    //date: dd/mm, time: 09:00, 10:00,...
    public void addData(String date, String time, Appointment appointment){
        String key = date + "#" + time;
        if(!data.containsKey(key)){
            data.put(key, appointment);
        }

    }

    public Appointment getAppointment(String date, String time){
        String key = date + "#" + time;
        if(data.containsKey(key)){
            return data.get(key);
        } else {
            return null;
        }
    }

}