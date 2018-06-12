#ifndef __APR_H__
#define __APR_H__

#include "data_structures/APR/APR.hpp"

class JavaAPR {
    PixelData <uint16_t> reconstructedImage;

public:
    JavaAPR () {}
    void read(const std::string &aAprFileName) {
        APR <uint16_t> apr;
        apr.read_apr(aAprFileName);
        ReconPatch r;
        APRReconstruction().interp_image_patch(apr, reconstructedImage, apr.particles_intensities, r);
    }

    int16_t *data() {return (int16_t*)reconstructedImage.mesh.get();}
    int height() const {return reconstructedImage.x_num;}
    int width() const {return reconstructedImage.y_num;}
    int depth() const {return reconstructedImage.z_num;}
};

#endif //__APR_H__