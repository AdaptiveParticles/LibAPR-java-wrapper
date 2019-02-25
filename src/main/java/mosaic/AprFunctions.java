package mosaic;

import org.bytedeco.javacpp.ShortPointer;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class AprFunctions extends LibApr.JavaAPR {
    public void reconstructToBuffer( final int x, final int y, final int z, final int width, final int height, final int depth, final ShortBuffer buffer ) {
        reconstructToBuffer( x, y, z, width, height, depth, 0, buffer );
    }

    public void reconstructToBuffer( final int x, final int y, final int z, final int width, final int height, final int depth, final int level, final ShortBuffer buffer ) {
        if ( !buffer.isDirect() )
            throw new IllegalArgumentException();
        reconstructToBuffer( x, y, z, width, height, depth, level, new ShortPointer( buffer ) );
    }

    public void get16bitUnsignedAPR(int width, int height, int depth, int bpp, ShortBuffer buffer) {
        ShortPointer p;
        if(buffer.isDirect()) {
            p = new ShortPointer(buffer);
        } else {
            ShortBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2).asShortBuffer();
            newBuffer.put(buffer);

            p = new ShortPointer(newBuffer);
        }
        get16bitUnsignedAPRInternal(width, height, depth, bpp, p);
    }
}
