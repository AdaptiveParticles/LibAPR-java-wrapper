import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import mosaic.JavaAPR;
import net.imglib2.Cursor;
import net.imglib2.cache.img.CellLoader;
import net.imglib2.cache.img.DiskCachedCellImgFactory;
import net.imglib2.cache.img.DiskCachedCellImgOptions;
import net.imglib2.cache.img.SingleCellArrayImg;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.bytedeco.javacpp.ShortPointer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URL;

import static net.imglib2.cache.img.DiskCachedCellImgOptions.options;

public class BdvTest {
    ShortPointer data;
    int width, heigth, depth;
    
    public class FullImgLoader implements CellLoader< UnsignedShortType > {
        @Override
        public void load( final SingleCellArrayImg< UnsignedShortType, ? > cell ) throws Exception {
            Cursor<UnsignedShortType> cursor = cell.cursor();
            long[] pos = new long[cursor.numDimensions()];
            // copy data from LibAPR to BDV
            while(cursor.hasNext()) {
                cursor.fwd();
                cursor.localize(pos);
                cursor.get().set(data.get(pos[0] + pos[1] * width + pos[2] * width * heigth));
            }
        }
    }

    public void init(ShortPointer imgData, int w, int h, int d) {
        data = imgData;
        width = w;
        heigth = h;
        depth = d;
    }

    /**
     * Creates big data viewer with one big cell containing whole image (good enough for proof of concept)
     */
    public void show() {
        final int[] cellDimensions = new int[] { width, heigth, depth};
        final long[] dimensions   = new long[] { width, heigth, depth };

        final DiskCachedCellImgOptions options = options().cellDimensions( cellDimensions );
        final CellLoader< UnsignedShortType > loader = new FullImgLoader();
        final Img< UnsignedShortType > img = new DiskCachedCellImgFactory<>( new UnsignedShortType(), options ).create(dimensions,loader );

        BdvStackSource<UnsignedShortType> bdv = BdvFunctions.show( img, "APR TEST" );
//        bdv.setDisplayRange(0, 7000);
    }
    
    @Test public void BdvTest() throws FileNotFoundException {
        // "must be" print statement in any new software
        System.out.println("Hello from Java APR!");
        
        // ========================   Create APR =========================
        JavaAPR apr = new JavaAPR();
        
        // ========================   Load APR ===========================
//        String filename = "/Users/gonciarz/Documents/MOSAIC/work/repo/LibAPR/build/output_apr.h5";
        final URL resource = this.getClass().getResource("sphere_apr.h5");

        if(resource == null) {
            throw new FileNotFoundException("Could not find example file!");
        }

        String filename = resource.toString().substring(resource.toString().indexOf("file:/")+5);
        System.out.println("Loading [" + filename + "]");
        //apr.read(url.getPath());
        apr.read(filename);
        System.out.println("Img Size (w/h/d): " + apr.width() + "/" + apr.height() + "/" + apr.depth());

        // ========================   Start BDV ===========================        
        BdvTest bt = new BdvTest();
        bt.init(apr.data(), apr.width(), apr.height(), apr.depth());
        bt.show();
        
        // ========================   Destroy APR =========================
        apr.close();
    }
}
