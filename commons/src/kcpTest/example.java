package kcpTest;

import practice.p1;

/**
 * Created by Admin on 2/15/2020.
 */
public class example
{
    public p1 method()
    {


    p1 o = new p1();

        o.setaddress("mumbai");
        o.setgrade('A');

        o.setname("sagar");
        o.setrollno(70);
        o.setpercentage(82.3f);

        return o;
}

    public static void main(String[] args)
    {
        example obj = new example();
        p1 o=obj.method();
        System.out.println(o.getaddress());
        System.out.println(o.getrollno());
        System.out.println(o.getpercentage());
        System.out.println(o.getname());
        System.out.println(o.getgrade());
    }
}
