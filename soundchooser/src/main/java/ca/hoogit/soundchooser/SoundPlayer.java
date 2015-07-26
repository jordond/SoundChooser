/**
 * Copyright (C) 2015, Jordon de Hoog
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ca.hoogit.soundchooser;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.IOException;

import mbanje.kurt.fabbutton.FabButton;

/**
 * @author jordon
 *         <p/>
 *         Date    24/07/15
 *         Description
 */
public class SoundPlayer implements MediaPlayer.OnCompletionListener {

    private static final String TAG = SoundPlayer.class.getSimpleName();

    private final String RESOURCE_PREFIX = "android.resource://";
    private final int TICK_LENGTH = 100;

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private int mSoundId;
    private int mStreamType = AudioManager.STREAM_MUSIC;

    private FabButton mButton;

    private CountDownTimer mTimer;
    private OnSoundEvent mListener;

    public SoundPlayer(Context mContext) {
        this.mContext = mContext;
        this.mSoundId = 0;
    }

    public SoundPlayer(Context mContext, int streamType) {
        this.mContext = mContext;
        this.mStreamType = streamType;
        this.mSoundId = 0;
    }

    public SoundPlayer(Context mContext, int mSoundId, FabButton mButton) {
        this.mContext = mContext;
        this.mSoundId = mSoundId;
        this.mButton = mButton;
    }

    public SoundPlayer(Context mContext, OnSoundEvent listener) {
        this.mContext = mContext;
        this.mSoundId = 0;
        this.mListener = listener;
    }

    public SoundPlayer(Context context, int soundId, OnSoundEvent listener) {
        this.mContext = context;
        this.mSoundId = soundId;
        this.mListener = listener;
        this.create(soundId);
    }

    public void setOnSoundEventListener(OnSoundEvent listener) {
        this.mListener = listener;
    }

    public boolean create(int soundId) {
        Uri soundFile = Uri.parse(RESOURCE_PREFIX + mContext.getPackageName() + "/" + soundId);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(mStreamType);
            mMediaPlayer.setDataSource(mContext, soundFile);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(false);
            this.mSoundId = soundId;
            Log.d(TAG, "Media player was created with soundId: " + soundId);
        } catch (IOException ex) {
            Log.e(TAG, "Error loading the sound file");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean play() {
        return play(mSoundId);
    }

    public boolean play(FabButton button, int soundId) {
        mButton = button;
        if (play(soundId)) {
            mButton.setIcon(R.drawable.ic_av_stop, R.drawable.ic_fab_complete);
            mButton.showProgress(true);
            mButton.setProgress(0);
            return true;
        }
        return false;
    }

    public boolean play(int soundId) {
        if (soundId == 0) {
            Log.e(TAG, "Invalid sound ID was supplied");
            return false;
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            stop();
        }
        if (create(soundId)) {
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.start();
            createTimer(mMediaPlayer.getDuration());
            if (mListener != null) {
                mListener.onStartPlayback(mMediaPlayer.getDuration());
            }
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    private void createTimer(final long duration) {
        mTimer = new CountDownTimer(duration, TICK_LENGTH) {
            @Override
            public void onTick(long millisUntilFinished) {
                long elapsed = duration - (millisUntilFinished - TICK_LENGTH);
                float progress = ((float) elapsed / duration) * 100.0f;
                //progress = progress < 0 ? 0 : progress;
                if (mButton != null) {
                    mButton.setProgress(progress);
                }
                if (mListener != null) {
                    mListener.onPlaybackTick(progress);
                }
            }

            @Override
            public void onFinish() {
                if (mListener != null) {
                    mListener.onFinishPlayback();
                }
                if (mButton != null) {
                    mButton.setProgress(100);
                }
                mTimer = null;
            }
        };
        mTimer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
        if (mListener != null) {
            mListener.onFinishPlayback();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            if (mTimer != null) {
                mTimer.cancel();
            }
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            Log.d(TAG, "Media player was released and cleaned up");
        }
    }

    public FabButton getButton() {
        return mButton;
    }

    public void setButton(FabButton button) {
        this.mButton = button;
    }

    public int getStreamType() {
        return mStreamType;
    }

    public void setStreamType(int type) {
        this.mStreamType = type;
    }

    public boolean isPlaying() {
        return this.mMediaPlayer != null && this.mMediaPlayer.isPlaying();
    }

    public interface OnSoundEvent {
        void onStartPlayback(int duration);

        void onPlaybackTick(float progress);

        void onFinishPlayback();
    }
}
