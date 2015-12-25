package top.liborange.cleverzgoapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import top.liborange.Config;
import top.liborange.connection.CmdService;
import top.liborange.connection.RpcFramework;
import top.liborange.joystick.JoyStick;

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
 * ━━━━━━神兽出没━━━━━━  Created by liborange on 15/12/21.
 */
public class CleverZGo extends AppCompatActivity {

    private CmdService service;

    private TextView text;
    private JoyStick joyStick;
    private int power = 50;
    private int level = 5;

    private Button conn;
    private Button powerPlus;
    private Button powerMin;
    private Button levelPlus;
    private Button levelMin;
    private Button adjust;
    private Button turnAround;
    private Button around;

    private EditText FL,FR,BL,BR;
    private EditText ip;

    private TextView powerText,levelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = (Button) findViewById(R.id.conn);
        powerPlus = (Button) findViewById(R.id.powerPlus);
        powerMin = (Button) findViewById(R.id.powermin);
        levelPlus = (Button) findViewById(R.id.levelPlus);
        levelMin = (Button) findViewById(R.id.levelmin);
        adjust = (Button) findViewById(R.id.adjust);
        turnAround = (Button) findViewById(R.id.turnAround);
        around = (Button) findViewById(R.id.around);

        FL = (EditText) findViewById(R.id.FL);
        FR = (EditText) findViewById(R.id.FR);
        BL = (EditText) findViewById(R.id.BL);
        BR = (EditText) findViewById(R.id.BR);
        ip = (EditText) findViewById(R.id.ip);

        powerText = (TextView) findViewById(R.id.power);
        levelText = (TextView) findViewById(R.id.level);

        text = (TextView) findViewById(R.id.text);
        joyStick = (JoyStick) findViewById(R.id.joystick);
        joyStick.setExporter(new JoyStick.Exporter() {
            @Override
            public void onTouchOver(int angle) {
                if (angle < 0) {
                    sendCMD(Config.CMD.stop);
                } else {
                    double a = (angle*Math.PI/180);
                    double sinV = Math.sin(a);
                    double cosV = Math.cos(a);
                    Log.d("cmd", "传送角度为："+angle+"\t"+a+"\t"+sinV+"\t"+cosV);
                    if (sinV == 1) {
                        sendCMD(power, power, power, power);
                    } else if (sinV >= 0 && cosV > 0) {
                        sendCMD((int) (sinV * power), (int) (sinV * power), power, power);
                    } else if (sinV >= 0 && cosV < 0) {
                        sendCMD(power, power, (int) (sinV * power), (int) (sinV * power));
                    } else if (sinV < 0 && cosV > 0 && sinV > cosV) {
                        sendCMD((int) (sinV * power), (int) (sinV * power), -power, -power);
                    } else if (sinV < 0 && cosV < 0 && sinV > cosV) {
                        sendCMD(-power, -power, (int) (sinV * power), (int) (sinV * power));
                    } else if (sinV < 0 && cosV < 0 && sinV < cosV) {
                        sendCMD(power, power, -power, -power);
                    } else if (sinV < 0 && cosV > 0 && sinV < cosV) {
                        sendCMD(-power, -power, power, power);
                    }
                }
            }
        });

        powerPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (power < 100)
                    power++;
                powerPlus.setBackgroundColor(Color.rgb(102, 153, 255));
                powerText.setText("当前车速为：" + power);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    powerPlus.setBackgroundColor(Color.rgb(204, 204, 204));
                }
                return false;
            }

        });
        powerMin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(power>0)
                    power--;
                powerMin.setBackgroundColor(Color.rgb(102, 153, 255));
                powerText.setText("当前车速为：" + power);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    powerMin.setBackgroundColor(Color.rgb(204,204,204));
                }
                return false;
            }
        });

        conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init("192.168.2.111", 50001);
                conn.setText("初始化完成");
            }
        });
    }




    class ButtonListener implements View.OnTouchListener{

        private Config.CMD identify;
        public ButtonListener(Config.CMD identify){
            this.identify = identify;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d("发送命令", identify + "准备发送到树莓派");
                sendCMD(identify);
            }else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d("发送命令", "发送 forward 命令到树莓派");
                sendCMD(Config.CMD.forward);
            }
            return false;
        }
    }

    //TODO: 初始化，按钮事件监听，发送命令。晚上回来再做，游泳去了。15:50 2015.12.21


    public void init(String ip,int port){
        try {
            service = RpcFramework.refer(CmdService.class, ip, port);
        } catch (Exception e) {
            Log.d("error","Rpc 连接失败.");
        }
    }
    /**
     *
     * @param cmd
     */
    public void sendCMD(Object  cmd){
        final Object c = cmd;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.executeCMD(c);
                }catch (Exception e) {
                    Log.d("debug", e.getMessage() + "\n" + e.getCause());
                }
            }
        }).start();
    }
    public void sendCMD(int a,int b,int c,int d){
        final int aa =a,bb =b,cc = c,dd =d;
        Log.d("cmd", a + " " + b + " " + c + " " + d);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.executeCMD(aa,bb,cc,dd);
                }catch (Exception e) {
                    Log.d("debug", e.getMessage() + "\n" + e.getCause());
                }
            }
        }).start();
    }
}
