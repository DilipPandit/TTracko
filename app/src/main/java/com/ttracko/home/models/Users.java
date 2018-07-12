package com.ttracko.home.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 5/7/18.
 */


public class Users {

    public String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String Mobile;

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String GroupName;

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
        result.put("GroupName", GroupName);
        return result;
    }

}
