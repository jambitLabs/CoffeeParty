package com.jambit.coffeeparty.clapyourhandsgame;

import java.util.LinkedList;

import android.util.Log;

// As described in algorithm "1b" at
// http://archive.gamedev.net/archive/reference/programming/features/beatdetection/index.html
public class OnsetDetector {

//    private static double SENSITIVITY = 1.0;
//    private static int MINFRAMESBETWEENONSETS = 2;
    private static double SENSITIVITY = 1.2;
    private static int MINFRAMESBETWEENONSETS = 1;
    private static int HISTORYSIZE = 10;

    LinkedList<Long> energyHistory = new LinkedList<Long>();
    long accumulatedEnergy = 0;
    int framesSinceLastOnset = 0;
    
    public boolean detect(short[] monoSamples, int frameStart)
    {
        long instantEnergy = computeInstantEnergy(monoSamples, frameStart);

        long localAverageEnergy = accumulatedEnergy / HISTORYSIZE;
        while (energyHistory.size() >= HISTORYSIZE)
        {
            long lastEnergy = energyHistory.poll();
            accumulatedEnergy -= lastEnergy;// * lastEnergy;
        }
        energyHistory.add(instantEnergy);
        accumulatedEnergy += instantEnergy;// * instantEnergy;
        
        boolean isOnset = false;
        if (energyHistory.size() >= HISTORYSIZE)
        {
//            boolean highEnergy = instantEnergy > SENSITIVITY * localAverageEnergy;// && instantEnergy > 500000;
            boolean highEnergy = instantEnergy > SENSITIVITY * localAverageEnergy && instantEnergy > 300;
            if (highEnergy && framesSinceLastOnset > MINFRAMESBETWEENONSETS)
            {
                isOnset = true;
                framesSinceLastOnset = 0;
                Log.d("clap", "isOnset= " + isOnset + " instantEnergy=" + instantEnergy + " localAverageEnergy=" + localAverageEnergy);
            }
        }
        framesSinceLastOnset++;
        
        return isOnset;
    }
    
    private static long computeInstantEnergy(short[] monoSamples, int frameStart)
    {
        long instantEnergy = 0;
        for (int i = frameStart + 1; i < frameStart + 1024; i++)
        {
            long diff = monoSamples[i - 1] - monoSamples[i];
            
//            instantEnergy += diff * diff;
            instantEnergy += Math.abs(diff);
        }
        
        return instantEnergy / 1024;
    }

    
//    private static long computeInstantEnergy(short[] monoSamples, int frameStart)
//    {
//        long instantEnergy = 0;
//        for (int i = frameStart; i < frameStart + 1024; i++)
//        {
//            instantEnergy += ((long)monoSamples[i]) * monoSamples[i];
//        }
//        
//        return instantEnergy / 1024;
//    }

}
