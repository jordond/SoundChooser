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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import ca.hoogit.soundchooser.SoundChooserButton.OnSoundSelectedListener;
import mbanje.kurt.fabbutton.FabButton;

/**
 * @author jordon
 *         <p/>
 *         Date    24/07/15
 *         Description
 */
public class SoundChooserPalette extends TableLayout {

    public OnSoundSelectedListener mOnSoundSelectedListener;

    private int mCircleColor;
    private int mButtonLength;
    private int mMarginSize;
    private int mNumColumns;

    public SoundChooserPalette(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoundChooserPalette(Context context) {
        super(context);
    }

    public FabButton findButton(int id) {
        SoundChooserButton button = (SoundChooserButton) this.findViewById(id);
        return button.get();
    }

    /**
     * Initialize the size, columns, and listener.  Size should be a pre-defined size (SIZE_LARGE
     * or SIZE_SMALL) from ColorPickerDialogFragment.
     */
    public void init(int circleColor, int size, int columns, OnSoundSelectedListener listener) {
        mCircleColor = circleColor;
        mNumColumns = columns;
        Resources res = getResources();
        if (size == SoundChooserDialog.SIZE_LARGE) {
            mButtonLength = res.getDimensionPixelSize(R.dimen.sound_swatch_large);
            mMarginSize = res.getDimensionPixelSize(R.dimen.sound_swatch_margins_large);
        } else {
            mButtonLength = res.getDimensionPixelSize(R.dimen.sound_swatch_small);
            mMarginSize = res.getDimensionPixelSize(R.dimen.sound_swatch_margins_small);
        }
        mOnSoundSelectedListener = listener;
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(params);
        return row;
    }

    /**
     * Adds swatches to table in a serpentine format.
     */
    public void drawPalette(int[] sounds) {
        if (sounds == null) {
            return;
        }

        this.removeAllViews();
        int rowElements = 0;
        int rowNumber = 0;

        // Fills the table with swatches based on the array of colors.
        TableRow row = createTableRow();
        for (int sound : sounds) {
            View soundButton = createSoundButton(sound);
            soundButton.setId(sound);
            addButtonToRow(row, soundButton, rowNumber);

            rowElements++;
            if (rowElements == mNumColumns) {
                addView(row);
                row = createTableRow();
                rowElements = 0;
                rowNumber++;
            }
        }

        // Create blank views to fill the row if the last row has not been filled.
        if (rowElements > 0) {
            while (rowElements != mNumColumns) {
                addButtonToRow(row, createBlankSpace(), rowNumber);
                rowElements++;
            }
            addView(row);
        }
    }

    /**
     * Appends a swatch to the end of the row for even-numbered rows (starting with row 0),
     * to the beginning of a row for odd-numbered rows.
     */
    private void addButtonToRow(TableRow row, View swatch, int rowNumber) {
        if (rowNumber % 2 == 0) {
            row.addView(swatch);
        } else {
            row.addView(swatch, 0);
        }
    }

    /**
     * Creates a blank space to fill the row.
     */
    private ImageView createBlankSpace() {
        ImageView view = new ImageView(getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(mButtonLength, mButtonLength);
        params.setMargins(mMarginSize, mMarginSize, mMarginSize, mMarginSize);
        view.setLayoutParams(params);
        return view;
    }

    /**
     * Creates a color swatch.
     */
    private SoundChooserButton createSoundButton(int sound) {
        SoundChooserButton view = new SoundChooserButton(getContext(), mCircleColor, sound, mOnSoundSelectedListener);
        TableRow.LayoutParams params = new TableRow.LayoutParams(mButtonLength, mButtonLength);
        params.setMargins(mMarginSize, mMarginSize, mMarginSize, mMarginSize);
        view.setLayoutParams(params);
        return view;
    }
}
