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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import ca.hoogit.soundchooser.SoundChooserButton.OnSoundSelectedListener;
import mbanje.kurt.fabbutton.FabButton;

/**
 * @author jordon
 *         <p/>
 *         Date    24/07/15
 *         Description
 */
public class SoundChooserDialog extends DialogFragment implements OnSoundSelectedListener {

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_CIRCLE_COLOR = "circle_color";
    protected static final String KEY_SOUNDS = "sounds";
    protected static final String KEY_SELECTED_SOUND = "selected_sound";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";
    private static final String TAG = SoundChooserDialog.class.getSimpleName();
    protected AlertDialog mAlertDialog;
    protected String mTitle;
    protected int mCircleColor;
    protected int[] mSounds = null;
    protected int mSelectedSound;
    protected int mLastSound;
    protected int mColumns;
    protected int mSize;
    protected int mStreamType;

    protected SoundPlayer mSoundPlayer;
    protected OnOptionChosen mListener;
    private SoundChooserPalette mPalette;

    public SoundChooserDialog() {
        // Empty constructor required for dialog fragments.
    }

    public static SoundChooserDialog newInstance(String title, int[] sounds, int circleColor,
                                                 int columns, int size) {
        SoundChooserDialog ret = new SoundChooserDialog();
        ret.initialize(title, sounds, circleColor, columns, size);
        return ret;
    }

    public void initialize(String title, int[] sounds, int circleColor, int columns, int size) {
        setArguments(title, circleColor, columns, size);
        setSounds(sounds);
    }

    public void setArguments(String title, int color, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putInt(KEY_CIRCLE_COLOR, color);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        setArguments(bundle);
    }

    public void setOnOptionChosen(OnOptionChosen listener) {
        mListener = listener;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitle = getArguments().getString(KEY_TITLE);
            mCircleColor = getArguments().getInt(KEY_CIRCLE_COLOR);
            mColumns = getArguments().getInt(KEY_COLUMNS);
            mSize = getArguments().getInt(KEY_SIZE);
        }

        if (savedInstanceState != null) {
            mSounds = savedInstanceState.getIntArray(KEY_SOUNDS);
            mSelectedSound = (Integer) savedInstanceState.getSerializable(KEY_SELECTED_SOUND);
        }
    }

    public void setAudioStreamType(int type) {
        mStreamType = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sound_picker_dialog, null);
        mPalette = (SoundChooserPalette) view.findViewById(R.id.sound_picker);
        mPalette.init(mCircleColor, mSize, mColumns, this);

        mSoundPlayer = new SoundPlayer(activity, mStreamType);

        if (mSounds != null) {
            showPaletteView();
        }

        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositive(dialog, mLastSound);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegative(dialog);
                    }
                })
                .create();

        return mAlertDialog;
    }

    @Override
    public void onSoundSelected(int sound) {
        if (mListener != null) {
            mListener.onSoundSelected(sound);
        }

        if (mSoundPlayer.isPlaying()) {
            mSoundPlayer.stop();
        } else {
            mSelectedSound = 0;
        }
        refreshPalette();

        FabButton button = mPalette.findButton(sound);

        if (mSelectedSound != sound) {
            mSelectedSound = sound;
            mSoundPlayer.play(button, sound);
        }
        mLastSound = sound;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSoundPlayer != null) {
            mSoundPlayer.destroy();
        }
    }

    public void showPaletteView() {
        if (mPalette != null) {
            refreshPalette();
            mPalette.setVisibility(View.VISIBLE);
        }
    }

    private void refreshPalette() {
        if (mPalette != null && mSounds != null) {
            mPalette.drawPalette(mSounds);
        }
    }

    public int[] getSounds() {
        return mSounds;
    }

    public void setSounds(int[] sounds) {
        if (mSounds != sounds) {
            mSounds = sounds;
            refreshPalette();
        }
    }

    public int getSelectedSound() {
        return mSelectedSound;
    }

    public void setSelectedSound(int sound) {
        if (mSelectedSound != sound) {
            mSelectedSound = sound;
            refreshPalette();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_SOUNDS, mSounds);
        outState.putSerializable(KEY_SELECTED_SOUND, mSelectedSound);
    }

    public interface OnOptionChosen {
        void onSoundSelected(int soundId);

        void onPositive(DialogInterface dialog, int soundId);

        void onNegative(DialogInterface dialog);
    }
}
