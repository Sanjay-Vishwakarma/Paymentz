package practice;

import java.util.Scanner;

/**
 * Created by Admin on 7/21/2020.
 */
public class demo1
{
    public static void main(String[] args)
    {
        commonprac_VO commonprac_voobj = new commonprac_VO();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Firstname");
        String firstname=sc.next();
        commonprac_voobj.setFirstname(firstname);
        if(firstname.equals(null)||firstname.isEmpty())
        {
            firstname="--";
        }

        System.out.println("Enter Education");
        String education=sc.next();
        commonprac_voobj.setEducation(education);
        if(education.equals(null)||education.isEmpty())
        {
            education="--";
        }

        System.out.println("Enter number");
        int number =sc.nextInt();
        commonprac_voobj.setNumber(number);



        System.out.println(commonprac_voobj.getFirstname());
        System.out.println(commonprac_voobj.getEducation());
        System.out.println(commonprac_voobj.getNumber());
    }
}

