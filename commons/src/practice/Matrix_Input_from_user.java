package practice;

import java.util.Scanner;

/**
 * Created by Admin on 2/13/2021.
 */
public class Matrix_Input_from_user
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter no.of rows ");
        int row=sc.nextInt();
        System.out.println("Enter no.of cols ");
        int col=sc.nextInt();
        System.out.println("Now enter elements");
        int matrix[][]=new int[row][col];

        for(int i=0;i<row;i++)
        {
            for(int j=0;j<col;j++)
            {
                matrix[i][j]=sc.nextInt();
            }
        }
        System.out.println("Elements are ");
        for(int i=0;i<row;i++)
        {
            for(int j=0;j<col;j++)
            {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
