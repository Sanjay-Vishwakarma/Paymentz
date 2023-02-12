package practice;

/**
 * Created by Admin on 2/13/2021.
 */
public class Any_largest_number_in_array
{
    public static int ascending_method(int[] arr)
    {
        for(int i=0;i<arr.length;i++)
        {
            for(int j=i+1;j<arr.length;j++)
            {
                if(arr[i]>arr[j])
                {
                    int temp=arr[i];
                    arr[i]=arr[j];
                    arr[j]=temp;
                }
            }
        }
        for(int i=0;i<arr.length;i++)
        {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
        return arr[arr.length-2];
    }
    public static void main(String[] args)
    {
        int arr[]={90,23,1,91,26,100};
        Any_largest_number_in_array any_largest_number_in_array = new Any_largest_number_in_array();
        any_largest_number_in_array.ascending_method(arr);

        int sec_largest=ascending_method(arr);
        System.out.println("sec_largest---"+sec_largest);
    }
}
