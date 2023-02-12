package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 3/24/15
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FileActionType
{
    VALIDATION("validation"),
    UPLOAD("upload"),
    REPLACE("replace"),
    RENAME("rename"),
    MOVE("move"),
    DELETE("delete"),
    VIEW("view"),
    EXCEPTION("exception"),
    GENERATE("generate");//this is for any exception occurred


    private String fileAction;

    FileActionType(String fileAction)
    {
        this.fileAction=fileAction;
    }

    public String toString()
    {
        return fileAction;
    }
}
