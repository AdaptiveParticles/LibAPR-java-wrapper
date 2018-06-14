package mosaic;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

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
    public native void reconstruct();
    public native void reconstruct(int x_min, int x_max, int y_min, int y_max, int z_min, int z_max);
    public native int width();
    public native int height();
    public native int depth();
    public native ShortPointer data();

    protected native JavaAPR get16bitUnsignedAPRInternal(int width, int height, int depth, int bpp, @Cast("uint16_t*") ShortPointer buffer);

    public JavaAPR get16bitUnsignedAPR(int width, int height, int depth, int bpp, ShortBuffer buffer) {
        ShortPointer p;
        if(buffer.isDirect()) {
            p = new ShortPointer(buffer);
        } else {
            ShortBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2).asShortBuffer();
            newBuffer.put(buffer);

            p = new ShortPointer(newBuffer);
        }
        return get16bitUnsignedAPRInternal(width, height, depth, bpp, p);
    }
}
