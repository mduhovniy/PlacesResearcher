package info.duhovniy.maxim.placesresearcher.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import info.duhovniy.maxim.placesresearcher.R;

public class DistancePickerPreference extends DialogPreference {

    // allowed range
    private int maxValue;
    private static final int MIN_VALUE = 1;
    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int value;
    private Context mContext;

    public DistancePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DistancePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        mContext = getContext();
        rangeSetup();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(mContext);
        picker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(mContext);
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(maxValue);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }

    public void rangeSetup() {
        if (PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(mContext.getResources().getString(R.string.unit_checkbox), true))
            maxValue = UIConstants.MAX_DISTANCE_KM;
        else
            maxValue = UIConstants.MAX_DISTANCE_MILE;
    }
}
