package practice;

import java.util.Scanner;

/**
 * Created by Admin on 1/12/2021.
 */
public class largestamong_entered_nos
{
    public static void main(String[] args)
    {
        int max, i, n;
        int a[];
        Scanner sc = new Scanner(System.in);
        System.out.println("enter n numbers ");
        n = sc.nextInt();
        a = new int[n];
        System.out.println("enter the numbers now ");
        for (i = 0; i < n; i++)
        {
            a[i] = sc.nextInt();
        }
        max = max_num_method(a, n);
        System.out.println("Largest number is "+ max);
    }
        static int max_num_method(int[] a,int n)
      {
        int i;
          int m=0;
          for(i=0;i<n;i++)
          {
              if(a[i]>m)
              {
              m=a[i];
              }
          }
          return m;
      }
    }

