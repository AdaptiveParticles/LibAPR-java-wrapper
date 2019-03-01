package adaptiveparticles.wrapper.presets;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(
        target = "adaptiveparticles.apr",
        value = @Platform(
                include={"APR.h",
                        "algorithm/APRParameters.hpp",
                        "data_structures/APR/APRAccessStructures.hpp",
                        "data_structures/APR/APRIterator.hpp",
                        "data_structures/APR/GenIterator.hpp",
                        "algorithm/LocalParticleCellSet.hpp",
                        "data_structures/Mesh/PixelData.hpp"
                },
                define = {"UNIQUE_PTR_NAMESPACE std", "SHARED_PTR_NAMESPACE std"},
                link = { "hdf5", "apr"}
        ),
        helper = "adaptiveparticles.wrapper.helper.AprLibHelper"
)
public class AprLibConfig implements InfoMapper {
    public void map(InfoMap infoMap) {
        infoMap.put(new Info("std::vector<MapIterator>").pointerTypes("MapIteratorVector").define()).
                put(new Info("std::map<uint16_t,YGap_map>", "std::map<uint16_t,YGap_map>::iterator").skip()).
                put(new Info("PixelData<uint16_t>").pointerTypes("PixelDataShort").skip()).
                put(new Info("PinnedMemoryUniquePtr").skip()).
                put(new Info("ArrayWrapper<uint16_t>").pointerTypes("ArrayWrapperShort").skip());
    }
}
