package tech.yaowen.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

public class TwoColumnNumberPicker extends LinearLayout {
    private TextView titleView1;
    private TextView titleView2;
    private NumberPicker numberPicker1;
    private NumberPicker numberPicker2;
    private Paint linePaint;

    public TwoColumnNumberPicker(Context context) {
        super(context);
    }

    public TwoColumnNumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoColumnNumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TwoColumnNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.two_column_number_picker, this, true);
        titleView1 = findViewById(R.id.zixi_number_picker_title1);
        titleView1 = findViewById(R.id.zixi_number_picker_title2);
        numberPicker1 = findViewById(R.id.zixi_mumber_picker1);
        numberPicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker1.setWrapSelectorWheel(false);

        numberPicker2 = findViewById(R.id.zixi_mumber_picker2);
        numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker2.setWrapSelectorWheel(false);

        changeDividerColor(numberPicker1, Color.TRANSPARENT);
        changeDividerColor(numberPicker2, Color.TRANSPARENT);

        numberPicker1.setMinValue(0);
        numberPicker1.setMaxValue(8);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFFF2F2F5);
        linePaint.setStrokeWidth(0.5f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            numberPicker1.setSelectionDividerHeight(1);
        }
        numberPicker1.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                changeSelectTextSizeAndColor(numberPicker1);
                numberPicker1.performClick();
            }
        });

        numberPicker2.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                changeSelectTextSizeAndColor(numberPicker2);
                numberPicker2.performClick();
            }
        });
        numberPicker2.setValue(30);
    }


    private void changeDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private EditText changeSelectTextSizeAndColor(@NonNull NumberPicker picker) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        try {
            Field item = picker.getClass().getDeclaredField("mInputText");
            item.setAccessible(true);
            Object obj = item.get(picker);
            if (obj instanceof EditText) {
                EditText text = (EditText) obj;
                text.setTextColor(0xFF3C464F);
                text.setTextSize(30);
                return text;
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
