
#define MYLIBDLL extern "C" _declspec(dllexport)

extern "C" {
#include <libavutil/opt.h>
#include <libavcodec/avcodec.h>
#include <libavutil/channel_layout.h>
#include <libavutil/common.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>
#include <libavutil/samplefmt.h>
#include <libavformat/avformat.h>
 }
#include <stdlib.h>
#include <vector>
#include <iostream>

#include <string.h>
#include <jni.h>
#include <stdio.h>
#include "jniUtils.h"

const int INBUF_SIZE=65535;
class H264Decoder
{
		private:
			int inWidth,inHeight,outHeight,outWidth;
			AVCodecContext *c;
			int frame_count;
			AVFrame *picture;
			AVCodec *codec;
			uint8_t inbuf[INBUF_SIZE + FF_INPUT_BUFFER_PADDING_SIZE];
			AVPacket avpkt;


			int *colortab;
			int *u_b_tab;
			int *u_g_tab;
			int *v_g_tab;
			int *v_r_tab;

			unsigned int *rgb_2_pix;
			unsigned int *r_2_pix;
			unsigned int *g_2_pix;
			unsigned int *b_2_pix;

public :
			int decoderImage(uint8_t *source,int ssize,uint8_t *desig,int* picInfo);
			int setImageWH(int* picInfo);
			int decoderCreate(int inw,int inh,int outw,int outh,int id);
			void releaseData();
			void createYUVTab_16();
			void displayYUV_16(unsigned int *pdst1, unsigned char *y, unsigned char *u, unsigned char *v, int width, int height, int src_ystride, int src_uvstride, int dst_ystride);
			H264Decoder();
			~H264Decoder();
	};

	H264Decoder::H264Decoder()
	{
	}
	H264Decoder::~H264Decoder()
	{
	}
	void H264Decoder::releaseData()
	{
	//	av_free(tmp_pic);

		av_free(colortab);
		av_free(rgb_2_pix);
		avpkt.data = NULL;
		avpkt.size = 0;
		if (c)
		{
    		avcodec_close(c);
			av_free(c);
		}
	}

	void H264Decoder::createYUVTab_16()
	{
		int i;
		int u, v;

	//	tmp_pic = (short*)av_malloc(iWidth*iHeight*2); // ???? iWidth * iHeight * 16bits

		colortab = (int *)av_malloc(4*256*sizeof(int));
		u_b_tab = &colortab[0*256];
		u_g_tab = &colortab[1*256];
		v_g_tab = &colortab[2*256];
		v_r_tab = &colortab[3*256];

		for (i=0; i<256; i++)
		{
			u = v = (i-128);

			u_b_tab[i] = (int) ( 1.772 * u);
			u_g_tab[i] = (int) ( 0.34414 * u);
			v_g_tab[i] = (int) ( 0.71414 * v);
			v_r_tab[i] = (int) ( 1.402 * v);
		}

		rgb_2_pix = (unsigned int *)av_malloc(3*768*sizeof(unsigned int));

		r_2_pix = &rgb_2_pix[0*768];
		g_2_pix = &rgb_2_pix[1*768];
		b_2_pix = &rgb_2_pix[2*768];

		for(i=0; i<256; i++)
		{
			r_2_pix[i] = 0;
			g_2_pix[i] = 0;
			b_2_pix[i] = 0;
		}

		for(i=0; i<256; i++)
		{
			r_2_pix[i+256] = (i & 0xF8) << 8;
			g_2_pix[i+256] = (i & 0xFC) << 3;
			b_2_pix[i+256] = (i ) >> 3;
		}

		for(i=0; i<256; i++)
		{
			r_2_pix[i+512] = 0xF8 << 8;
			g_2_pix[i+512] = 0xFC << 3;
			b_2_pix[i+512] = 0x1F;
		}

		r_2_pix += 256;
		g_2_pix += 256;
		b_2_pix += 256;
	}

	void H264Decoder::displayYUV_16(unsigned int *pdst1, unsigned char *y, unsigned char *u,
			unsigned char *v, int width, int height, int src_ystride, int src_uvstride, int dst_ystride)
	{
		int i, j;
		int r, g, b, rgb;

		int yy, ub, ug, vg, vr;

		unsigned char* yoff;
		unsigned char* uoff;
		unsigned char* voff;

		unsigned int* pdst=pdst1;

		int width2 = width/2;
		int height2 = height/2;

		if(width2>outWidth/2)
		{
			width2=outWidth/2;

			y+=(width-outWidth)/4*2;
			u+=(width-outWidth)/4;
			v+=(width-outWidth)/4;
		}

		if(height2>outHeight)
			height2=outHeight;

		for(j=0; j<height2; j++)
		{
			yoff = y + j * 2 * src_ystride;
			uoff = u + j * src_uvstride;
			voff = v + j * src_uvstride;

			for(i=0; i<width2; i++)
			{
				yy  = *(yoff+(i<<1));
				ub = u_b_tab[*(uoff+i)];
				ug = u_g_tab[*(uoff+i)];
				vg = v_g_tab[*(voff+i)];
				vr = v_r_tab[*(voff+i)];

				b = yy + ub;
				g = yy - ug - vg;
				r = yy + vr;

				rgb = r_2_pix[r] + g_2_pix[g] + b_2_pix[b];

				yy = *(yoff+(i<<1)+1);
				b = yy + ub;
				g = yy - ug - vg;
				r = yy + vr;

				pdst[(j*dst_ystride+i)] = (rgb)+((r_2_pix[r] + g_2_pix[g] + b_2_pix[b])<<16);

				yy = *(yoff+(i<<1)+src_ystride);
				b = yy + ub;
				g = yy - ug - vg;
				r = yy + vr;

				rgb = r_2_pix[r] + g_2_pix[g] + b_2_pix[b];

				yy = *(yoff+(i<<1)+src_ystride+1);
				b = yy + ub;
				g = yy - ug - vg;
				r = yy + vr;

				pdst [((2*j+1)*dst_ystride+i*2)>>1] = (rgb)+((r_2_pix[r] + g_2_pix[g] + b_2_pix[b])<<16);
			}
		}
	}

	int H264Decoder::decoderImage(uint8_t *source,int ssize,uint8_t *desig,int* picInfo)
	{
		//LOGE("DecoderImage ssize=%d",ssize);
		avpkt.size = ssize;
		avpkt.data = source;
		int len;
		int got_picture;
		int nx=0;
		while (avpkt.size > 0) 
		{
			len = avcodec_decode_video2(c, picture, &got_picture, &avpkt);

			//LOGE("avcodec_decode_video2=%d",len);

			if (len < 0) 
			{
				break;
			}
			if (got_picture) 
			{

				displayYUV_16((unsigned int*)desig, picture->data[0], picture->data[1], picture->data[2], c->width, c->height, picture->linesize[0], picture->linesize[1], outWidth);
				//LOGE("avcodec_decode_video2 c->width=%d,c->height=%d",picInfo[0],picInfo[1]);

				picInfo[0] = c->width;
				picInfo[1] = c->height;
				outWidth = picInfo[0];
				outHeight = picInfo[1];

				nx=outWidth*outHeight*3;
               
			}
			avpkt.size -= len;
			avpkt.data += len;
		}
		return nx;
	} 
	


	int H264Decoder::setImageWH(int* picInfo){
	}
		
	int H264Decoder::decoderCreate(int inw,int inh,int outw,int outh,int id)
	{
		outWidth=outw;
		outHeight=outh;
		// Register all formats and codecs
		av_register_all();
		av_init_packet(&avpkt);
	    /* set end of buffer to 0 (this ensures that no overreading happens for damaged mpeg streams) */
	    memset(inbuf + INBUF_SIZE, 0, FF_INPUT_BUFFER_PADDING_SIZE);

		codec = avcodec_find_decoder(CODEC_ID_H264);

		if (!codec) {
			LOGE("codec not foundn");
			return -5;
		}
 
		c = avcodec_alloc_context3(codec);
	    if (!c) {
	    	LOGE("Could not allocate video codec context\n");
	        return -7;
	    }
		
		c->codec_type = AVMEDIA_TYPE_VIDEO;
		c->bit_rate = 0;
		c->time_base.den = 10;
		c->width = outw;
		c->height = outh;
		LOGE("c->width=%d,c->height=%d",c->width,c->height);
		c->time_base.num = 1; 

        c->time_base.den = 25;
		
		if (avcodec_open2(c, codec, NULL) < 0) 
		{
		    LOGE("could not open codecn");
			return -6;
		}

		picture= avcodec_alloc_frame();
		createYUVTab_16();

		return JNI_TRUE;
	} 
	

	static jint InitDecoder(JNIEnv* env, jobject thiz, jint width, jint height)
	{
		H264Decoder* mPtr = new H264Decoder();
		if(mPtr->decoderCreate(0,0,(int)width,(int)height,0)!=JNI_TRUE){
			mPtr->releaseData();
			mPtr->~H264Decoder();
			return 0;
		}
		return (jint)mPtr;
	}

	/*
	 * Class:     h264_com_VView
	 * Method:    UninitDecoder
	 * Signature: ()I
	 */
	static jint UninitDecoder(JNIEnv* env, jobject thiz,jint mPtr)
	{
		H264Decoder* decoder = (H264Decoder*)mPtr;
		decoder->releaseData();
		return (jint)1;
	}

	/*
	 * Method:    DecoderNal
	 * Signature: ([B[I)I
	 */
	static jint DecoderNal(JNIEnv* env, jobject thiz,jint mPtr, jbyteArray in, jint nalLen, jbyteArray out,jintArray picInfo)
	{
		H264Decoder* decoder = (H264Decoder*)mPtr;
		uint8_t *Buf = (uint8_t *)(env)->GetByteArrayElements(in, 0);
		uint8_t *Pixel = (uint8_t *)(env)->GetByteArrayElements(out, 0);
		jint *picWH = (int *)(env)->GetIntArrayElements(picInfo, 0);

		int picLen = decoder->decoderImage(Buf,(int)nalLen,Pixel,(int *)picWH);

	    (env)->ReleaseByteArrayElements(in, (jbyte *)Buf, 0);
	    (env)->ReleaseByteArrayElements(out, (jbyte *)Pixel, 0);
	    (env)->ReleaseIntArrayElements(picInfo, (jint *)picWH, 0);
		return (jint)picLen;
	}

	static jint SetImageParams(JNIEnv* env, jobject thiz,jint mPtr, jintArray picInfo)
	{
		jint *picWH = (int *)(env)->GetIntArrayElements(picInfo, 0);
		H264Decoder* decoder = (H264Decoder*)mPtr;
		int picLen = decoder->setImageWH((int *)picWH);

	    (env)->ReleaseIntArrayElements(picInfo, (jint *)picWH, 0);
		return (jint)picLen;
	}
	/*
	* JNI registration.
	*/
	static JNINativeMethod methods[] = {
		{ "InitDecoder", "(II)I",  (void*) InitDecoder },
		{ "UninitDecoder",      "(I)I",  (void*) UninitDecoder },
		{ "DecoderNal", "(I[BI[B[I)I", (void*) DecoderNal},
		{ "SetImageParams", "(I[I)I", (void*) SetImageParams},
	};

	int register_JNI_function(JNIEnv *env) {
		int res = jniRegisterNativeMethods(env, "com/ffmpeg/lib/h264/NativeH264Decoder", methods, sizeof(methods) / sizeof(methods[0]));
		LOGE("jniRegisterNativeMethods=%d",res);
		return res;

	}

