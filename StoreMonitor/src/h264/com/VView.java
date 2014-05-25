package h264.com;

public class VView {


	
	/**
	 * 初始化解码器
	 * @param width
	 * @param height
	 * @return
	 */
    public native int InitDecoder(int width, int height);
    
    /**
     * 
     * @return
     */
    public native int UninitDecoder(); 
    
    /**
     * 对Nal进行解码
     * @param in
     * @param insize
     * @param out
     * @return
     */
    public native int DecoderNal(byte[] in, int insize, byte[] out);
    
    static {
        System.loadLibrary("H264Android_CPP");
    }

}
