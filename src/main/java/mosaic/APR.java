package mosaic;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include = "APR.h")
@Namespace("APR")
public class APR extends Pointer {

    static {
        Loader.load();
    }

    public APR() {
        allocate();
    }

    private native void allocate();


    public static void main(String[] args) {
        // Create the Abc instance also in C++.
        APR abc = new APR();
        abc.close();
    }
}