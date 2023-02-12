package com.directi.pg;

public class SystemError extends Exception
{
    public SystemError()
    {
        super(":1000:");
    }

    public SystemError(String msg)
    {
        super(msg);
    }
}




    
