package com.jambit.coffeeparty.clapyourhandsgame;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class ClapDetector extends Thread {

    public interface ClapDetectorListener {
        public void onClapDetected(double time);
    }

    private ClapDetectorListener listener;

    private long numSamplesRecorded = 0;
    private static int samplerate = 44100;
    private static int framesize = 1024;
    
    public ClapDetector(ClapDetectorListener listener)
    {
        super();
        this.listener = listener;

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    }

    @Override
    public void run() {
        int minSupportedBufferSize = AudioRecord.getMinBufferSize(samplerate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.d("clap", "minSupportedBufferSize=" + minSupportedBufferSize);
        
        int numFramesPerBuffer = minSupportedBufferSize / framesize;
        if (numFramesPerBuffer == 0)
            numFramesPerBuffer = 1;

        short samples[] = new short[numFramesPerBuffer * framesize];
        OnsetDetector onsetDetector = new OnsetDetector();

        AudioRecord recorder = new AudioRecord(AudioSource.MIC, samplerate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, samples.length);

        recorder.startRecording();

        while (!isInterrupted()) {
            int numSamplesRead = recorder.read(samples, 0, samples.length);
//            Log.d("clap", "numSamplesRead=" + numSamplesRead);

            int samplesToProcess = numSamplesRead;
            for (int i = 0; i < numFramesPerBuffer; i++)
            {
                if (samplesToProcess >= framesize)
                {
                    numSamplesRecorded += framesize;
                    boolean isOnset = onsetDetector.detect(samples, i * framesize);

                    if (isOnset) {
                        listener.onClapDetected((double) numSamplesRecorded / samplerate);
                    }
                }
                else
                {
                    break;
                }
                samplesToProcess -= framesize;
            }
        }

        recorder.stop();
    }
}
