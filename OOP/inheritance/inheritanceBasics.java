package OOP.inheritance;
class animal{
    void sound(){
        System.out.println("Animal makes a sound");
    }
}
class dog extends animal{
    void barks(){
        System.out.println("Dog barks");
    }
}
public class inheritanceBasics {
    public static void main(String [] args){
        dog tom=new dog();
        tom.sound();
        tom.barks();


    }
}
