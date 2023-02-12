package acqra;

/**
 * Created by Admin on 4/20/2021.
 */
public class missing_element_in_array
{
    public static void main(String[] args)
    {
        int num[]={1,2,4,5};
        int missing_num=findMissingNumber(num,5);
        System.out.println(missing_num);
    }
    public static int findMissingNumber(int num[],int totalcount)
    {
        /*formula
        n*(n+1)/2
         */
        int expSum=totalcount*((totalcount+1)/2);
        int actualSum=0;
        for(int i:num)
        {
            actualSum=actualSum+i;
        }
        return expSum-actualSum;
    }
}
