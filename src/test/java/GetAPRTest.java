import mosaic.BdvFullReconstruction;
import mosaic.JavaAPR;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
public class GetAPRTest {
  private ShortBuffer generateTestBuffer(int width) {
    final ShortBuffer b = ByteBuffer.allocateDirect(width *width *width *2).asShortBuffer();
    final int nucleiCount = 16;
    int i = 0;

    ArrayList<Integer> centers = new ArrayList<>(16);
    ArrayList<Integer> radii = new ArrayList<>(16);

    for(int j = 0; j < nucleiCount; j++) {
      centers.add((int) (Math.random()*width));
      centers.add((int) (Math.random()*width));
      centers.add((int) (Math.random()*width));
      radii.add((int) (Math.random()*32));
    }

    System.out.println("Creating " + width + "^3 test image with " + nucleiCount + " nuclei");
    while(b.hasRemaining()) {
      int x =   i % width ;
      int y =  (i / width  ) % width ;
      int z = ((i / width  ) / width  ) % width ;
      short value = 0;

      for(int k = 0; k < nucleiCount; k++) {
        int dx = x - centers.get(k*3);
        int dy = y - centers.get(k*3+1);
        int dz = z - centers.get(k*3+2);

        int radius = radii.get(k);

        if(dx*dx + dy*dy + dz*dz < radius*radius) {
          value = ((short) (x + y + z));
        }
      }
      b.put(value);
      i++;
    }

    b.flip();
    return b;
  }

  @Test
  public void getAPRTest() {
    final int width = 128;
    JavaAPR j = new JavaAPR();
    final ShortBuffer b = generateTestBuffer(width);

    System.out.println("Trying to get APR...");
    j.get16bitUnsignedAPR(width, width, width, 2, b);
    j.close();
  }

  @Test
  public void getAPRTestBDV() throws InterruptedException {
    final int width = 128;
    JavaAPR j = new JavaAPR();
    final ShortBuffer b = generateTestBuffer(width);

    System.out.println("Trying to get APR...");
    j.get16bitUnsignedAPR(width, width, width, 2, b);
    j.reconstruct();

    System.out.println("Showing APR in BDV...");
    BdvFullReconstruction bt = new BdvFullReconstruction();
    bt.init(j.data(), j.width(), j.height(), j.depth());
    bt.show();

    Thread.sleep(15000);

    j.close();
  }
}
