package mosaic;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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
    public native void read(int timeStep);
    public native void reconstruct();
    public native void showLevel();
    public native int numberParticles();
    public native void setMaxDownsample();
    public native void reconstruct(int x_min, int x_max, int y_min, int y_max, int z_min, int z_max);
    public native int width();
    public native int height();
    public native int depth();

    public native void write(String directory,String name);

    public native int timePoint();
    public native int numberTimePoints();

    public native ShortPointer data();

    protected native JavaAPR get16bitUnsignedAPRInternal(int width, int height, int depth, int bpp, @Cast("uint16_t*") ShortPointer buffer);
    protected native void reconstructToBuffer(int x, int y, int z, int width, int height, int depth, int level, @Cast("uint16_t*") Pointer buffer );

    protected native void getAPRBufferInternal( @Cast("float*") FloatPointer buffer);

    public void getAPRBuffer(final FloatBuffer buffer){
        if ( !buffer.isDirect() )
            throw new IllegalArgumentException();
        getAPRBufferInternal(new FloatPointer(buffer));
    }

    public void reconstructToBuffer( final int x, final int y, final int z, final int width, final int height, final int depth, final ShortBuffer buffer ) {
    	reconstructToBuffer( x, y, z, width, height, depth, 0, buffer );
    }

    public void reconstructToBuffer( final int x, final int y, final int z, final int width, final int height, final int depth, final int level, final ShortBuffer buffer ) {
        if ( !buffer.isDirect() )
            throw new IllegalArgumentException();
        reconstructToBuffer( x, y, z, width, height, depth, level, new ShortPointer( buffer ) );
    }

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
