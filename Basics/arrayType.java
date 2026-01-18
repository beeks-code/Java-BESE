package Basics;
import java.sql.Struct;

public class arrayType {
    public static void main(String args[]){
        // Creating an array
        int age[]=new int[10]; // new keyword in java is used to allocated memory
        age[0]=5;
        age[1]=10;
        System.out.println("Each uninitialized value in array is initialized with"+age[2]);

        // Initializing an array
        String days[]={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        // Multidimensional Array or Matrices
        int weights[][] = {{2, 3, 4},{4, 3, 5}}; 

        System.out.println("2D array looks like"+weights); // It will give memroy reference of that array
        for(int i=0; i<=1;i++){
        for(int j=0; j<=2;j++){
            System.out.printf("%d %d th value is %d \n",i+1,j+1,weights[i][j]);
        }

        }
    }
}
 