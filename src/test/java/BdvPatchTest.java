import static net.imglib2.cache.img.DiskCachedCellImgOptions.options;

import java.io.FileNotFoundException;
import java.net.URL;

import org.bytedeco.javacpp.ShortPointer;
import org.junit.Test;

import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import mosaic.JavaAPR;
import net.imglib2.Cursor;
import net.imglib2.cache.img.CellLoader;
import net.imglib2.cache.img.DiskCachedCellImgFactory;
import net.imglib2.cache.img.DiskCachedCellImgOptions;
import net.imglib2.cache.img.DiskCachedCellImgOptions.CacheType;
import net.imglib2.cache.img.SingleCellArrayImg;
import net.imglib2.img.Img;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class BdvPatchTest {
    ShortPointer data;
    int width, heigth, depth;
    JavaAPR apr;
    
    public class PatchImgLoader implements CellLoader< UnsignedShortType > {
        @Override
        public synchronized void load( final SingleCellArrayImg< UnsignedShortType, ? > cell ) throws Exception {
            apr.reconstruct((int)cell.min(0), 
                            (int)cell.max(0), 
                            (int)cell.min(1), 
                            (int)cell.max(1), 
                            (int)cell.min(2), 
                            (int)cell.max(2));
            final int w = (int)(cell.dimension(0));
            final int h = (int)(cell.dimension(1));
            data=apr.data();
            
            Cursor<UnsignedShortType> cursor = cell.cursor();
            long[] pos = new long[cursor.numDimensions()];
            // copy data from LibAPR to BDV
            while(cursor.hasNext()) {
                cursor.fwd();
                cursor.localize(pos);
                cursor.get().set(data.get((pos[0] - cell.min(0)) + (pos[1] - cell.min(1)) * w + (pos[2] - cell.min(2)) * w * h));
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
        final int[] cellDimensions = new int[] { 65,65,65 };
        final long[] dimensions   = new long[] { width, heigth, depth };

        final DiskCachedCellImgOptions options = options()
                .cellDimensions(cellDimensions)
                .cacheType(CacheType.SOFTREF)
                .numIoThreads(1);

        final CellLoader< UnsignedShortType > loader = new PatchImgLoader();
        final Img< UnsignedShortType > img = new DiskCachedCellImgFactory<>(new UnsignedShortType(), options).create(dimensions, loader );
        
        BdvStackSource<UnsignedShortType> bdv = BdvFunctions.show( img, "APR TEST" );
//        bdv.setDisplayRange(0, 7000);
    }

    @Test public void testShowImg() throws FileNotFoundException {
        // "must be" print statement in any new software
        System.out.println("Hello from Java APR!");
        
        final URL resource = this.getClass().getResource("zebra.h5");
        if (resource == null) {
            throw new FileNotFoundException("Could not find example file!");
        }
        String filename = resource.toString().substring(resource.toString().indexOf("file:/")+5);
//        String filename = "/Users/gonciarz/Documents/MOSAIC/work/repo/LibAPR/build/output_apr.h5";
        
        // ========================   Create APR =========================
        apr = new JavaAPR();
        
        // ========================   Load APR ===========================
        System.out.println("Loading [" + filename + "]");
        apr.read(filename);
        System.out.println("Img Size (w/h/d): " + apr.width() + "/" + apr.height() + "/" + apr.depth());

        // ========================   Start BDV ===========================        
        init(apr.data(), apr.width(), apr.height(), apr.depth());
        show();
        try {Thread.sleep(15000);}catch (InterruptedException e) {e.printStackTrace();}
        // ========================   Destroy APR =========================
        apr.close();
    }
}
