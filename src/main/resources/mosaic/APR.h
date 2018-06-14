#ifndef __APR_H__
#define __APR_H__

#include "data_structures/APR/APR.hpp"
#include "numerics/APRTreeNumerics.hpp"
#include <cstdint>
#include <iostream>

class JavaAPR {
    PixelData <uint16_t> reconstructedImage;
    APR <uint16_t> apr;

public:
    JavaAPR () {}
    void read(const std::string &aAprFileName) {
        apr.read_apr(aAprFileName);
    }

    // Default values for min/max will reconstruct whole image
    void reconstruct(int x_min = 0, int x_max = -1, int y_min = 0, int y_max = -1, int z_min = 0, int z_max = -1) {
        APRTree<uint16_t> aprTree;
        aprTree.init(apr);
        ExtraParticleData<uint16_t> partsTree;
        APRTreeNumerics::fill_tree_from_particles(apr,aprTree,apr.particles_intensities,partsTree,[] (const uint16_t& a,const uint16_t& b) {return std::max(a,b);});

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
};

#endif //__APR_H__
