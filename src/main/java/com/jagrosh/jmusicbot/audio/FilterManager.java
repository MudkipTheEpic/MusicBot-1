package com.jagrosh.jmusicbot.audio;

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class FilterManager {
    //private int distortionLevel = 0;
    private double timeScaleFactor = 1;
    private float bassScaleFactor = 1; //First 4 channels
    private double pitchScaleFactor = 1;
    private boolean karaokeEnabled = false;
    private int karaokeBand = 220;
    private int karaokeWidth = 100;
    private float[] bassScaleArray;
    private PcmFilterFactory cachedFactory;

    public FilterManager() {}

    public double getTimeScaleFactor() {
        return timeScaleFactor;
    }

    public double getPitchScaleFactor() {
        return pitchScaleFactor;
    }

    /*public int getDistortionLevel() {
        return distortionLevel;
    }*/

    public float getBassScaleFactor() {
        return bassScaleFactor;
    }

    public boolean isKaraokeEnabled() {
        return karaokeEnabled;
    }

    public boolean setBassScaleFactor(float newFactor) {
        if (newFactor < 0 || newFactor > 5) {
            return false;
        }
        bassScaleFactor = newFactor;
        float adjustedFactor;
        //Create band multiplier array for bass boost
        /*if (bassScaleFactor >= 1) {
            adjustedFactor = (float) (((Math.log(2*bassScaleFactor) / Math.log(2)) / 4) - 0.25);
        } else {
            adjustedFactor = (float) ((bassScaleFactor)/4 - 0.25);
        }*/
        adjustedFactor = (bassScaleFactor-1)/4;

        bassScaleArray = new float[] {adjustedFactor, adjustedFactor, adjustedFactor, adjustedFactor, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        return true;
    }

    public boolean setTimeScaleFactor(double newFactor) {
        if (newFactor < 0.25 || newFactor > 4) {
            return false;
        }
        timeScaleFactor = newFactor;
        return true;
    }

    public boolean setPitchScaleFactor(double newFactor) {
        if (newFactor < 0.25 || newFactor > 4) {
            return false;
        }
        pitchScaleFactor = newFactor;
        return true;
    }

    public void setKaraokeEnabled(boolean karaokeEnabled) {
        this.karaokeEnabled = karaokeEnabled;
    }


    public PcmFilterFactory getFactory() {
        return (audioTrack, audioDataFormat, universalPcmAudioFilter) -> {
            List<AudioFilter> filterList = new ArrayList<>();
            FloatPcmAudioFilter lastFilter = universalPcmAudioFilter;
            if (bassScaleFactor != 1) {
                Equalizer eqFilter = new Equalizer(audioDataFormat.channelCount, lastFilter, bassScaleArray);
                lastFilter = eqFilter;
                filterList.add(eqFilter);
            }
            if (karaokeEnabled) {
                KaraokePcmAudioFilter karaokeFilter = new KaraokePcmAudioFilter(lastFilter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
                karaokeFilter.setFilterBand(karaokeBand);
                karaokeFilter.setFilterWidth(karaokeWidth);
                lastFilter = karaokeFilter;
                filterList.add(karaokeFilter);
                //System.out.println("Karaoke converter is " + (karaokeFilter.isEnabled() ? "enabled." : "disabled."));
            }
            if (timeScaleFactor != 1 || pitchScaleFactor != 1) {
                TimescalePcmAudioFilter timescaleFilter = new TimescalePcmAudioFilter(lastFilter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
                lastFilter = timescaleFilter;
                timescaleFilter.setSpeed(timeScaleFactor);
                timescaleFilter.setPitch(pitchScaleFactor);
                filterList.add(timescaleFilter);
            }
            return filterList;
        };
    }
}
