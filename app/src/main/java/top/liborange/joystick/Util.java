package top.liborange.joystick;


import android.graphics.Point;
import android.util.Log;
import android.view.ContextThemeWrapper;

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
 * ━━━━━━神兽出没━━━━━━  Created by liborange on 15/12/24.
 */
public class Util {
    public static int distance(Point a,Point b) {
        return distance(a.x, a.y, b.x, b.y);
    }

    public static int distance(float x1, float y1, float x2, float y2) {
        return (int)Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int calculateAngle(float x1, float y1, float x2, float y2) {
        Log.d("输入数据",x1+"\t"+y1+"\t"+x2+"\t"+y2);
        Log.d("compute", (x2 - x1) / (y1 - y2)+"");
        Log.d("result",Math.atan((x2 - x1) / (y1 - y2))+"");
        return (int)Math.atan((x2 - x1) / (y1 - y2));
    }


    public static int dip2px(ContextThemeWrapper context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(ContextThemeWrapper context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static Point getBorderPoint(Point a, Point b, int cutRadius) {
        float radian = getRadian(a, b);
        return new Point(a.x + (int) (cutRadius * Math.cos(radian)), a.y + (int) (cutRadius * Math.sin(radian)));
    }

    //获取水平线夹角弧度
    public static float getRadian(Point a, Point b) {
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y < a.y ? -1 : 1);
        return ang;
    }
    public static int getAngleCouvert(float radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }
    public static void main(String[] args) {
        int angle = calculateAngle(2, 2, 1, 1);
        System.out.println(angle);
    }
}