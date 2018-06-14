#ifndef __APR_H__
#define __APR_H__

#include "data_structures/APR/APR.hpp"
#include "numerics/APRTreeNumerics.hpp"
#include <cstdint>

class JavaAPR {
    PixelData <uint16_t> reconstructedImage;
    APR <uint16_t> apr;

public:
    JavaAPR () {}
    void read(const std::string &aAprFileName) {
        apr.read_apr(aAprFileName);

        reconstruct();
    }

    void reconstruct() {
        APRTree<uint16_t> aprTree;
        aprTree.init(apr);
        ExtraParticleData<uint16_t> partsTree;
        APRTreeNumerics::fill_tree_from_particles(apr,aprTree,apr.particles_intensities,partsTree,[] (const uint16_t& a,const uint16_t& b) {return std::max(a,b);});

        ReconPatch r;
        APRReconstruction().interp_image_patch(apr, aprTree, reconstructedImage, apr.particles_intensities, partsTree, r);
    }

    JavaAPR* get16bitUnsignedAPRInternal(int width, int height, int depth, int bpp, uint16_t* buffer) {
        PixelData<uint16_t> p = PixelData<uint16_t>(width, height, depth);
        p.mesh.set(buffer, width*height*depth);
        apr.get_apr(p);

        return this;
    }

    int16_t *data() {return (int16_t*)reconstructedImage.mesh.get();}

    int height() const {return reconstructedImage.x_num;}
    int width() const {return reconstructedImage.y_num;}
    int depth() const {return reconstructedImage.z_num;}
};

#endif //__APR_H__
