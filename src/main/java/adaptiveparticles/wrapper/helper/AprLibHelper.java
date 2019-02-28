package adaptiveparticles.wrapper.helper;

import adaptiveparticles.apr;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.NoOffset;
import adaptiveparticles.wrapper.presets.AprLibConfig;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * This class keeps manually defined code which is too hard for javacpp parser to process automatically
 */
public class AprLibHelper extends AprLibConfig {

    /**
     * Provides implementation for 3D mesh with elements of given type.
     */
    @Name("PixelData<uint16_t>") @NoOffset
    public static class PixelDataShort extends Pointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public PixelDataShort(Pointer p) { super(p); }
        /** Native array allocator. Access with {@link Pointer#position(long)}. */
        public PixelDataShort(long size) { super((Pointer)null); allocateArray(size); }
        private native void allocateArray(long size);
        @Override public PixelDataShort position(long position) {
            return (PixelDataShort)super.position(position);
        }

        // size of mesh and container for data
        public native @Cast("size_t") long y_num(); public native PixelDataShort y_num(long y_num);
        public native @Cast("size_t") long x_num(); public native PixelDataShort x_num(long x_num);
        public native @Cast("size_t") long z_num(); public native PixelDataShort z_num(long z_num);

        /**
         * Constructor - initialize mesh with size of 0,0,0
         */
        public PixelDataShort() { super((Pointer)null); allocate(); }
        private native void allocate();

        /**
         * Constructor - initialize initial size of mesh to provided values
         * @param aSizeOfY
         * @param aSizeOfX
         * @param aSizeOfZ
         */
        public PixelDataShort(int aSizeOfY, int aSizeOfX, int aSizeOfZ) { super((Pointer)null); allocate(aSizeOfY, aSizeOfX, aSizeOfZ); }
        private native void allocate(int aSizeOfY, int aSizeOfX, int aSizeOfZ);

        public native @Cast("uint16_t*") @ByRef ShortPointer at(@Cast("size_t") long y, @Cast("size_t") long x, @Cast("size_t") long z);

        public native void printMeshT(int aColumnWidth, int aFloatPrecision/*=10*/, @Cast("bool") boolean aXYplanes/*=true*/);
        public native void printMeshT(int aColumnWidth);
    }

    public static class Ops extends apr.AprBasicOps {
        public void reconstructToBuffer( final int x, final int y, final int z, final int width, final int height, final int depth, final ShortBuffer buffer ) {
            reconstructToBuffer( x, y, z, width, height, depth, 0, buffer );
        }

        public void reconstructToBuffer( final int x, final int y, final int z, final int width, final int height, final int depth, final int level, final ShortBuffer buffer ) {
            if ( !buffer.isDirect() )
                throw new IllegalArgumentException();
            reconstructToBuffer( x, y, z, width, height, depth, level, new ShortPointer( buffer ) );
        }

        public apr.AprBasicOps get16bitUnsignedAPR(int width, int height, int depth, int bpp, ShortBuffer buffer) {
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
}
