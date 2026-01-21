package OOP;
class box{
    int height,width,length;
    // default constructor
    box(){
        height=10;
        width=20;
        length=40;
    }
    box(int height,int width, int length){
        this.length=length;
        this.height=height;
        this.width=width;
    }

    int volume(){
        return length*width*height;
    }

}

public class constructor {
    public static void main(String[] args) {
        box box1=new box();
        box box2=new box(20,2,5);
        System.err.println("Volume of box 1 is "+box1.volume());
        System.err.println("Volume of box 2 is "+box2.volume());

    }

    
}
