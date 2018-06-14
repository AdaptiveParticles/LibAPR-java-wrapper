package mosaic.imagej;

import net.imagej.ImageJ;
import mosaic.BdvTest;
import mosaic.JavaAPR;
import net.imglib2.img.Img;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin( type = Command.class, menuPath = "File > Import > APR..." )
public class OpenAprCommand implements Command
{
	@Parameter
	File file;

	@Parameter(type = ItemIO.OUTPUT)
	Img<?> output;

	@Override
	public void run()
	{
		JavaAPR apr = new JavaAPR();
		apr.read( file.getPath() );
		output = new BdvTest( apr.data(), apr.width(), apr.height(), apr.depth() ).getImg();
		// TODO apr never gets closed
	}

	public static void main(String... args)
	{
		ImageJ imageJ = new ImageJ();
		imageJ.ui().showUI();
	}
}
