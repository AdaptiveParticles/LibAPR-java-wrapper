package mosaic.imagej;

import mosaic.BdvFullReconstruction;
import mosaic.JavaAPR;
import net.imglib2.img.Img;
import net.imglib2.img.WrappedImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * This is intended to be "The data type to represent an APR" in ImageJ.
 */
public class AprImg extends ForwardingImg< UnsignedShortType > implements WrappedImg< UnsignedShortType >, AutoCloseable
{
	private final Img<  UnsignedShortType > img;

	private final JavaAPR apr;

	private AprImg( Img<  UnsignedShortType > img, JavaAPR apr )
	{
		super( img );
		this.img = img;
		this.apr = apr;
	}

	public static AprImg of( JavaAPR apr ) {
	    BdvFullReconstruction bdv = new BdvFullReconstruction();
	    bdv.init( apr.data(), apr.width(), apr.height(), apr.depth());
		Img< UnsignedShortType > img = bdv.getImg();
		return new AprImg( img, apr );
	}

	public Img< UnsignedShortType> getImg() {
		return img;
	}

	public JavaAPR getAPR() {
		return apr;
	}

	@Override
	public void close() throws Exception
	{
		// TODO: make sure this is called when appropriate
		apr.close();
	}
}
