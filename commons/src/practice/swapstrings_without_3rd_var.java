package practice;

/**
 * Created by Admin on 2/17/2021.
 */
public class swapstrings_without_3rd_var
{
public static void main(String[] args)
{
    String var1="virat";
    String var2="kohli";
    System.out.println("Before swap");
     System.out.println("Name is "+var1);
    System.out.println("SurName is "+var2);

    var1=var1+var2;                                                            //append var1 & var2
    var2=var1.substring(0,var1.length()-var2.length());                         //store initial string var1 in var2
    var1=var1.substring(var2.length());                                        //store initial string var2 in var1
    System.out.println("After swap");
    System.out.println("Name is "+var1);
    System.out.println("SurName is "+var2);


}
}
