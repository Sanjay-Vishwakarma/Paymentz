package practice;

import java.util.Scanner;

/**
 * Created by Admin on 10/4/2020.
 */
public class Add_two_numbers_without_using_plus_operator
{
    public static void main(String[] args)
    {
        int num1;
        int num2;
        int add;
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter two numbers ");
        num1=sc.nextInt();
        num2=sc.nextInt();
        add=num1-(-num2);
        System.out.println(add);
    }
}
