package com.example.anonymous.campussocialportal;

/**
 * Created by Anonymous on 3/2/2018.
 */

public   class getid {
    private String userId;
    private String convert;
    private int Error;
    getid(String userId, int Error){
        this.userId=userId;
        this.Error=Error;


    }
    public String  show(){
        convert = String.valueOf(Error);
        if(convert.equals("404"))
        {
            return convert;
        }
        else
        return userId;

    }

}
