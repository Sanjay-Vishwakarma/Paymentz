package practice;

import java.util.Scanner;

/**
 * Created by Admin on 2/13/2021.
 */
public class matrix_sqaure_all
{
    public static void main(String[] args)
    {
        int arr[][]={{1,2,3},{3,21,1},{10,20,3}};
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                System.out.print(arr[i][j]*arr[i][j]+" ");
            }
            System.out.println();
        }
    }
}
