package practice;

/**
 * Created by Admin on 2/25/2021.
 */
public class Searching_Arrays

{
    public static void main(String[] args)
    {
        int[] arrays={10,2,33,900,454,43};
    //    binarysearch(arrays,0,arrays.length,33);
       binarysearch(arrays,0,arrays.length,50);
    }

    public static void binarysearch(int[] array,int lowerbound,int upperbound,int key)
    { //key means number to be searched
       int position;
        position=(lowerbound+upperbound)/2; // to start find the middle position
        while((array[position]!=key)&&(lowerbound<=upperbound))// try to find the match
        {
            if(array[position]>key)// to check if key is less than the middle portion of the array
            {
                upperbound=position-1;//if it is it resets the upperbound
            }
            else
            {
                lowerbound=position+1;//if it is not it resets the positon of lowerbound
            }
            position=(lowerbound+upperbound)/2;//From here it finds out the next half array
        }
        if(lowerbound<=upperbound)
        {
            System.out.println("The number was found in the array at position -- "+position);
        }
        else
        {
            System.out.println("Not found ");
        }
    }
}
