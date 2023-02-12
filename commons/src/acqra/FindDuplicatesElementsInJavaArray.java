package acqra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Admin on 7/3/2021.
 */
public class FindDuplicatesElementsInJavaArray
{
    public static void main(String[] args)
    {
        String arr[]={"abc","pew","gjd","pew","qww"};
        for(int i=0;i< arr.length;i++)
        {
            for(int j=i+1;j<arr.length;j++)
            {
                if(arr[i].equals(arr[j]))
                {
                    System.out.println("1st way");
                    System.out.println("duplicate -- "+arr[i]);
                }
            }
        }
        HashSet<String> hashSet = new HashSet<String>();
        for(String str : arr)
        {
            if(hashSet.add(str)==false)
            {
                System.out.println("2nd way");
                System.out.println("duplicate -- "+str);
            }
        }

    }
}
