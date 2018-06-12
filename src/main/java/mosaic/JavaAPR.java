package mosaic;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;

@Properties( value = { @Platform(include = "APR.h", link = { "apr", "hdf5"}) })
public class JavaAPR extends Pointer {

    // A 'must be' stuff for creating/destroying object
    static {
        Loader.load();
    }
    public JavaAPR() {
        allocate();
    }
    private native void allocate();

    // Declarations of all methods from JavaAPR class
    public native void read(String s);
    public native int width();
    public native int height();
    public native int depth();
    public native ShortPointer data(); 
}
