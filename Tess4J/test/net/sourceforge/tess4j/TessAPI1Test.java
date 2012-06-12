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

import com.sun.jna.ptr.PointerByReference;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel.MapMode;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.TessAPI1.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TessAPI1Test {

    String datapath = "tessdata";
    String language = "eng";
    String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";

    public TessAPI1Test() {
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
     * Test of TessBaseAPIRect method, of class TessDllAPI1.
     */
    @Test
    public void testTessBaseAPIRect() throws Exception {
        System.out.println("TessBaseAPIRect");
        String expResult = expOCRResult;
        String lang = "eng";
        File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(tiff); // require jai-imageio lib to read TIFF
        MappedByteBuffer buf = new FileInputStream(tiff).getChannel().map(MapMode.READ_ONLY, 0, tiff.length());
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPIInit3(handle, "tessdata", lang);
        String utf8Text = TessAPI1.TessBaseAPIRect(handle, buf, bytespp, bytespl, 0, 0, 1024, 800);
        String result = new String(utf8Text.getBytes(), "utf8");
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessDllAPI1.
     */
    @Test
    public void testTessBaseAPIGetUTF8Text() throws Exception {
        System.out.println("TessBaseAPIGetUTF8Text");
        String expResult = expOCRResult;
        String lang = "eng";
        File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        MappedByteBuffer buf = new FileInputStream(tiff).getChannel().map(MapMode.READ_ONLY, 0, tiff.length());
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPIInit3(handle, "tessdata", lang);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPISetRectangle(handle, 0, 0, 1024, 800);
        String utf8Text = TessAPI1.TessBaseAPIGetUTF8Text(handle);
        String result = new String(utf8Text.getBytes(), "utf8");
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessVersion method, of class TessAPI1.
     */
    @Test
    public void testTessVersion() {
        System.out.println("TessVersion");
        String expResult = "3.02";
        String result = TessAPI1.TessVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPICreate method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPICreate() {
        System.out.println("TessBaseAPICreate");
        TessBaseAPI result = TessAPI1.TessBaseAPICreate();
        assertNotNull(result);
    }

    /**
     * Test of TessBaseAPIDelete method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIDelete() {
        System.out.println("TessBaseAPIDelete");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPIDelete(handle);
    }

    /**
     * Test of TessBaseAPISetInputName method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetInputName() {
        System.out.println("TessBaseAPISetInputName");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String name = "eurotext.tif";
        TessAPI1.TessBaseAPISetInputName(handle, name);
    }

    /**
     * Test of TessBaseAPISetOutputName method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetOutputName() {
        System.out.println("TessBaseAPISetOutputName");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String name = "out";
        TessAPI1.TessBaseAPISetOutputName(handle, name);
    }

    /**
     * Test of TessBaseAPISetVariable method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetVariable() {
        System.out.println("TessBaseAPISetVariable");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String name = "tessedit_create_hocr";
        String value = "1";
        int expResult = 1;
        int result = TessAPI1.TessBaseAPISetVariable(handle, name, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetBoolVariable method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetBoolVariable() {
        System.out.println("TessBaseAPIGetBoolVariable");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String name = "tessedit_create_hocr";
        TessAPI1.TessBaseAPISetVariable(handle, name, "1");
        IntBuffer value = IntBuffer.allocate(1);
        int result = -1;
        if (TessAPI1.TessBaseAPIGetBoolVariable(handle, "tessedit_create_hocr", value) == TessAPI1.TRUE) {
            result = value.get(0);
        }
        int expResult = 1;
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIPrintVariables method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIPrintVariables() throws Exception {
        System.out.println("TessBaseAPIPrintVariables");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String var = "tessedit_char_whitelist";
        String value = "0123456789";
        TessAPI1.TessBaseAPISetVariable(handle, var, value);
        String filename = "printvar.txt";
        TessAPI1.TessBaseAPIPrintVariables(handle, filename); // will crash if not invoked after some method
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
     * Test of TessBaseAPIInit1 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit1() {
        System.out.println("TessBaseAPIInit1");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        PointerByReference configs = null;
        int configs_size = 0;
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit1(handle, datapath, language, oem, configs, configs_size);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit2 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit2() {
        System.out.println("TessBaseAPIInit2");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit2(handle, datapath, language, oem);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit3 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit3() {
        System.out.println("TessBaseAPIInit3");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetInitLanguagesAsString method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetInitLanguagesAsString() {
        System.out.println("TessBaseAPIGetInitLanguagesAsString");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String expResult = "";
        String result = TessAPI1.TessBaseAPIGetInitLanguagesAsString(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPISetPageSegMode method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetPageSegMode() {
        System.out.println("TessBaseAPISetPageSegMode");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        int mode = TessPageSegMode.PSM_AUTO;
        TessAPI1.TessBaseAPISetPageSegMode(handle, mode);
    }

    /**
     * Test of TessBaseAPIGetPageSegMode method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetPageSegMode() {
        System.out.println("TessBaseAPIGetPageSegMode");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        int expResult = TessPageSegMode.PSM_AUTO;
        int result = TessAPI1.TessBaseAPIGetPageSegMode(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPISetImage method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetImage() {
        System.out.println("TessBaseAPISetImage");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        ByteBuffer imagedata = null;
        int width = 0;
        int height = 0;
        int bytes_per_pixel = 0;
        int bytes_per_line = 0;
        TessAPI1.TessBaseAPISetImage(handle, imagedata, width, height, bytes_per_pixel, bytes_per_line);
    }

    /**
     * Test of TessBaseAPISetRectangle method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetRectangle() {
        System.out.println("TessBaseAPISetRectangle");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        int left = 0;
        int top = 0;
        int width = 0;
        int height = 0;
        TessAPI1.TessBaseAPISetRectangle(handle, left, top, width, height);
    }

    /**
     * Test of TessBaseAPIProcessPages method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIProcessPages() {
        System.out.println("TessBaseAPIProcessPages");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        String filename = "eurotext.tif";
        String retry_config = null;
        int timeout_millisec = 0;
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        String expResult = expOCRResult;
        String result = TessAPI1.TessBaseAPIProcessPages(handle, filename, retry_config, timeout_millisec);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetHOCRText method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetHOCRText() throws Exception {
        System.out.println("TessBaseAPIGetHOCRText");

        File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        MappedByteBuffer buf = new FileInputStream(tiff).getChannel().map(MapMode.READ_ONLY, 0, tiff.length());
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int page_number = 0;
        String utf8Text = TessAPI1.TessBaseAPIGetHOCRText(handle, page_number);
        String result = new String(utf8Text.getBytes(), "utf8");
        assertTrue(result.contains("<div class='ocr_page'"));
    }

    /**
     * Test of TessBaseAPIClear method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIClear() {
        System.out.println("TessBaseAPIClear");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPIClear(handle);
    }

    /**
     * Test of TessBaseAPIEnd method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIEnd() {
        System.out.println("TessBaseAPIEnd");
        TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPIEnd(handle);
    }
}
