package AdaptiveParticles.presets;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(
        target = "AdaptiveParticles.LibApr",
        value = @Platform(include={"APR.h", "algorithm/APRParameters.hpp"})
)
public class AprLibConfig implements InfoMapper {
    public void map(InfoMap infoMap) {}
}
