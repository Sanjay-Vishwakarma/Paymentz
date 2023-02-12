package practice;

/**
 * Created by Admin on 3/1/2021.
 */
public class Largest_3_numbers_from_Array
{
    static void print3largest(int arr[],int arraysize)
    {
        int i,no1,no2,no3;
        if(arraysize<3)
        {
            System.out.println("Invalid input");
            return;
        }
        no3=no1=no2= Integer.MIN_VALUE;
        for(i=0;i<arraysize;i++)
        {
            if(arr[i]>no1)
            {
                no3=no2;
                no2=no1;
                no1=arr[i];
            }
            else if(arr[i]>no2)
            {
                no3=no2;
                no2=arr[i];
            }
            else
            {
                no3=arr[i];
            }

        }

        System.out.println("Three largest elements "+no1+" "+no2+" "+no3);
    }

    public static void main(String[] args)
    {
        int array[]={10,90,7,8,6,3,1,20,202};
        int sizeofarray=array.length;
       print3largest(array,sizeofarray);
    }
}
