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

/**
 * @author jordon
 * <p/>
 * Date    24/07/15
 * Description
 * Contains the FabButton view
 */

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import mbanje.kurt.fabbutton.FabButton;

/**
 * Creates a circular swatch of a specified color.  Adds a checkmark if marked as checked.
 */
public class SoundChooserButton extends FrameLayout implements View.OnClickListener {

    private int mSound;
    private FabButton mButton;
    private OnSoundSelectedListener mOnSoundSelectedListener;

    public SoundChooserButton(Context context) {
        super(context);
    }

    public SoundChooserButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoundChooserButton(Context context, int circleColor, int sound, OnSoundSelectedListener listener) {
        super(context);
        mSound = sound;
        mOnSoundSelectedListener = listener;

        LayoutInflater.from(context).inflate(R.layout.sound_button, this);
        mButton = (FabButton) findViewById(R.id.button);
        mButton.setColor(circleColor);
        mButton.setProgressColor(getProgressColor(circleColor));
        mButton.setOnClickListener(this);

    }

    public static int getProgressColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.70f;
        return Color.HSVToColor(hsv);
    }

    public FabButton get() {
        return mButton;
    }

    @Override
    public void onClick(View v) {
        if (mOnSoundSelectedListener != null) {
            mOnSoundSelectedListener.onSoundSelected(mSound);
        }
    }

    /**
     * Interface for a callback when a color square is selected.
     */
    public interface OnSoundSelectedListener {

        /**
         * Called when a specific color square has been selected.
         */
        void onSoundSelected(int soundId);
    }
}
