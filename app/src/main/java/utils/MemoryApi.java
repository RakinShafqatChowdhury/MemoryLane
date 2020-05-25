package utils;

import android.app.Application;

public class MemoryApi extends Application {
    private String username;
    private String userid;

    private static MemoryApi instace;

    public static MemoryApi getInstace(){
        if(instace==null)
            instace=new MemoryApi();

        return instace;

    }

    public MemoryApi() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
