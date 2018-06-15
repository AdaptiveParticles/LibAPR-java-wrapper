package mosaic.imagej;

import net.imagej.ImageJ;
import mosaic.BdvFullReconstruction;
import mosaic.JavaAPR;
import net.imglib2.img.Img;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

/**
 * The import APR menu entry.
 */
@Plugin( type = Command.class, menuPath = "File > Import > APR..." )
public class OpenAprCommand implements Command
{
	@Parameter
	File file;

	@Parameter(type = ItemIO.OUTPUT)
	AprImg output;

	@Override
	public void run()
	{
		JavaAPR apr = new JavaAPR();
		apr.read( file.getPath() );
		output = AprImg.of(apr);
	}

	public static void main(String... args)
	{
		ImageJ imageJ = new ImageJ();
		imageJ.ui().showUI();
	}
}
