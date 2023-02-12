package practice;

/**
 * Created by Admin on 2/17/2021.
 */
public class Armstrong
{
    public void isArmstrong(int num)
    {
        int cube=0;
        int remainder;
        int temp;
        temp=num;//given value of num to temp
        while(num>0)
        {
            remainder=num%10;
            num=num/10;
            cube=cube+(remainder*remainder*remainder);
        }
        if(temp==cube)
        {
            System.out.println(temp +" is armstrong ");
        }
        else
        {
            System.out.println(temp+" is not armstrong ");
        }

    }

    public static void main(String[] args)
    {
        Armstrong a = new Armstrong();
        a.isArmstrong(153);
        a.isArmstrong(1);
        a.isArmstrong(11);
    }
}
