#ifndef __APR_H__
#define __APR_H__

#include "data_structures/APR/APR.hpp"
#include "numerics/APRTreeNumerics.hpp"
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

public:
    JavaAPR () {
        currentTimePoint = 0;
    }
    void read(const std::string &aAprFileName) {
        apr.read_apr(aAprFileName);
        aprTree.init(apr);
        //APRTreeNumerics::fill_tree_from_particles(apr,aprTree,apr.particles_intensities,partsTree,[] (const uint16_t& a,const uint16_t& b) {return std::max(a,b);});
	    APRTreeNumerics::fill_tree_mean(apr,aprTree,apr.particles_intensities,partsTree);

	    totalTimePoints=aprWriter.get_num_time_steps(aAprFileName)+1;

	    std::cout << totalTimePoints << std::endl;

	    currentFileName = aAprFileName;
    }

     void read(int newTimePoint) {
        currentTimePoint = newTimePoint;
        if(newTimePoint < totalTimePoints){
            std::cout << newTimePoint << std::endl;

            APR<uint16_t> aprRead;
            APRTree<uint16_t> aprTree2;
            APRTree<uint16_t> aprTree3;
            ExtraParticleData<float> partsTree2;

            //aprWriter.read_apr(apr,currentFileName,false,0,(unsigned int)newTimePoint);

            aprWriter.read_apr(aprRead,currentFileName,false,0,(unsigned int)newTimePoint);

            apr.copy_from_APR(aprRead);

            aprTree2.init(apr);

            //aprTree3.init(apr);

            APRTreeNumerics::fill_tree_mean(apr,aprTree2,apr.particles_intensities,partsTree2);


            aprTree.copyTree(aprTree2);

            partsTree.data = partsTree2.data;

        }
    }


   void showLevel(){
         APRNumerics::compute_part_level(apr,apr.particles_intensities);
         APRTreeNumerics::fill_tree_mean(apr,aprTree,apr.particles_intensities,partsTree);

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

        APRReconstruction().interp_image_patch(apr, aprTree, reconstructedImage, apr.particles_intensities, partsTree, r);
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
        APRReconstruction().interp_image_patch(apr, aprTree, img, apr.particles_intensities, partsTree, r);
        memcpy( buffer, img.mesh.get(), 2 * width * height * depth );
    }

    JavaAPR* get16bitUnsignedAPRInternal(int width, int height, int depth, int bpp, uint16_t* buffer) {
        PixelData<uint16_t> p = PixelData<uint16_t>(width, height, depth);
        p.mesh.set(buffer, width*height*depth);
        apr.get_apr(p);

        return this;
    }

    int16_t *data() {return (int16_t*)reconstructedImage.mesh.get();}

    int height() const {return apr.orginal_dimensions(1);}
    int width() const {return apr.orginal_dimensions(0);}
    int depth() const {return apr.orginal_dimensions(2);}

    int timePoint() const {return currentTimePoint;}
    int numberTimePoints() const {return totalTimePoints;}

};

#endif //__APR_H__
