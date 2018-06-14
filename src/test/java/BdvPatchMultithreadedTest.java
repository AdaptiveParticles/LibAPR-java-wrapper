import static net.imglib2.cache.img.ReadOnlyCachedCellImgOptions.options;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import net.imglib2.cache.img.CellLoader;
import net.imglib2.cache.img.DiskCachedCellImgOptions.CacheType;
import net.imglib2.cache.img.ReadOnlyCachedCellImgFactory;
import net.imglib2.cache.img.ReadOnlyCachedCellImgOptions;
import net.imglib2.cache.img.SingleCellArrayImg;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Intervals;

import org.bytedeco.javacpp.ShortPointer;
import org.junit.Test;

import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import bdv.util.volatiles.VolatileViews;
import mosaic.JavaAPR;

public class BdvPatchMultithreadedTest
{
	ShortPointer data;

	int width, heigth, depth;

	JavaAPR apr;

	private final ThreadLocal< ShortBuffer > tlBuffer = ThreadLocal.withInitial( () -> ByteBuffer.allocateDirect( 2 ).order( ByteOrder.nativeOrder() ).asShortBuffer() );

	public class PatchImgLoader implements CellLoader< UnsignedShortType >
	{
		@Override
		public void load( final SingleCellArrayImg< UnsignedShortType, ? > cell ) throws Exception
		{
			final int size = ( int ) Intervals.numElements( cell );
			if ( tlBuffer.get().capacity() < size )
				tlBuffer.set( ByteBuffer.allocateDirect( 2 * size ).order( ByteOrder.nativeOrder() ).asShortBuffer() );
			final ShortBuffer buffer = tlBuffer.get();
			buffer.rewind();
			apr.reconstructToBuffer(
					( int ) cell.min( 0 ),
					( int ) cell.min( 1 ),
					( int ) cell.min( 2 ),
					( int ) cell.dimension( 0 ),
					( int ) cell.dimension( 1 ),
					( int ) cell.dimension( 2 ),
					buffer );
			buffer.get( ( short[] ) cell.getStorageArray(), 0, size );
		}
	}

	public void init( final ShortPointer imgData, final int w, final int h, final int d )
	{
		data = imgData;
		width = w;
		heigth = h;
		depth = d;
	}

	/**
	 * Creates big data viewer with one big cell containing whole image (good
	 * enough for proof of concept)
	 */
	public void show()
	{
		final int[] cellDimensions = new int[] { 32, 32, 32 };
		final long[] dimensions = new long[] { width, heigth, depth };

		final ReadOnlyCachedCellImgOptions options = options()
				.cellDimensions( cellDimensions )
				.cacheType( CacheType.SOFTREF );

		final CellLoader< UnsignedShortType > loader = new PatchImgLoader();
		final Img< UnsignedShortType > img = new ReadOnlyCachedCellImgFactory( options ).create( dimensions, new UnsignedShortType(), loader );

		final BdvStackSource< ? > bdv = BdvFunctions.show(
				VolatileViews.wrapAsVolatile( img ), "APR TEST" );
//        bdv.setDisplayRange(0, 7000);
	}

	public static void main( final String[] args ) throws FileNotFoundException
	{
		new BdvPatchMultithreadedTest().testShowImg();
	}

	@Test
	public void testShowImg() throws FileNotFoundException
	{
		// "must be" print statement in any new software
		System.out.println( "Hello from Java APR!" );

		final URL resource = this.getClass().getResource( "zebra.h5" );
		if ( resource == null ) { throw new FileNotFoundException( "Could not find example file!" ); }
		final String filename = resource.toString().substring( resource.toString().indexOf( "file:/" ) + 5 );
//        String filename = "/Users/gonciarz/Documents/MOSAIC/work/repo/LibAPR/build/output_apr.h5";

		// ======================== Create APR =========================
		apr = new JavaAPR();

		// ======================== Load APR ===========================
		System.out.println( "Loading [" + filename + "]" );
		apr.read( filename );
		System.out.println( "Img Size (w/h/d): " + apr.width() + "/" + apr.height() + "/" + apr.depth() );

		// ======================== Start BDV ===========================
		init( apr.data(), apr.width(), apr.height(), apr.depth() );
		show();
		try
		{
			Thread.sleep( 15000 );
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
		// ======================== Destroy APR =========================
		apr.close();
	}
}
