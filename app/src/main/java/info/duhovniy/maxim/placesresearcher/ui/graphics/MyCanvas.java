package info.duhovniy.maxim.placesresearcher.ui.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by maxduhovniy on 22/12/2015.
 */
public class MyCanvas extends View {
    public MyCanvas(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        float density = getResources().getDisplayMetrics().density;
        float scale = getResources().getDisplayMetrics().scaledDensity;

        paint.setTextSize(40 * scale);
        canvas.drawText("Control Fragment", 50 * density,  50 * density, paint);
    }
}
