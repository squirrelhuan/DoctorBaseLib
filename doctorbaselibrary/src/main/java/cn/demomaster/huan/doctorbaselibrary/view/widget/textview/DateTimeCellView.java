package cn.demomaster.huan.doctorbaselibrary.view.widget.textview;

import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.CalendarTimeModel;
import cn.demomaster.huan.doctorbaselibrary.model.DateTimeModel;
import cn.demomaster.huan.doctorbaselibrary.util.DateTimeUtil;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/20.
 * description：
 */
public class DateTimeCellView extends View {

    private Context context;

    public DateTimeCellView(Context context) {
        super(context);
        this.context = context;
    }

    public DateTimeCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DateTimeCellView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        super.onDraw(canvas);
        if (durationTimeList != null && durationTimeList.size() > 0||showType==1) {
            drawLine(canvas);
        }
    }

    private int line_hight;
    private int lineCount;
    private int lineCountDef = 3;
    private int showType = 0;//0按照时间段显示，1按照时间点显示

    private void drawLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.main_color_light));

        if (showType == 0) {
            lineCount = durationTimeList.size();
            line_hight = height / (lineCountDef) / 2;
            for (int i = 0; i < (durationTimeList.size() > 3 ? 3 : durationTimeList.size()); i++) {
                //动态宽度
                CalendarTimeModel.Bean bean = durationTimeList.get(i);
                if(bean.isAppointmented()){
                    paint.setColor(context.getResources().getColor(R.color.main_color));
                }else {
                    paint.setColor(context.getResources().getColor(R.color.main_color_light));
                }

                long startTime = DateTimeUtil.dateToStamp(bean.getStartDateTime());
                long endTime =TextUtils.isEmpty(bean.getEndDateTime())?startTime: DateTimeUtil.dateToStamp(bean.getEndDateTime());
                long startX = DateTimeUtil.dateToStamp(getDateTime("08:00"));
                float left = (startTime - startX) / 1000 / (12 * 60 * 60f) * width;
                float right = ((endTime - startX) / 1000) / (12 * 60 * 60f) * width;
                if (right - left < line_hight) {
                    right = left + line_hight;
                }
                RectF rectF = new RectF(left, i * height / 3 + (height / 3 - line_hight) / 2, right, i * height / 3 + (height / 3 - line_hight) / 2 + line_hight);
                canvas.drawRoundRect(rectF, line_hight / 2, line_hight / 2, paint);
            }
        } else if(showType == 1){
            line_hight = height/5;
            float left = width * .1f;
            float right = width * (1 - .1f);
            RectF rectF = new RectF(left, height / 2 - line_hight / 2, right, height / 2 + line_hight / 2);
            canvas.drawRoundRect(rectF, line_hight / 2, line_hight / 2, paint);
        }
    }

    List<CalendarTimeModel.Bean> durationTimeList;

    public void setData(List<CalendarTimeModel.Bean> list) {
        this.durationTimeList = list;
        postInvalidate();
    }

    public void setShowType(int showType) {
        this.showType = showType;
        postInvalidate();
    }

    public String getDateTime(String time) {
        return DateTimeUtil.getToday().getYearMonthDay() + " " + time + ":00";
    }

}
