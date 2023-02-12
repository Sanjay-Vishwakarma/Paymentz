package practice;

import java.util.Scanner;

/**
 * Created by Admin on 10/4/2020.
 */
public class Numberswithoutloops
{
    static int count=1;
    static void printnumbers(int n)
    {
        System.out.println(count);
        count++;
        if(count==n+1)
            return;
        printnumbers(n);
    }
    public static void main(String[] args)
    {
int num;
        Scanner sc= new Scanner(System.in);
        System.out.println("enter any number");
        num=sc.nextInt();
        printnumbers(num);
    }
}
