/**
 * Copyright @ 2012 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import net.sourceforge.vietocr.ImageIOHelper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class TessAPITest {

    String datapath = "tessdata";
    String language = "eng";
    String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";

    public TessAPITest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of TessBaseAPIRect method, of class TessDllLibrary.
     */
    @Test
    public void testTessBaseAPIRect() throws Exception {
        System.out.println("TessBaseAPIRect");
        String expResult = expOCRResult;
        String lang = "eng";
        File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(tiff); // require jai-imageio lib to read TIFF
//        MappedByteBuffer buf = new FileInputStream(tiff).getChannel().map(MapMode.READ_ONLY, 0, tiff.length());
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPISetPageSegMode(handle, TessAPI.TessPageSegMode.PSM_AUTO);
        api.TessBaseAPIInit3(handle, "tessdata", lang);
        String utf8Text = api.TessBaseAPIRect(handle, buf, bytespp, bytespl, 90, 50, 862, 614);
        String result = new String(utf8Text.getBytes(), "utf8");
        System.out.println(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessDllLibrary.
     */
    @Test
    public void testTessBaseAPIGetUTF8Text() throws Exception {
        System.out.println("TessBaseAPIGetUTF8Text");
        String expResult = expOCRResult;
        String lang = "eng";
        File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
//        MappedByteBuffer buf = new FileInputStream(tiff).getChannel().map(MapMode.READ_ONLY, 0, tiff.length());
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPISetPageSegMode(handle, TessAPI.TessPageSegMode.PSM_AUTO);
        api.TessBaseAPIInit3(handle, "tessdata", lang);
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        api.TessBaseAPISetRectangle(handle, 90, 50, 862, 614);
        String utf8Text = api.TessBaseAPIGetUTF8Text(handle);
        String result = new String(utf8Text.getBytes(), "utf8");
        System.out.println(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessVersion method, of class TessAPI.
     */
    @Test
    public void testTessVersion() {
        System.out.println("TessVersion");
        TessAPI instance = new TessDllAPIImpl().getInstance();
        String expResult = "3.02";
        String result = instance.TessVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPICreate method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPICreate() {
        System.out.println("TessBaseAPICreate");
        TessAPI api = new TessDllAPIImpl().getInstance();
        assertNotNull(api);
    }

    /**
     * Test of TessBaseAPIDelete method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIDelete() {
        System.out.println("TessBaseAPIDelete");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPIDelete(handle);
    }

    /**
     * Test of TessBaseAPISetInputName method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetInputName() {
        System.out.println("TessBaseAPISetInputName");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String name = "eurotext.tif";
        api.TessBaseAPISetInputName(handle, name);
    }

    /**
     * Test of TessBaseAPISetOutputName method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetOutputName() {
        System.out.println("TessBaseAPISetOutputName");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String name = "out";
        api.TessBaseAPISetOutputName(handle, name);
    }

    /**
     * Test of TessBaseAPISetVariable method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetVariable() {
        System.out.println("TessBaseAPISetVariable");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String name = "tessedit_create_hocr";
        String value = "1";
        int expResult = 1;
        int result = api.TessBaseAPISetVariable(handle, name, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetBoolVariable method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetBoolVariable() {
        System.out.println("TessBaseAPIGetBoolVariable");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String name = "tessedit_create_hocr";
        api.TessBaseAPISetVariable(handle, name, "1");
        IntBuffer value = IntBuffer.allocate(1);
        int result = -1;
        if (api.TessBaseAPIGetBoolVariable(handle, "tessedit_create_hocr", value) == TessAPI.TRUE) {
            result = value.get(0);
        }
        int expResult = 1;
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIPrintVariables method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIPrintVariables() throws Exception {
        System.out.println("TessBaseAPIPrintVariables");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String var = "tessedit_char_whitelist";
        String value = "0123456789";
        api.TessBaseAPISetVariable(handle, var, value);
        String filename = "printvar.txt";
        api.TessBaseAPIPrintVariables(handle, filename);  // will crash if not invoked after some method
        File file = new File(filename);
        BufferedReader input = new BufferedReader(new FileReader(file));
        StringBuilder strB = new StringBuilder();
        String line = null;
        String EOL = System.getProperty("line.separator");
        while ((line = input.readLine()) != null) {
            strB.append(line).append(EOL);
        }
        input.close();
        file.delete();
        assertTrue(strB.toString().contains(var + "\t" + value));
    }

    /**
     * Test of TessBaseAPIInit1 method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIInit1() {
        System.out.println("TessBaseAPIInit1");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        int oem = TessAPI.TessOcrEngineMode.OEM_DEFAULT;
        PointerByReference configs = null;
        int configs_size = 0;
        int expResult = 0;
        int result = api.TessBaseAPIInit1(handle, datapath, language, oem, configs, configs_size);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit2 method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIInit2() {
        System.out.println("TessBaseAPIInit2");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        int oem = TessAPI.TessOcrEngineMode.OEM_DEFAULT;
        int expResult = 0;
        int result = api.TessBaseAPIInit2(handle, datapath, language, oem);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit3 method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIInit3() {
        System.out.println("TessBaseAPIInit3");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        int expResult = 0;
        int result = api.TessBaseAPIInit3(handle, datapath, language);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetInitLanguagesAsString method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetInitLanguagesAsString() {
        System.out.println("TessBaseAPIGetInitLanguagesAsString");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String expResult = "";
        String result = api.TessBaseAPIGetInitLanguagesAsString(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPISetPageSegMode method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetPageSegMode() {
        System.out.println("TessBaseAPISetPageSegMode");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        int mode = TessAPI.TessPageSegMode.PSM_AUTO;
        api.TessBaseAPISetPageSegMode(handle, mode);
    }

    /**
     * Test of TessBaseAPIGetPageSegMode method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetPageSegMode() {
        System.out.println("TessBaseAPIGetPageSegMode");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPISetPageSegMode(handle, TessAPI.TessPageSegMode.PSM_AUTO);
        int expResult = TessAPI.TessPageSegMode.PSM_AUTO;
        int result = api.TessBaseAPIGetPageSegMode(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPISetImage method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetImage() {
        System.out.println("TessBaseAPISetImage");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        ByteBuffer imagedata = null;
        int width = 0;
        int height = 0;
        int bytes_per_pixel = 0;
        int bytes_per_line = 0;
        api.TessBaseAPISetImage(handle, imagedata, width, height, bytes_per_pixel, bytes_per_line);
    }

    /**
     * Test of TessBaseAPISetRectangle method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetRectangle() {
        System.out.println("TessBaseAPISetRectangle");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        int left = 0;
        int top = 0;
        int width = 0;
        int height = 0;
        api.TessBaseAPISetRectangle(handle, left, top, width, height);
    }

    /**
     * Test of TessBaseAPIProcessPages method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIProcessPages() {
        System.out.println("TessBaseAPIProcessPages");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        String filename = "eurotext.tif";
        String retry_config = null;
        int timeout_millisec = 0;
        api.TessBaseAPIInit3(handle, datapath, language);
        String expResult = expOCRResult;
        String result = api.TessBaseAPIProcessPages(handle, filename, retry_config, timeout_millisec);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetHOCRText method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetHOCRText() throws Exception {
        System.out.println("TessBaseAPIGetHOCRText");

        File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        MappedByteBuffer buf = new FileInputStream(tiff).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, tiff.length());
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPISetPageSegMode(handle, TessAPI.TessPageSegMode.PSM_AUTO);
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int page_number = 0;
        String utf8Text = api.TessBaseAPIGetHOCRText(handle, page_number);
        String result = new String(utf8Text.getBytes(), "utf8");
        assertTrue(result.contains("<div class='ocr_page'"));
    }

    /**
     * Test of TessBaseAPIClear method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIClear() {
        System.out.println("TessBaseAPIClear");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPIClear(handle);
    }

    /**
     * Test of TessBaseAPIEnd method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIEnd() {
        System.out.println("TessBaseAPIEnd");
        TessAPI api = new TessDllAPIImpl().getInstance();
        TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
        api.TessBaseAPIEnd(handle);
    }

    public class TessDllAPIImpl implements TessAPI {

        public TessAPI getInstance() {
            return INSTANCE;
        }

        public void TessDllEndPage() {
        }

        public void TessDllRelease() {
        }

        public boolean SetVariable(String variable, String value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void SimpleInit(String datapath, String language, boolean numeric_mode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int Init(String datapath, String outputbase, String configfile, boolean numeric_mode, int argc, String[] argv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int InitWithLanguage(String datapath, String outputbase, String language, String configfile, boolean numeric_mode, int argc, String[] argv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int InitLangMod(String datapath, String outputbase, String language, String configfile, boolean numeric_mode, int argc, String[] argv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void SetInputName(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TesseractRect(ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TesseractRectBoxes(ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height, int imageheight) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TesseractRectUNLV(ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void ClearAdaptiveClassifier() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void End() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void DumpPGM(String filename) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Image GetTesseractImage() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int OtsuStats(int histogram, int H_out, int omega0_out) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int IsValidWord(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessVersion() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessDeleteText(String text) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessDeleteIntArray(IntBuffer arr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessBaseAPI TessBaseAPICreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIDelete(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetInputName(TessBaseAPI handle, String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetOutputName(TessBaseAPI handle, String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPISetVariable(TessBaseAPI handle, String name, String value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIGetIntVariable(TessBaseAPI handle, String name, IntBuffer value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIGetBoolVariable(TessBaseAPI handle, String name, IntBuffer value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIGetDoubleVariable(TessBaseAPI handle, String name, DoubleBuffer value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetStringVariable(TessBaseAPI handle, String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIPrintVariables(TessBaseAPI handle, String filename) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIInit1(TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIInit2(TessBaseAPI handle, String datapath, String language, int oem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIInit3(TessBaseAPI handle, String datapath, String language) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIInitLangMod(TessBaseAPI handle, String datapath, String language) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIInitForAnalysePage(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIReadConfigFile(TessBaseAPI handle, String filename, int init_only) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetPageSegMode(TessBaseAPI handle, int mode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIGetPageSegMode(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIRect(TessBaseAPI handle, ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIClearAdaptiveClassifier(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetImage(TessBaseAPI handle, ByteBuffer imagedata, int width, int height, int bytes_per_pixel, int bytes_per_line) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetRectangle(TessBaseAPI handle, int left, int top, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIDumpPGM(TessBaseAPI handle, String filename) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessPageIterator TessBaseAPIAnalyseLayout(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIRecognize(TessBaseAPI handle, ETEXT_DESC monitor) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIRecognizeForChopTest(TessBaseAPI handle, ETEXT_DESC monitor) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessResultIterator TessBaseAPIGetIterator(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIProcessPages(TessBaseAPI handle, String filename, String retry_config, int timeout_millisec) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetUTF8Text(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetHOCRText(TessBaseAPI handle, int page_number) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetBoxText(TessBaseAPI handle, int page_number) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetUNLVText(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIMeanTextConf(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public IntByReference TessBaseAPIAllWordConfidences(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIAdaptToWordStr(TessBaseAPI handle, int mode, String wordstr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIClear(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPIEnd(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIIsValidWord(TessBaseAPI handle, String word) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessBaseAPIGetTextDirection(TessBaseAPI handle, IntBuffer out_offset, FloatBuffer out_slope) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetUnichar(TessBaseAPI handle, int unichar_id) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseGetInitLanguagesAsString(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetMinOrientationMargin(TessBaseAPI handle, double margin) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessPageIteratorDelete(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessPageIterator TessPageIteratorCopy(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessPageIteratorBegin(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessPageIteratorNext(TessPageIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessPageIteratorIsAtBeginningOf(TessPageIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessPageIteratorIsAtFinalElement(TessPageIterator handle, int level, int element) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessPageIteratorBoundingBox(TessPageIterator handle, int level, IntBuffer left, IntBuffer top, IntBuffer right, IntBuffer bottom) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessPageIteratorBlockType(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessPageIteratorBaseline(TessPageIterator handle, int level, IntBuffer x1, IntBuffer y1, IntBuffer x2, IntBuffer y2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessPageIteratorOrientation(TessPageIterator handle, IntBuffer orientation, IntBuffer writing_direction, IntBuffer textline_order, FloatBuffer deskew_angle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessResultIteratorDelete(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessResultIterator TessResultIteratorCopy(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessPageIterator TessResultIteratorGetPageIterator(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TessPageIterator TessResultIteratorGetPageIteratorConst(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessResultIteratorGetUTF8Text(TessResultIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public float TessResultIteratorConfidence(TessResultIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessResultIteratorWordFontAttributes(TessResultIterator handle, IntBuffer is_bold, IntBuffer is_italic, IntBuffer is_underlined, IntBuffer is_monospace, IntBuffer is_serif, IntBuffer is_smallcaps, IntBuffer pointsize, IntBuffer font_id) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessResultIteratorWordIsFromDictionary(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessResultIteratorWordIsNumeric(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessResultIteratorSymbolIsSuperscript(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessResultIteratorSymbolIsSubscript(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int TessResultIteratorSymbolIsDropcap(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String TessBaseAPIGetInitLanguagesAsString(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void TessBaseAPISetSourceResolution(TessBaseAPI handle, int ppi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
