package practice;

import java.util.Scanner;

/**
 * Created by Admin on 2/13/2021.
 */
public class matrixprog1_antidiagonal
{
    public static void main(String[] args)
    {

        int arr[][]= {{1,89,1},{90,1,6},{1,76,1}};
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                System.out.print(arr[i][j]+" ");
            }
            System.out.println();

        }
     for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(i==j||i+j==2)
                {
                    System.out.print(arr[i][j]);
                }
                else
                {
                    System.out.print(" ");
                }

            }
            System.out.println();
        }
    }

}
