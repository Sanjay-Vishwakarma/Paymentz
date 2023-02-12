package acqra;

/**
 * Created by Admin on 7/3/2021.
 */
public class findMissingNumberInArray
{
    public static void main(String[] args)
    {
        int arr[]={11,12,13,15};
        int sum1=0;
        for(int i=0;i<arr.length;i++)
        {
            sum1=sum1+arr[i];
        }
        System.out.println(sum1);

        int sum2=0;
        for(int j=11;j<=15;j++)
        {
            sum2=sum2+j;
        }
        System.out.println(sum2);
        System.out.println("MissingNumberInArray--"+(sum2-sum1));
    }
}
