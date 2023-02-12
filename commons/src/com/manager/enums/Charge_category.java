package com.manager.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Charge_category
{
    Success("Success"),
    Failure("Failure"),
    Others("Others");

    private String category;

    Charge_category(String category)
    {
        this.category=category;
    }

    public String toString()
    {
        return category;
    }
}
