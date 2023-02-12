/*
package practice;

*/
/**
 * Created by Admin on 3/3/2021.
 *//*

public class Sorting_of_Array_Depending_on_freq_of_elements
{
    static int x=0;
    public static void main(String[] args)
    {
        int array[]={5,8,7,5,5,5,6,5,1,2,6,11,11};
        int b[][]=new int[array.length][2];//2nd column will define the occurence
        array=sort_method(array);
        b[x][0]=array[0];


        for(int var :array)
        {
            if(array[var]==array[var-1])
            {
                b[x][0]=b[x][1]+1;
            }
            else
            {
                x++;
                b[x][0]=array[var];
            }
        }
        x++;
        b=sort_method(b);

    }
    public static int[] sort_method(int a[])
    {
          for(int i=0;i<a.length;i++)
          {
              for(int j=i+1;j<a.length;j++)
              {
                  if(a[i]>a[j])
                  {
                      int temp=a[i];
                      a[i]=a[j];
                      a[j]=temp;
                  }
              }
          }

        return a;

    }
}
*/
