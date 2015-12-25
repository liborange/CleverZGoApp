package top.liborange.joystick;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━  Created by liborange on 2015/12/24.
 */
public class JoyStick extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private DrawerThread drawer;
    private Exporter exporter;

    private Point centre , curPos;    //操纵点中心位置,当前操纵点所处位置
    public static int radius = 120;    //操纵点移动范围为一个圆，半径为80
    public static int JOY_RADIUS;
    public static int ACTIVE_RANGE;     //激活操纵杆的距离
    public static int CENTER_RANGE;

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        radius = Util.dip2px((ContextThemeWrapper) context, radius);
        JOY_RADIUS = radius/5;
        ACTIVE_RANGE = radius*2/3;
        CENTER_RANGE = radius/3;

        holder = this.getHolder();
        holder.addCallback(this);
        drawer = new DrawerThread(holder);

        this.setKeepScreenOn(true);
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);                  //抗锯齿
        setFocusable(true);                         //可获得焦点（键盘）
        setFocusableInTouchMode(true);              //可获得焦点（触摸）
        setZOrderOnTop(true);                       //保持在界面最上层
        holder.setFormat(PixelFormat.TRANSPARENT); //设置背景透明
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int width = this.getWidth();
        int height = this.getHeight();

        centre = new Point(width/2, height*2/3);
        curPos = new Point(centre);

        drawer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawer.pause = true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int distance = Util.distance(event.getX(), event.getY(), centre.x, centre.y);
       if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(distance > radius) {
                Log.d("action down", "out of radius");
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d("touch event", "action move");
            if (distance < radius) {
                curPos.set((int) event.getX(), (int) event.getY());
            } else {
                curPos = Util.getBorderPoint(centre, new Point((int) event.getX(), (int) event.getY()), radius);
            }

            if (distance > ACTIVE_RANGE)
                exporter.onTouchOver(Util.getAngleCouvert(Util.getRadian(centre, new Point((int) event.getX(), (int) event.getY()))));
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if(distance < ACTIVE_RANGE) {
                curPos.set(centre.x, centre.y);
                exporter.onTouchOver(-1);
            }
            else {
                curPos.set(centre.x, centre.y - radius);
                exporter.onTouchOver(90);
            }
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public interface Exporter{
        public void onTouchOver(int angle);
    }
    public void setExporter(Exporter exporter){
        this.exporter = exporter;
    }

    class DrawerThread extends Thread{
        SurfaceHolder holder;
        boolean pause = false;

        public DrawerThread(SurfaceHolder holder){
            this.holder = holder;
        }

        public void run(){
            Canvas canvas = null;
            Paint paint = new Paint();
            while(!pause){
                synchronized(holder) {
                    try {

                        canvas = holder.lockCanvas();
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                        paint.setColor(Color.WHITE);    //画大圆（小车运动范围）
                        paint.setAlpha(150);
                        canvas.drawCircle(centre.x, centre.y, radius, paint);
                        int distance = Util.distance(centre, curPos);
                        if (distance>ACTIVE_RANGE) {
                            paint.setColor(Color.GREEN);
                            paint.setAlpha(200);
                            RectF oval2 = new RectF(centre.x-radius, centre.y-radius, centre.x+radius, centre.y+radius);// 设置个新的长方形，扫描测量
                            int angle = Util.getAngleCouvert(Util.getRadian(centre, curPos));
                            canvas.drawArc(oval2, -angle-15, 30, true, paint);
                        }

                        if(distance>CENTER_RANGE&&distance<ACTIVE_RANGE){
                            paint.setColor(Color.rgb(102,153,255));
                        }else {
                            paint.setColor(Color.WHITE);    //画中圆
                        }
                        paint.setAlpha(100);
                        canvas.drawCircle(centre.x, centre.y, ACTIVE_RANGE, paint);

                        if(distance<CENTER_RANGE){
                            paint.setColor(Color.rgb(204,255,204));
                        }else {
                            paint.setColor(Color.GRAY);     //圆心
                        }
                        paint.setAlpha(100);
                        canvas.drawCircle(centre.x, centre.y, CENTER_RANGE, paint);


                        paint.setColor(Color.RED);      //画小圆（焦点位置）
                        paint.setAlpha(150);
                        canvas.drawCircle(curPos.x, curPos.y, JOY_RADIUS, paint);


                    } catch (Exception e) {

                    } finally {
                        if(canvas!=null)
                            holder.unlockCanvasAndPost(canvas);
                        canvas = null;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

}
