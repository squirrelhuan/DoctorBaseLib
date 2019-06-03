package cn.demomaster.huan.doctorbaselibrary.view.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselibrary.R;

/**
 * @author squirrel桓
 * @date 2018/11/20.
 * description：
 */
@SuppressLint("AppCompatCustomView")
public class TimerButtonTextView extends TextView {

    private Context context;

    public TimerButtonTextView(Context context) {
        super(context);
        this.context = context;
    }

    public TimerButtonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public TimerButtonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private int center_x, center_y, mwidth, width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        center_x = width / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);
        super.onDraw(canvas);
    }

    private void drawLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.main_color));

        int width_min = Math.min(width, height);
        int stroke_width = width_min / 50;
        paint.setStrokeWidth(stroke_width);
        paint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形,只显示边缘的线，不显示内部
        canvas.drawCircle(width / 2, height / 2, width_min / 2 - stroke_width, paint);// 圆

        int gap = width_min / 9;
        paint.setStyle(Paint.Style.FILL);  //绘制空心圆或 空心矩形,只显示边缘的线，不显示内部
        canvas.drawCircle(width / 2, height / 2, width_min / 2 - gap, paint);// 圆

    }


}
