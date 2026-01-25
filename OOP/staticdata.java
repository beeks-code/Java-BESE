package OOP;
class data{
    static int value=10;
    void increment(int by){
        value=value+by;
    }
    // static method it can use static value only;
    static void printvalue(){
        System.err.println("Static value is "+value);


    }
}
public class staticdata {
    public static void main(String[] args) {
        data obj1=new data();
        data obj2=new data();
        obj1.increment(20);
        obj2.increment(20);
        
        // increased by 20+20
        obj1.printvalue();
        obj2.printvalue();

    }
    
}
