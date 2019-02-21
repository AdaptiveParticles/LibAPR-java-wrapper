package mosaic;

public class JavaAPRPipelinePars {


    public native void set_I_th(float I_th);  // Threshold, adaptation ignores areas below this fixed intensity threshold
    public native void set_lambda(float lambda); // Smoothing parameter for calculation of the gradient (default: 3, range 0.5-5)
    public native void set_local_scale_min(float local_scale_min);  // Minimum local contrast (object brightness - background)
    public native void set_local_scale_ignore_th(float local_scale_ignore_th);  // Threshold of the local scale below which the adaptation ignores
    public native void set_relative_error_E(float relative_error_E); // Relative error E, default 0.1

    public native void use_auto_parameters(Boolean turn_on); //calculate automatic parameters from the image
    public native void use_neighborhood_optimization(Boolean turn_on); // use the neighborhood optimization -> (See Cheeseman et. al. 2018), Default: on;

    JavaAPRPipelinePars(){

    }
}
