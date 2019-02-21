#ifndef __APR_H__
#define __APR_H__

#include "data_structures/APR/APR.hpp"
#include "numerics/APRTreeNumerics.hpp"
#include "numerics/APRNumerics.hpp"

#include "io/APRTimeIO.hpp"

#include <cstdint>
#include <iostream>

class JavaAPR {
    PixelData <uint16_t> reconstructedImage;
    APR <uint16_t> apr;
    APRTree<uint16_t> aprTree;
    ExtraParticleData<float> partsTree;
    int currentTimePoint;
    int totalTimePoints;
    APRWriter aprWriter;
    std::string currentFileName;

    APRTimeIO<uint16_t> aprTimeIO;
    bool delta_mode;
    bool display_level;
    bool max_down_sample;

    ExtraParticleData<uint16_t> parts;

    ExtraParticleData<uint8_t> treeMaxLevel;

    APRParameters PipelinePars;

    int levelMax_;
    int levelMin_;

public:
    JavaAPR () {
        currentTimePoint = 0;
        delta_mode = false;
        display_level = false;
        max_down_sample = false;
        levelMax_ = 0;
        levelMin_ = 0;
    }

    int16_t *data() {return (int16_t*)reconstructedImage.mesh.get();}

    int height() const {return apr.orginal_dimensions(1);}
    int width() const {return apr.orginal_dimensions(0);}
    int depth() const {return apr.orginal_dimensions(2);}
    int numberParticles() const {return apr.total_number_particles();}

    int timePoint() const {return currentTimePoint;}
    int numberTimePoints() const {return totalTimePoints;}

    int levelMax()  {return levelMax_;}
    int levelMin()  {return levelMin_;}

    void read(const std::string &aAprFileName) {

        currentFileName = aAprFileName;

        delta_mode = aprWriter.time_adaptation_check(aAprFileName);

        if(!delta_mode){
            std::cout << "Reading standard file" << std::endl;
            apr.read_apr(aAprFileName);
            aprTree.init(apr);

            if(display_level){
                APRNumerics::compute_part_level(apr,parts);
            } else {
                std::swap(parts,apr.particles_intensities);
            }

            if(max_down_sample){
                APRTreeNumerics::fill_tree_max(apr,aprTree,parts,partsTree); //use max
            } else {
                APRTreeNumerics::fill_tree_mean(apr,aprTree,parts,partsTree); //use mean
            }

            APRTreeNumerics::fill_tree_max_level(apr,aprTree,treeMaxLevel);

            levelMax_ = apr.level_max();
            levelMin_ = apr.level_min();

	        totalTimePoints=aprWriter.get_num_time_steps(aAprFileName)+1;
        } else {
            std::cout << "Reading APR+T file" << std::endl;
            aprTimeIO.read_apr_init(aAprFileName);
            totalTimePoints=aprTimeIO.number_time_steps;
            aprTimeIO.read_time_point(0);
            aprTree.init(*aprTimeIO.current_APR);

            if(display_level){
                APRNumerics::compute_part_level(*aprTimeIO.current_APR,parts);
            } else {
                aprTimeIO.copy_pcd_to_parts(*aprTimeIO.current_APR,parts,aprTimeIO.current_particles);
            }

            if(max_down_sample){
                APRTreeNumerics::fill_tree_max(*aprTimeIO.current_APR,aprTree,parts,partsTree);
            } else {
                APRTreeNumerics::fill_tree_mean(*aprTimeIO.current_APR,aprTree,parts,partsTree);
            }

            APRTreeNumerics::fill_tree_max_level(*aprTimeIO.current_APR,aprTree,treeMaxLevel);

            levelMax_= aprTimeIO.current_APR->level_max();
            levelMin_ = aprTimeIO.current_APR->level_min();

            apr.copy_from_APR(*aprTimeIO.current_APR);

        }

	    std::cout << totalTimePoints << std::endl;
    }

     void read(int newTimePoint) {
        currentTimePoint = newTimePoint;
        if(newTimePoint < totalTimePoints){
            std::cout << newTimePoint << std::endl;

            if(!delta_mode){
                APR<uint16_t> aprRead;
                APRTree<uint16_t> aprTree2;
                APRTree<uint16_t> aprTree3;
                ExtraParticleData<float> partsTree2;

                //aprWriter.read_apr(apr,currentFileName,false,0,(unsigned int)newTimePoint);

                aprWriter.read_apr(aprRead,currentFileName,false,0,(unsigned int)newTimePoint);

                apr.copy_from_APR(aprRead);

                aprTree2.init(apr);

                //aprTree3.init(apr);

                 if(display_level){
                       APRNumerics::compute_part_level(apr,parts);
                  } else {
                       std::swap(parts,apr.particles_intensities);
                  }

                 if(max_down_sample){

                    APRTreeNumerics::fill_tree_max(apr,aprTree2,parts,partsTree);
                 } else {
                    APRTreeNumerics::fill_tree_mean(apr,aprTree2,parts,partsTree);
                 }

                aprTree.copyTree(aprTree2);


            } else{
                aprTimeIO.read_time_point(newTimePoint);

                if(display_level){
                     APRNumerics::compute_part_level(*aprTimeIO.current_APR,parts);
                } else {
                     aprTimeIO.copy_pcd_to_parts(*aprTimeIO.current_APR,parts,aprTimeIO.current_particles);
                }

                //read tree
                APRTree<uint16_t> aprTree2;
                aprTree2.init(*aprTimeIO.current_APR);

                //down-sample
                if(max_down_sample){
                    APRTreeNumerics::fill_tree_max(*aprTimeIO.current_APR,aprTree2,parts,partsTree);
                } else {
                    APRTreeNumerics::fill_tree_mean(*aprTimeIO.current_APR,aprTree2,parts,partsTree);
                }

                APRTreeNumerics::fill_tree_max_level(*aprTimeIO.current_APR,aprTree2,treeMaxLevel);

                aprTree.copyTree(aprTree2);

                std::cout << "Total added (APR+t): " << aprTimeIO.get_num_updated() << std::endl;
                std::cout << "Total added (APR): " << aprTimeIO.current_APR->total_number_particles() << std::endl;
                std::cout << "Total added (Pixels): " << aprTimeIO.current_APR->orginal_dimensions(0)*aprTimeIO.current_APR->orginal_dimensions(1)*aprTimeIO.current_APR->orginal_dimensions(2) << std::endl;
            }

        }
    }

    void getAPRBufferInternal(float* buffer){

        auto num_parts = apr.total_number_particles();

            auto apr_iterator = apr.iterator();

            auto buffer_size = 8*num_parts;

            std::vector<float> apr_buffer;
            apr_buffer.resize(buffer_size);

            buffer = apr_buffer.data();

            ExtraParticleData<std::vector<float>> gradient;

            APRNumerics::compute_gradient_vector(apr,gradient);

            for (unsigned int level = apr_iterator.level_min(); level <= apr_iterator.level_max(); ++level) {
                int z = 0;
                int x = 0;

        #ifdef HAVE_OPENMP
        #pragma omp parallel for schedule(dynamic) private(z, x) firstprivate(apr_iterator)
        #endif
                for (z = 0; z < apr_iterator.spatial_index_z_max(level); z++) {
                    for (x = 0; x < apr_iterator.spatial_index_x_max(level); ++x) {
                        for (apr_iterator.set_new_lzx(level, z, x); apr_iterator.global_index() < apr_iterator.end_index;
                             apr_iterator.set_iterator_to_particle_next_particle()) {

                            apr_buffer[apr_iterator] = apr_iterator.y();
                            apr_buffer[apr_iterator + num_parts] = apr_iterator.x();
                            apr_buffer[apr_iterator + num_parts*2] = apr_iterator.z();
                            apr_buffer[apr_iterator + num_parts*3] = apr_iterator.level();

                            apr_buffer[apr_iterator + num_parts*4] = apr.particles_intensities[apr_iterator];

                            apr_buffer[apr_iterator + num_parts*5] = gradient[apr_iterator][0];
                            apr_buffer[apr_iterator + num_parts*6] = gradient[apr_iterator][1];
                            apr_buffer[apr_iterator + num_parts*7] = gradient[apr_iterator][2];

                        }
                    }
                }
            }


    }


   void showLevel(){
         //instead of the particle instensities show the adaptation level.
         std::cout << "Displaying level instead of Intensities" << std::endl;
         display_level = true;
    }

    void setMaxDownsample(){
         //instead of the particle instensities show the adaptation level.
         std::cout << "Using Maximum down-sample instead of mean" << std::endl;
         max_down_sample = true;
    }

 /*
  *
  *    Reconstruct Patch
  */

    // Default values for min/max will reconstruct whole image
    void reconstruct(int x_min = 0, int x_max = -2, int y_min = 0, int y_max = -2, int z_min = 0, int z_max = -2) {
        ReconPatch r;
        // Intentionally swapped x<->y
        r.x_begin = y_min;
        r.x_end = y_max + 1;
        r.y_begin = x_min;
        r.y_end = x_max + 1;
        r.z_begin = z_min;
        r.z_end = z_max + 1;

        APRReconstruction().interp_image_patch(apr, aprTree, reconstructedImage, parts, partsTree, r);
    }

    void reconstructToBuffer(int x, int y, int z, int width, int height, int depth, int level, uint16_t* buffer) {
//		printf("in reconstructToBuffer(%d, %d, %d, %d, %d, %d, %p)\n", x,y,z,width,height,depth,buffer);
//		fflush(stdout);

        ReconPatch r;
        // Intentionally swapped x<->y
        r.x_begin = y;
        r.x_end = y + height;
        r.y_begin = x;
        r.y_end = x + width;
        r.z_begin = z;
        r.z_end = z + depth;
        r.level_delta = -level;

        PixelData <uint16_t> img;
        if(!delta_mode){
            APRReconstruction().interp_image_patch(apr, aprTree, img, parts, partsTree, r);
        } else {
            APRReconstruction().interp_image_patch(*aprTimeIO.current_APR, aprTree, img, parts, partsTree, r);
        }

        memcpy( buffer, img.mesh.get(), 2 * width * height * depth );
    }

    int checkReconstructionRequired(int x, int y, int z, int width, int height, int depth, int level){
        ReconPatch r;
        // Intentionally swapped x<->y
        r.x_begin = y;
        r.x_end = y + height;
        r.y_begin = x;
        r.y_end = x + width;
        r.z_begin = z;
        r.z_end = z + depth;
        r.level_delta = -level;

        auto render_level = levelMax_ - level;

        auto max_patch_size = std::max(width,std::max(height,depth));
        int patch_level_delta = std::ceil(std::log2(max_patch_size));

        int global_level_check = (render_level - patch_level_delta);

        if(global_level_check <= levelMin_){
            return 1;
        } else {
            const float step_size = pow(2,patch_level_delta);
            int x_begin_l = (int) floor(y/step_size);
            int z_begin_l= (int)floor(z/step_size);
            int y_begin_l =  (int)floor(x/step_size);

            auto aprTreeIterator = aprTree.tree_iterator();

            aprTreeIterator.set_new_lzxy(global_level_check, z_begin_l, x_begin_l,y_begin_l);

            if(aprTreeIterator < aprTreeIterator.end_index){
                auto max_level = treeMaxLevel[aprTreeIterator];
                if(max_level < render_level){
                    return 0;
                } else {
                    return 1;
                }
            } else{
                return 0; //tree doesn't exist at this level no need to render
            }
        }

    }



    /*
    *
    *    Get APR from img
    */

    JavaAPR* get16bitUnsignedAPRInternal(int width, int height, int depth, int bpp, uint16_t* buffer) {
        PixelData<uint16_t> p = PixelData<uint16_t>(width, height, depth);
        p.mesh.set(buffer, width*height*depth);
        apr.get_apr(p);

        return this;
    }


    void set_I_th(float I_th)// Threshold, adaptation ignores areas below this fixed intensity threshold
    {
        PipelinePars.Ip_th = I_th;
    }
    void set_lambda(float lambda) // Smoothing parameter for calculation of the gradient (default: 3, range 0.5-5)
    {
        PipelinePars.lambda = lambda;
    }
    void set_local_scale_min(float local_scale_min)  // Minimum local contrast (object brightness - background)
    {
        PipelinePars.sigma_th = local_scale_min;
    }

    void set_local_scale_ignore_th(float local_scale_ignore_th) // Threshold of the local scale below which the adaptation ignores
    {
        PipelinePars.sigma_th_max = local_scale_ignore_th;
    }
    void set_relative_error_E(float relative_error_E) // Relative error E, default 0.1
    {
        PipelinePars.rel_error = relative_error_E;
    }
    void use_auto_parameters(bool turn_on) //calculate automatic parameters from the image
    {
        PipelinePars.auto_parameters = turn_on;
    }

    void use_neighborhood_optimization(bool turn_on) // use the neighborhood optimization -> (See Cheeseman et. al. 2018), Default: on;
    {
        PipelinePars.neighborhood_optimization = turn_on;
    }

    void write(std::string directory,std::string name){
        apr.write_apr(directory,name);
    }


};

#endif //__APR_H__
