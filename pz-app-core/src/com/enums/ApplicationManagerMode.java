package com.enums;

/**
 * Created by NIKET on 25-05-2016.
 */
public enum ApplicationManagerMode
{
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    MODIFY("MODIFY");

    private String mode;

    ApplicationManagerMode(String mode)
    {
        this.mode=mode;
    }

    public String toString()
    {
        return mode;
    }

    public static /*<E extends Enum<E>>*/ boolean isInEnum(String value)
    {
        try
        {
            ApplicationManagerMode.valueOf(value);
            return true;
        } catch (IllegalArgumentException iae)
        {
            return false;
        }
    }

}
