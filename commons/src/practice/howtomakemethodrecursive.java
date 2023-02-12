package practice;

/**
 * Created by Admin on 11/17/2020.
 */
public class howtomakemethodrecursive
{
    public  static void sayHi(int n)
    {
     if(n==0)
     {
         System.out.println("Done");
     }
        else
     {
         System.out.println("hi");
         n--;
         sayHi(n);
     }
    }
    public static void main(String[] args)
    {

    sayHi(10);
    }
}
