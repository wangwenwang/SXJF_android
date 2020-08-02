package com.kaidongyuan.app.kdydriver.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kaidongyuan.app.basemodule.widget.loadingDialog.MyLoadingDialog;
import com.kaidongyuan.app.kdydriver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zpSDK.zpSDK.zpBluetoothPrinter;

public class PrintActivityshein extends Activity {

    protected MyLoadingDialog myLoadingDialog;
    public static BluetoothAdapter myBluetoothAdapter;
    public String SelectedBDAddress;
    public static Context mContext;

    public static String BStr = "gbk";

    StatusBox statusBox;
    /**
     * 返回上一界面按钮
     */

    View view;
    View viewTwo;
    View viewTwoother;
    String json_print;

    // 打印的json_array
    com.alibaba.fastjson.JSONArray json_array_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_firstshein);

        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请先在手机设置的蓝牙中连接打印机设备")
//                    .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                })
                .create();

        if(!ListBluetoothDevice()){
            alertDialog2.show();
        };

        mContext = this;
        Intent intent1 = getIntent();
        json_print = intent1.getStringExtra("omsNo");

        json_array_data = JSONArray.parseArray(json_print);

        Button Button1 = (Button) findViewById(R.id.btPrintCustom);

        statusBox = new StatusBox(this,Button1);

        Button1.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {

                new Thread() {
                    public void run() {

                        Print1(SelectedBDAddress);
                    }
                }.start();
            }
        });

        Button Button2 = (Button) findViewById(R.id.returnjsbtn);

        statusBox = new StatusBox(this,Button2);
        Button2.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    //    打印
    public void Print1(String BDAddress)
    {
//        Looper.prepare();//增加部分
        if(BDAddress == null){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(mContext,"请选择要连接的设备", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadingDialog();
            }
        });

        zpBluetoothPrinter zpSDK=new zpBluetoothPrinter(this);
        if(!zpSDK.connect(BDAddress))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    cancelLoadingDialog();
                    Toast.makeText(mContext,"连接失败------", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        JSONObject scanLabelItem;
        String sheinContentItem;


        for (int i = 0; i < json_array_data.size(); i++) {

            scanLabelItem = (JSONObject) json_array_data.get(i);

            sheinContentItem = scanLabelItem.getString("content");

            Log.d("LM", sheinContentItem);
//            sData.getBytes();//系统自带  ，string就是需要转化的字符串

//            Base64.decodeBase64(sData);

//            byte[] by = sData.getBytes();

//            zpSDK.Write(by);
//            zpSDK.Write(sheinContentItem.getBytes("gbk"));
            try{

                zpSDK.SPPWrite( sheinContentItem.getBytes(BStr));

            }catch (Exception e){
                Log.d("LM", "云打印");
            }

            zpSDK.SPPWrite(new byte[]{0x0d,0x0a});

//            Base64.decodeFast(sData)
            zpSDK.print(0, 0);
            zpSDK.printerStatus();

            int a=zpSDK.GetStatus();

            if(a==-1)
            { //"获取状态异常------";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"获取状态异常------", Toast.LENGTH_LONG).show();
                    }
                });

                zpSDK.disconnect();
                break;
            }
            if(a==1)
            {//"缺纸----------";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"缺纸----------", Toast.LENGTH_LONG).show();
                    }
                });

                zpSDK.disconnect();
                break;
            }
            if(a==2)
            {
                //"开盖----------";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"开盖----------", Toast.LENGTH_LONG).show();
                    }
                });
                zpSDK.disconnect();
                break;
            }
//
            if(a==0 && i==json_array_data.size()-1)
            {
                //"打印机正常-------";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"已成功打印", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelLoadingDialog();
            }
        });
        zpSDK.disconnect();

    }

    public boolean ListBluetoothDevice()
    {

        final List<Map<String,String>> list=new ArrayList<Map<String, String>>();

        ListView listView = (ListView) findViewById(R.id.listView1);

        SimpleAdapter m_adapter = new SimpleAdapter( this,list,
                android.R.layout.simple_list_item_2,
                new String[]{"DeviceName","BDAddress"},
                new int[]{android.R.id.text1,android.R.id.text2}
        );

        listView.setAdapter(m_adapter);

        if((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter())==null)
        {
            Toast.makeText(this,"没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!myBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() <= 0)return false;
        for (BluetoothDevice device : pairedDevices)
        {
            Map<String,String> map=new HashMap<String, String>();
            map.put("DeviceName", device.getName());
            map.put("BDAddress", device.getAddress());
            list.add(map);
        }
        listView.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                SelectedBDAddress = list.get(arg2).get("BDAddress");
                if (((ListView)arg0).getTag() != null){
                    ((View)((ListView)arg0).getTag()).setBackgroundDrawable(null);
                }
                ((ListView)arg0).setTag(arg1);
                arg1.setBackgroundColor(getResources().getColor(R.color.mineblue));

            }
        });
        return true;
    }

    //    @Override
    public void showLoadingDialog() {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(this);
        }
//        cancelLoadingDialog();
        myLoadingDialog.showDialog();
    }

    //    @Override
    public void cancelLoadingDialog() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
        }
    }

    //    生成条形码
    public static Bitmap createBarcode(String content,int cheight) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, 2000, cheight);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    //    生成二维码
    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}
