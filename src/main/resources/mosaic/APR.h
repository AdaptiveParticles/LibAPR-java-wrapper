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

public:
    JavaAPR () {
        currentTimePoint = 0;
        delta_mode = false;
        display_level = false;
        max_down_sample = false;
    }
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

                    APRTreeNumerics::fill_tree_max(apr,aprTree2,parts,partsTree2);
                 } else {
                    APRTreeNumerics::fill_tree_mean(apr,aprTree2,parts,partsTree2);
                 }

                aprTree.copyTree(aprTree2);

                partsTree.data = partsTree2.data;



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
                aprTree.copyTree(aprTree2);
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

    JavaAPR* get16bitUnsignedAPRInternal(int width, int height, int depth, int bpp, uint16_t* buffer) {
        PixelData<uint16_t> p = PixelData<uint16_t>(width, height, depth);
        p.mesh.set(buffer, width*height*depth);
        apr.get_apr(p);

        return this;
    }

    void write(std::string directory,std::string name){
        apr.write_apr(directory,name);
    }

    int16_t *data() {return (int16_t*)reconstructedImage.mesh.get();}

    int height() const {return apr.orginal_dimensions(1);}
    int width() const {return apr.orginal_dimensions(0);}
    int depth() const {return apr.orginal_dimensions(2);}
    int numberParticles() const {return apr.total_number_particles();}

    int timePoint() const {return currentTimePoint;}
    int numberTimePoints() const {return totalTimePoints;}

};

#endif //__APR_H__
