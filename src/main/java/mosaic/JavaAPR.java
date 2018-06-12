package mosaic;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

//@Platform(include = "APR.h")
@Properties( value = { @Platform(include = "APR.h", link = { "apr"}) })
public class JavaAPR extends Pointer {

    static {
        Loader.load();
    }

    public JavaAPR() {
        allocate();
    }

    private native void allocate();


    public static void main(String[] args) {
        // Create the Abc instance also in C++.
        System.out.println("Hello from Java APR");
        JavaAPR abc = new JavaAPR();
        abc.close();
    }
}