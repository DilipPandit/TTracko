package com.ttracko.home.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 5/7/18.
 */


public class Users {

    public String Name;
    public String Mobile;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Users(String Name, String Mobile) {
        this.Name = Name;
        this.Mobile = Mobile;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name", Name);
        result.put("Mobile", Mobile);
        return result;
    }

}
