package mosaic.imagej;

import mosaic.JavaAPR;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Example ImageJ plugin that processes an APR.
 */
@Plugin( type = Command.class, menuPath = "Process > APR Example" )
public class ProcessAprExampleCommand implements Command
{

	@Parameter
	AprImg input;

	@Parameter(type = ItemIO.OUTPUT)
	AprImg output;

	@Override public void run()
	{
		output = AprImg.of( doSomething( input.getAPR() ) );
	}

	private JavaAPR doSomething( JavaAPR apr )
	{
		// TODO: implement some processing and return the result as JavaAPR
		return apr;
	}
}
