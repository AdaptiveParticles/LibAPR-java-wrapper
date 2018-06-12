package mosaic;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Properties( value = { @Platform(include = "APR.h", link = { "apr", "hdf5"}) })
public class JavaAPR extends Pointer {

    static {
        Loader.load();
    }

    public JavaAPR() {
        allocate();
    }

    private native void allocate();

    public native void read(String s);
    public native int width();
    public native int height();
    public native int depth();
    public native FloatPointer data(); 
    
    public static void main(String[] args) {
        // Create the Abc instance also in C++.
        System.out.println("Hello from Java APR!");
        JavaAPR apr = new JavaAPR();
        apr.read("/Users/gonciarz/Documents/MOSAIC/work/repo/LibAPR/test/files/Apr/sphere_120/sphere_apr.h5");
        System.out.println("SIZE: " + apr.width() + "/" + apr.height() + "/" + apr.depth());
        FloatPointer data = apr.data();
        System.out.println(data.get());
        apr.close();
    }
}
