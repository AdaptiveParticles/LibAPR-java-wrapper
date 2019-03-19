package adaptiveparticles;

import adaptiveparticles.apr.AprBasicOps;
import org.junit.Test;
import java.io.FileNotFoundException;
import java.net.URL;
import static org.junit.Assert.assertTrue;

public class TestAprWrapper {

    @Test
    public void testCreateAprObjAndLoadFile() throws FileNotFoundException {
        AprBasicOps apr = new AprBasicOps();
        final URL resource = this.getClass().getResource("test_apr.h5");
        if (resource == null) {
            throw new FileNotFoundException("Could not find example file!");
        }
        String filename = resource.getFile();
        apr.read(filename);

        assertTrue(apr.height() == 120);
        assertTrue(apr.width() == 120);
        assertTrue( apr.depth() == 120);
    }

}
