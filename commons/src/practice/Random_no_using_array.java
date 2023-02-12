package practice;

import java.util.Random;

/**
 * Created by Admin on 3/3/2021.
 */
public class Random_no_using_array
{
    public static void main(String[] args)
    {
        Random random = new Random();
        int a[]= new int[1];
        int i;
        for(i=0;i<a.length;i++)
        {
            a[i]=random.nextInt(20);
        }
        for(int var : a)
        {
            System.out.println(var + " ");
        }
    }
}
