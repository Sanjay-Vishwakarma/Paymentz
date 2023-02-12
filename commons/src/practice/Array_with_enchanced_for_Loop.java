package practice;

/**
 * Created by Admin on 3/3/2021.
 */
public class Array_with_enchanced_for_Loop
{
    public static void main(String[] args)
    {
        int a[]=new int[5];
        a[0]=10;
        a[1]=20;
        a[2]=30;
        a[3]=40;
        a[4]=50;
        for(int i=0;i<a.length;i++)
        {
            System.out.println(a[i]);
        }

        System.out.println("Array + 1");
        for(int var:a)
        {
            System.out.println(var+1);
        }


    }
}
