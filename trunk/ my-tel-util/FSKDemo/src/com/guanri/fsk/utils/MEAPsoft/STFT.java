package com.guanri.fsk.utils.MEAPsoft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class STFT {
  AudioInputStream input;
  AudioFormat format;
  int bytesPerWavFrame, frameLen;
  ArrayList listeners = new ArrayList();
  double[] re, im, window;
  static double log10 = Math.log(10);
  static double epsilon = 1e-9;       // avoid log of zero
  RingMatrix freq, time;
  FFT fft;

  static double rmsTarget = 0.08;
  static double rmsAlpha = 0.001;
  double rms = 1;

  public float samplingRate;
  public int nhop;

  // The line should be open, but not started yet.
  public STFT(AudioInputStream input, int frameLen, int hopsize, int history) {
    this(input, frameLen, history);

    nhop = hopsize;
  }

  // The line should be open, but not started yet.
  public STFT(AudioInputStream input, int frameLen, int history) {
    freq = new RingMatrix(frameLen/2+1, history);
    time = new RingMatrix(frameLen, history);
    this.frameLen = frameLen;

    this.input = input;
    format = input.getFormat();
    bytesPerWavFrame = format.getFrameSize();

    samplingRate = format.getSampleRate();

    fft = new FFT(frameLen);

    this.re = new double[frameLen];
    this.im = new double[frameLen];
    this.window = fft.getWindow();
    for(int i=0; i<im.length; i++)
      im[i] = 0;

    nhop = frameLen;

    samplingRate = input.getFormat().getSampleRate();
  }

  // returns the total number of bytes read
  public long start() {
      byte[] b = new byte[bytesPerWavFrame * frameLen];
      Arrays.fill(b, (byte)0);

      int noverlapBytes = (frameLen-nhop)*bytesPerWavFrame;
      int nhopBytes = nhop*bytesPerWavFrame;

      int totalBytesRead = 0;
      int bytesRead = 22;
      while(bytesRead > 0) {
          if(nhop > 0)
          {
              // shift b so our overlap works out
              for(int x = 0; x < noverlapBytes; x++)
                  b[x] = b[x+nhopBytes];
          }

          try {
              bytesRead = input.read(b, noverlapBytes, nhopBytes);
              totalBytesRead += bytesRead;
          } catch(IOException ioe) {
              ioe.printStackTrace();
              return totalBytesRead;
          }
          
          // store the unwindowed waveform for getSamples function
          double[] wav = time.checkOutColumn();
          bytes2doubles(b, wav);
          
          // Normalize rms using a moving average estimate of it
          // Calculate current rms
          double rmsCur = 0;
          for(int i=0; i<wav.length; i++)
              rmsCur += wav[i]*wav[i];
          rmsCur = Math.sqrt(rmsCur / wav.length);
          
          // update moving average
          rms = rmsAlpha*rmsCur + (1-rmsAlpha)*rms;
          
          // normalize by rms
          for(int i=0; i<wav.length; i++)
              wav[i] = wav[i] * rmsTarget / rms;
          
          time.checkInColumn(wav);
          
          // window waveform
          for(int i=0; i<wav.length; i++)
              re[i] = window[i] * wav[i];
          
          // take fft
          fft.fft(re, im);
          
          // Calculate magnitude
          double[] mag = freq.checkOutColumn();
          for(int i=0; i<mag.length; i++)
              mag[i] = 10*Math.log(re[i]*re[i] + im[i]*im[i] + epsilon) / log10;
          
          // clear im[]
          Arrays.fill(im, 0);

          // Tell everyone concerned that we've added another frame
          long frAddr = freq.checkInColumn(mag);
          notifyListeners(frAddr);
      }

      // let the frame listeners know that we're done reading:
      notifyListeners(-1);
      
      return totalBytesRead;
  }
    

  // Get the waveform samples from frames frStart to frEnd-1
  public double[] getSamples(long frStart, long frEnd) {
    long sampStart = fr2Samp(frStart);
    long sampEnd = fr2Samp(frEnd);

    double[] x = new double[(int)(sampEnd - sampStart)];

    for(int fr=0; fr < frEnd-frStart; fr++) {
        double[] frame = time.getColumn(frStart+fr);
        if(frame == null) continue;
        // only the first nhop samples of frame are valid
        for(int i=0; i<nhop; i++)
            x[(int)(fr2Samp(fr+frStart)-sampStart + i)] = frame[i];
    }

    return x;
  }
  
  // Convert an address in frames into an address in samples
  public long fr2Samp(long frAddr) {
    return nhop * frAddr;
  }

  // Convert an address in samples into an address in frames
  public long samp2fr(long sampAddr) {
    return sampAddr/nhop;
  }

  public double[] getFrame(long frAddr) { return freq.getColumn(frAddr); }
  public void setFrame(long frAddr, double[] dat) { freq.setColumn(frAddr, dat); }
  public int getColumns() { return freq.getColumns(); }
  public int getRows() { return freq.getRows(); }

  // Dealing with FrameListeners
  public void addFrameListener(FrameListener fl) {
    listeners.add(fl);
  }
  public void removeFrameListener(FrameListener fl) {
    listeners.remove(fl);
  }
  public void notifyListeners(long frAddr) {
    for(int i=0; i<listeners.size(); i++) {
      FrameListener list = (FrameListener) listeners.get(i);
      list.newFrame(this, frAddr);
    }
  }


  // Convert a byte stream into a stream of doubles.  If it's stereo,
  // the channels will be interleaved with each other in the double
  // stream, as in the byte stream.
  public void bytes2doubles(byte[] audioBytes, double[] audioData) {
    if (format.getSampleSizeInBits() == 16) {
      if (format.isBigEndian()) {
        for (int i = 0; i < audioData.length; i++) {
          /* First byte is MSB (high order) */
          int MSB = (int) audioBytes[2*i];
          /* Second byte is LSB (low order) */
          int LSB = (int) audioBytes[2*i+1];
          audioData[i] = ((double)(MSB << 8 | (255 & LSB))) 
            / 32768.0;
        }
      } else {
        for (int i = 0; i < audioData.length; i++) {
          /* First byte is LSB (low order) */
          int LSB = (int) audioBytes[2*i];
          /* Second byte is MSB (high order) */
          int MSB = (int) audioBytes[2*i+1];
          audioData[i] = ((double)(MSB << 8 | (255 & LSB))) 
            / 32768.0;
        }
      }
    } else if (format.getSampleSizeInBits() == 8) {
      int nlengthInSamples = audioBytes.length;
      if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
        for (int i = 0; i < audioBytes.length; i++) {
          audioData[i] = audioBytes[i] / 128.0;
        }
      } else {
        for (int i = 0; i < audioBytes.length; i++) {
          audioData[i] = (audioBytes[i] - 128) / 128.0;
        }
      }
    }
  }

  // Convert an address in frames to an address in seconds
  public double fr2Seconds(long frAddr)
  {
      return(fr2Samp(frAddr)/samplingRate);
  }  

  // Convert an address in seconds to an address in frames
  public long seconds2fr(double sec)
  {
      return(samp2fr((long)(sec*samplingRate)));
  }  


  public static RingMatrix getSTFT(double[] samples, int nfft)
  {
      return STFT.getSTFT(samples, nfft, nfft);
  }

  public static RingMatrix getSTFT(double[] samples, int nfft, int nhop)
  {
      RingMatrix freq = new RingMatrix(nfft/2+1, samples.length/nhop);

      FFT fft = new FFT(nfft);
      double[] window = fft.getWindow();

      double[] wav = new double[nfft];
      double rms = 1;
      for(int currFrame = 0; currFrame < samples.length/nhop; currFrame++)
      {
          // zero pad if we run out of samples:
          int zeroPadLen = currFrame*nhop + wav.length - samples.length;
          if(zeroPadLen < 0)
              zeroPadLen = 0;
          int wavLen = wav.length - zeroPadLen;
          
          //for(int i = 0; i<wav.length; i++)
          //    wav[i] = samples[currFrame*nhop + i];
          for(int i = 0; i < wavLen; i++)
              wav[i] = samples[currFrame*nhop + i];
          for(int i = wavLen; i < wav.length; i++)
              wav[i] = 0;

          // Normalize rms using a moving average estimate of it
          // Calculate current rms
          double rmsCur = 0;
          for(int i=0; i<wav.length; i++)
              rmsCur += wav[i]*wav[i];
          rmsCur = Math.sqrt(rmsCur / wav.length);
      
          // update moving average
          rms = rmsAlpha*rmsCur + (1-rmsAlpha)*rms;
          
          // normalize by rms
          for(int i=0; i<wav.length; i++)
              wav[i] = wav[i] * rmsTarget / rms;
      
          // window waveform
          double[] re = new double[wav.length];
          double[] im = new double[wav.length];
          for(int i=0; i<wav.length; i++)
          {
              re[i] = window[i] * wav[i];
              im[i] = 0;
          }

          // take fft
          fft.fft(re, im);
          
          // Calculate magnitude
          double[] mag = freq.checkOutColumn();
          for(int i=0; i<mag.length; i++)
              mag[i] = 10*Math.log(re[i]*re[i] + im[i]*im[i] + epsilon) / log10;

          freq.checkInColumn(mag);
      }  

      return freq;
  }


  public int readFrames(long nframes) 
  {
      byte[] b = new byte[bytesPerWavFrame * frameLen];
      Arrays.fill(b, (byte)0);

      int noverlapBytes = (frameLen-nhop)*bytesPerWavFrame;
      int nhopBytes = nhop*bytesPerWavFrame;

      int bytesRead = 22;
      int nFramesRead = 0;
      while(nFramesRead <= nframes)
      {
          if(nhop > 0)
          {
              // shift b so our overlap works out
              for(int x = 0; x < noverlapBytes; x++)
                  b[x] = b[x+nhopBytes];
          }

          try 
          { 
              input.read(b, noverlapBytes, nhopBytes); 
              nFramesRead++;
          }
          catch(IOException ioe) 
          { 
              ioe.printStackTrace(); 
              return nFramesRead;
          }
          
          // store the unwindowed waveform for getSamples function
          double[] wav = time.checkOutColumn();
          bytes2doubles(b, wav);
          
          // Normalize rms using a moving average estimate of it
          // Calculate current rms
          double rmsCur = 0;
          for(int i=0; i<wav.length; i++)
              rmsCur += wav[i]*wav[i];
          rmsCur = Math.sqrt(rmsCur / wav.length);
          
          // update moving average
          rms = rmsAlpha*rmsCur + (1-rmsAlpha)*rms;
          
          // normalize by rms
          for(int i=0; i<wav.length; i++)
              wav[i] = wav[i] * rmsTarget / rms;
          
          time.checkInColumn(wav);
          
          // window waveform
          for(int i=0; i<wav.length; i++)
              re[i] = window[i] * wav[i];
          
          // take fft
          fft.fft(re, im);
          
          // Calculate magnitude
          double[] mag = freq.checkOutColumn();
          for(int i=0; i<mag.length; i++)
              mag[i] = 10*Math.log(re[i]*re[i] + im[i]*im[i] + epsilon) / log10;
          
          // clear im[]
          Arrays.fill(im, 0);

          // Tell everyone concerned that we've added another frame
          long frAddr = freq.checkInColumn(mag);
          notifyListeners(frAddr);
      }

      return nFramesRead;
  }    

  public long getLastFrameAddress()
  {
      return freq.nextFrAddr-1;
  } 

  public void stop() throws IOException
  {
      input.close();
  }
}

