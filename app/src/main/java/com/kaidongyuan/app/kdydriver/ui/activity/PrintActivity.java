package com.kaidongyuan.app.kdydriver.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kaidongyuan.app.basemodule.widget.loadingDialog.MyLoadingDialog;
import com.kaidongyuan.app.kdydriver.R;

//打印
import zpSDK.zpSDK.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.view.Menu;

import android.graphics.Canvas;

public class PrintActivity extends Activity{

    protected MyLoadingDialog myLoadingDialog;
    public static BluetoothAdapter myBluetoothAdapter;
    public String SelectedBDAddress;
    public static Context mContext;

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

        setContentView(R.layout.activity_first);

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
        Button ButtonTwoTnOne = (Button) findViewById(R.id.btPrintCustomTwoTnOne);
        statusBox = new StatusBox(this,Button1);
        statusBox = new StatusBox(this,ButtonTwoTnOne);
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

        ButtonTwoTnOne.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {

                new Thread() {
                    public void run() {

                        JSONObject orderInfo14 = (JSONObject) json_array_data.get(0);
                        String transitSupplier =  "69545";

                        if(orderInfo14.getString("transitSupplier").equals(transitSupplier) ){

                            PrintTwoTnOne(SelectedBDAddress);
                        }else{
                            PrintTwoTnOneOther(SelectedBDAddress);
                        }
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
        try{
            json_array_data = JSONArray.parseArray(json_print);

            JSONObject orderInfo = (JSONObject) json_array_data.get(0);

            view =  getLayoutInflater().inflate(R.layout.print_layout, null);

            //  生成二维码
            ImageView imageviewqrcode45 = (ImageView) view.findViewById(R.id.imageviewqrcode);

            Bitmap imagebitmap = createQRCode(orderInfo.getString("QRCode"), 400);
            imageviewqrcode45.setImageBitmap(imagebitmap);

            //  生成二维码 小联
            ImageView imageviewqrcode156 = (ImageView) view.findViewById(R.id.imageviewqrcode1);
            imageviewqrcode156.setImageBitmap(imagebitmap);

            //   发货城市
            TextView issuepartycity = (TextView) view.findViewById(R.id.issuepartycity);
            issuepartycity.setText(orderInfo.getString("issuePartyCity"));

            //   发货城市 小联
            TextView issuepartycity1 = (TextView) view.findViewById(R.id.issuepartycity1);
            issuepartycity1.setText(orderInfo.getString("issuePartyCity"));

            //   发货联系人
            TextView issuepartycontact = (TextView) view.findViewById(R.id.issuepartycontact);
            issuepartycontact.setText(orderInfo.getString("issuePartyContact"));

            //   发货联系人 小联
            TextView issuepartycontact1 = (TextView) view.findViewById(R.id.issuepartycontact1);
            issuepartycontact1.setText(orderInfo.getString("issuePartyContact"));

            String issuePartyTel = orderInfo.getString("issuePartyTel")!=""?orderInfo.getString("issuePartyTel"):orderInfo.getString("issuePartyGuHua");
            //   发货联系人电话
            TextView issuepartytel = (TextView) view.findViewById(R.id.issuepartytel);
            issuepartytel.setText(issuePartyTel);

            //   发货联系人电话 小联
            TextView issuepartytel1 = (TextView) view.findViewById(R.id.issuepartytel1);
            issuepartytel1.setText(issuePartyTel);

            //   发货详细地址
            TextView issuepartyaddr = (TextView) view.findViewById(R.id.issuepartyaddr);
            issuepartyaddr.setText(orderInfo.getString("issuePartyDistricict") +"   "+ orderInfo.getString("issuePartyAddr"));

            //   发货详细地址 小联
            TextView issuepartyaddr1 = (TextView) view.findViewById(R.id.issuepartyaddr1);
            issuepartyaddr1.setText(orderInfo.getString("issuePartyDistricict") +"   "+ orderInfo.getString("issuePartyAddr"));

            //   寄件时间
            TextView actualpiecetime = (TextView) view.findViewById(R.id.actualpiecetime);
            actualpiecetime.setText(orderInfo.getString("actualPieceTime"));

            //   收货城市
            TextView receivepartycity = (TextView) view.findViewById(R.id.receivepartycity);
            receivepartycity.setText(orderInfo.getString("receivePartyCity"));

            //   收货城市 小联
            TextView receivepartycity1 = (TextView) view.findViewById(R.id.receivepartycity1);
            receivepartycity1.setText(orderInfo.getString("receivePartyCity"));

            //   收货联系人
            TextView receivepartycontactname = (TextView) view.findViewById(R.id.receivepartycontactname);
            receivepartycontactname.setText(orderInfo.getString("receivePartyContactName"));

            //   收货联系人 小联
            TextView receivepartycontactname1 = (TextView) view.findViewById(R.id.receivepartycontactname1);
            receivepartycontactname1.setText(orderInfo.getString("receivePartyContactName"));

            String receivePartyPhone = orderInfo.getString("receivePartyPhone")!=""?orderInfo.getString("receivePartyPhone"):orderInfo.getString("receivePartyGuHua");
            //   收货联系人电话
            TextView receivepartyphone = (TextView) view.findViewById(R.id.receivepartyphone);
            receivepartyphone.setText(receivePartyPhone);

            //   收货联系人电话 小联
            TextView receivepartyphone1 = (TextView) view.findViewById(R.id.receivepartyphone1);
            receivepartyphone1.setText(receivePartyPhone);

            //   收货地址
            TextView receivePartyAddr1 = (TextView) view.findViewById(R.id.receivePartyAddr1);
            receivePartyAddr1.setText(orderInfo.getString("receicePartyDistricict") +"   "+ orderInfo.getString("receivePartyAddr1"));

            //   收货地址 小联
            TextView receivePartyAddr11 = (TextView) view.findViewById(R.id.receivePartyAddr11);
            receivePartyAddr11.setText(orderInfo.getString("receicePartyDistricict") +"   "+ orderInfo.getString("receivePartyAddr1"));

            //   货物名称
            TextView productname = (TextView) view.findViewById(R.id.textView182);
            productname.setText(orderInfo.getString("productName"));

            //   打印时间
            TextView printtime = (TextView) view.findViewById(R.id.printtime);
            printtime.setText(orderInfo.getString("nowTime"));

            //   签收时间
            TextView deliverydate = (TextView) view.findViewById(R.id.deliverydate);
            deliverydate.setText(orderInfo.getString("deliveryDate"));

            //   月结卡号
            TextView monthcardnumber = (TextView) view.findViewById(R.id.monthcardnumber);
            monthcardnumber.setText(orderInfo.getString("monthCardNumber"));

            //   费用合计
            TextView orderamount = (TextView) view.findViewById(R.id.orderamount);
            orderamount.setText(orderInfo.getString("orderAmount"));

            //   件数
            TextView orderqty = (TextView) view.findViewById(R.id.orderqty);
            orderqty.setText(orderInfo.getString("orderQty"));

            //   实重
            TextView displaywt = (TextView) view.findViewById(R.id.displaywt);
            displaywt.setText(orderInfo.getString("displayWt"));

            //   计重
            TextView chargeweight = (TextView) view.findViewById(R.id.chargeweight);
            chargeweight.setText(orderInfo.getString("chargeWeight"));

            //   运费
            TextView transportfee = (TextView) view.findViewById(R.id.transportfee);
            transportfee.setText(orderInfo.getString("transportFee"));

            //   付款方式
            TextView chargetypes = (TextView) view.findViewById(R.id.chargetypes);
            chargetypes.setText(orderInfo.getString("chargeTypes"));

            //   附加服务
            TextView insuranceFee = (TextView) view.findViewById(R.id.insurancefee);
            insuranceFee.setText(orderInfo.getString("insuranceFee"));

            //   价值声明
            TextView valuationmoney = (TextView) view.findViewById(R.id.valuationmoney);
            valuationmoney.setText(orderInfo.getString("valuationMoney"));

            //   代收款
            TextView collectpayment = (TextView) view.findViewById(R.id.collectpayment);
            collectpayment.setText(orderInfo.getString("collectPayment"));

            //   签回单
            TextView returnbillflag = (TextView) view.findViewById(R.id.returnbillflag);
            returnbillflag.setText(orderInfo.getString("returnBillFlag"));

            //   尺寸
            TextView productsize = (TextView) view.findViewById(R.id.productsize);
            productsize.setText(orderInfo.getString("productSize"));

            //   备注
            TextView remark = (TextView) view.findViewById(R.id.remark);
            remark.setText(orderInfo.getString("remark"));

            //   备注 小联
            TextView remark1 = (TextView) view.findViewById(R.id.remark1);
            remark1.setText(orderInfo.getString("remark"));

            //   订单号
            TextView omsno = (TextView) view.findViewById(R.id.omsno);
            omsno.setText(orderInfo.getString("omsNo"));

        }catch (Exception e){
            Log.d("LM", "onCreate: ");
        }

        JSONArray scanLabelInfo = (JSONArray) json_array_data.get(1);

        JSONObject scanLabelItem;
        for (int i = 0; i < scanLabelInfo.size(); i++) {

            scanLabelItem = (JSONObject) scanLabelInfo.get(i);

           //  生成条形码
            ImageView imageviewbarcode = (ImageView) view.findViewById(R.id.imageviewbarcode);

            Bitmap barcodebitmap = createBarcode(scanLabelItem.getString("productNo"),400);
            imageviewbarcode.setImageBitmap(barcodebitmap);

            //  生成条形码 小联
            ImageView imageviewbarcode1 = (ImageView) view.findViewById(R.id.imageviewbarcode1);
            Bitmap barcodebitmap1 = createBarcode(scanLabelItem.getString("productNo"),300);
            imageviewbarcode1.setImageBitmap(barcodebitmap1);

            //   标签号
            TextView productno = (TextView) view.findViewById(R.id.productno);
            productno.setText(scanLabelItem.getString("productNo"));

            //   标签号 小联
            TextView productno1 = (TextView) view.findViewById(R.id.productno1);
            productno1.setText(scanLabelItem.getString("productNo"));

            int me = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);

            view.measure(me,me);

            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            if (Build.VERSION.SDK_INT >= 11) {
                view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
                view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
            } else {
                view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
            view.draw(canvas);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //设置想要的大小
            int newWidth=770;//96.25   800
            int newHeight=1144;//1155  144.375
//            int newHeight=1200;//1155  144.375

            //计算压缩的比率
            float scaleWidth=((float)newWidth)/width;
            float scaleHeight=((float)newHeight)/height;

            //获取想要缩放的matrix
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth,scaleHeight);
            matrix.postRotate(180);

            //获取新的bitmap
            bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
            bitmap.getWidth();
            bitmap.getHeight();
//            Log.d("LM","newWidth"+bitmap.getWidth());
//            Log.d("LM","newHeight"+bitmap.getHeight());
//            Log.d("LM","ByteCount"+bitmap.getByteCount());

            zpSDK.Draw_Page_Bitmap_(bitmap,1);//打印标签，打印大数据图片，宽度不要超过576

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
            if(a==0 && i==scanLabelInfo.size()-1)
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

    //    打印二合一面单
    public void PrintTwoTnOne(String BDAddress)
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

        JSONArray scanLabelInfo = (JSONArray) json_array_data.get(1);

        JSONObject scanLabelItem;

        JSONObject orderInfo12 = (JSONObject) json_array_data.get(0);

        Log.d("LM", "H5页面带来的参数 "+ json_array_data);

        //二合一面单
        viewTwo = getLayoutInflater().inflate(R.layout.print_layouttwoinone, null);

        //   货运单号  aviationMasterNo
        TextView twoaviationmasterno = (TextView) viewTwo.findViewById(R.id.aviationmasterno);
        twoaviationmasterno.setText(orderInfo12.getString("aviationMasterNo"));

        //   件数  bsOrderQty
        TextView twobsOrderQty = (TextView) viewTwo.findViewById(R.id.bsorderqty);
        twobsOrderQty.setText(orderInfo12.getString("bsOrderQty"));

        //   件数  小联  bsOrderQty
        TextView twobsOrderQty1 = (TextView) viewTwo.findViewById(R.id.bsorderqty1);
        twobsOrderQty1.setText(orderInfo12.getString("bsOrderQty"));

        //   重量   bsOrderWt
        TextView twobsOrderWt = (TextView) viewTwo.findViewById(R.id.bsorderwt);
        twobsOrderWt.setText(orderInfo12.getString("bsOrderWt"));

        //   重量   小联  bsOrderWt
        TextView twobsOrderWt1 = (TextView) viewTwo.findViewById(R.id.bsorderwt1);
        twobsOrderWt1.setText(orderInfo12.getString("bsOrderWt"));

        //   航班号  airlineNumber
        TextView two_airlineNumber = (TextView) viewTwo.findViewById(R.id.airlinenumber);
        two_airlineNumber.setText(orderInfo12.getString("airlineNumber"));

        //   始发站
        TextView two_departure = (TextView) viewTwo.findViewById(R.id.departure);
        two_departure.setText(orderInfo12.getString("startEndCity"));

        Bitmap imagebitmap = createQRCode(orderInfo12.getString("QRCode"), 400);

        //  生成二维码 小联  二合一
        ImageView imageviewqrcodetwo2 = (ImageView) viewTwo.findViewById(R.id.imageviewqrcodetwo2);
        imageviewqrcodetwo2.setImageBitmap(imagebitmap);

        //   发货城市 小联  二合一
        TextView issuepartycitytwo1 = (TextView) viewTwo.findViewById(R.id.issuepartycitytwo1);
        issuepartycitytwo1.setText(orderInfo12.getString("issuePartyCity"));

        //   发货联系人 小联 二合一
        TextView issuepartycontacttwo1 = (TextView) viewTwo.findViewById(R.id.issuepartycontacttwo1);
        issuepartycontacttwo1.setText(orderInfo12.getString("issuePartyContact"));

        String issuePartyTel = orderInfo12.getString("issuePartyTel")!=""?orderInfo12.getString("issuePartyTel"):orderInfo12.getString("issuePartyGuHua");

        //   发货联系人电话 小联  二合一
        TextView issuepartyteltwo1 = (TextView) viewTwo.findViewById(R.id.issuepartyteltwo1);
        issuepartyteltwo1.setText(issuePartyTel);

        //   发货详细地址 小联 二合一
        TextView issuepartyaddrtwo1 = (TextView) viewTwo.findViewById(R.id.issuepartyaddrtwo1);
        issuepartyaddrtwo1.setText(orderInfo12.getString("issuePartyDistricict") +"   "+ orderInfo12.getString("issuePartyAddr"));

        //   收货城市 小联 二合一
        TextView receivepartycitytwo1 = (TextView) viewTwo.findViewById(R.id.receivepartycitytwo1);
        receivepartycitytwo1.setText(orderInfo12.getString("receivePartyCity"));

        //   收货联系人 小联 二合一
        TextView receivepartycontactnametwo1 = (TextView) viewTwo.findViewById(R.id.receivepartycontactnametwo1);
        receivepartycontactnametwo1.setText(orderInfo12.getString("receivePartyContactName"));

        String receivePartyPhone = orderInfo12.getString("receivePartyPhone")!=""?orderInfo12.getString("receivePartyPhone"):orderInfo12.getString("receivePartyGuHua");

        //   收货联系人电话 小联 二合一
        TextView receivepartyphonetwo1 = (TextView) viewTwo.findViewById(R.id.receivepartyphonetwo1);
        receivepartyphonetwo1.setText(receivePartyPhone);

        //   收货地址 小联 二合一
        TextView receivePartyAddr1two1 = (TextView) viewTwo.findViewById(R.id.receivePartyAddr1two1);
        receivePartyAddr1two1.setText(orderInfo12.getString("receicePartyDistricict") +"   "+ orderInfo12.getString("receivePartyAddr1"));

        //   订单号  二合一
        TextView omsnotwo = (TextView) viewTwo.findViewById(R.id.omsnotwo);
        omsnotwo.setText(orderInfo12.getString("omsNo"));

        //  生成条形码   aviationMasterNo
        ImageView imageviewbarcodetwo1 = (ImageView) viewTwo.findViewById(R.id.imageviewbarcodetwo1);
        Bitmap barcodebitmaptwo1 = createBarcode(orderInfo12.getString("aviationMasterNo"),400);
        imageviewbarcodetwo1.setImageBitmap(barcodebitmaptwo1);

        //  生成条形码内容
        TextView productno = (TextView) viewTwo.findViewById(R.id.productnotwo1);
        productno.setText(orderInfo12.getString("aviationMasterNo"));

        for (int i = 0; i < scanLabelInfo.size(); i++) {

            scanLabelItem = (JSONObject) scanLabelInfo.get(i);

            //  生成条形码 小联
            ImageView imageviewbarcode14 = (ImageView) viewTwo.findViewById(R.id.imageviewbarcodetwo3);
            Bitmap barcodebitmap13 = createBarcode(scanLabelItem.getString("productNo"),300);
            imageviewbarcode14.setImageBitmap(barcodebitmap13);

            //   标签号 小联
            TextView productno16 = (TextView) viewTwo.findViewById(R.id.productnotwo3);
            productno16.setText(scanLabelItem.getString("productNo"));

            int me = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);

            viewTwo.measure(me,me);

            Bitmap bitmap = Bitmap.createBitmap(viewTwo.getMeasuredWidth(), viewTwo.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            if (Build.VERSION.SDK_INT >= 11) {
                viewTwo.measure(View.MeasureSpec.makeMeasureSpec(viewTwo.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(viewTwo.getHeight(), View.MeasureSpec.EXACTLY));
                viewTwo.layout((int) viewTwo.getX(), (int) viewTwo.getY(), (int) viewTwo.getX() + viewTwo.getMeasuredWidth(), (int) viewTwo.getY() + viewTwo.getMeasuredHeight());
            } else {
                viewTwo.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                viewTwo.layout(0, 0, viewTwo.getMeasuredWidth(), viewTwo.getMeasuredHeight());
            }
            viewTwo.draw(canvas);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //设置想要的大小
            int newWidth=770;//96.25   800
            int newHeight=1144;//1155  144.375
//            int newHeight=1200;//1155  144.375

            //计算压缩的比率
            float scaleWidth=((float)newWidth)/width;
            float scaleHeight=((float)newHeight)/height;

            //获取想要缩放的matrix
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth,scaleHeight);
            matrix.postRotate(180);

            //获取新的bitmap
            bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
            bitmap.getWidth();
            bitmap.getHeight();

            zpSDK.Draw_Page_Bitmap_(bitmap,1);//打印标签，打印大数据图片，宽度不要超过576

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
            if(a==0 && i==scanLabelInfo.size()-1)
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

    //    打印二合一面单  其他货代模板
    public void PrintTwoTnOneOther(String BDAddress)
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

        JSONArray scanLabelInfo = (JSONArray) json_array_data.get(1);

        JSONObject scanLabelItem;

        JSONObject orderInfo12 = (JSONObject) json_array_data.get(0);

        Log.d("LM", "H5页面带来的参数 "+ json_array_data);

        //二合一面单
        viewTwoother = getLayoutInflater().inflate(R.layout.print_layouttwoinoneother, null);

        //   货运单号  aviationMasterNo
        TextView twoaviationmasterno = (TextView) viewTwoother.findViewById(R.id.aviationmasterno);
        twoaviationmasterno.setText(orderInfo12.getString("aviationMasterNo"));

        //   件数  bsOrderQty
        TextView twobsOrderQty = (TextView) viewTwoother.findViewById(R.id.bsorderqty);
        twobsOrderQty.setText(orderInfo12.getString("bsOrderQty"));

        //   件数  小联  bsOrderQty
        TextView twobsOrderQty1 = (TextView) viewTwoother.findViewById(R.id.bsorderqty1);
        twobsOrderQty1.setText(orderInfo12.getString("bsOrderQty"));

        //   重量   bsOrderWt
        TextView twobsOrderWt = (TextView) viewTwoother.findViewById(R.id.bsorderwt);
        twobsOrderWt.setText(orderInfo12.getString("bsOrderWt"));

        //   重量   小联  bsOrderWt
        TextView twobsOrderWt1 = (TextView) viewTwoother.findViewById(R.id.bsorderwt1);
        twobsOrderWt1.setText(orderInfo12.getString("bsOrderWt"));

        //   航班号
        TextView two_airlineNumber = (TextView) viewTwoother.findViewById(R.id.departure);
        two_airlineNumber.setText(orderInfo12.getString("airlineCompany")+ "    " + orderInfo12.getString("airlineNumber"));

        //   始发站
        TextView two_departure = (TextView) viewTwoother.findViewById(R.id.airlinenumber);
        two_departure.setText(orderInfo12.getString("startEndCity"));

        Bitmap imagebitmap = createQRCode(orderInfo12.getString("QRCode"), 400);

        //  生成二维码 小联  二合一
        ImageView imageviewqrcodetwo2 = (ImageView) viewTwoother.findViewById(R.id.imageviewqrcodetwo2);
        imageviewqrcodetwo2.setImageBitmap(imagebitmap);

        //   发货城市 小联  二合一
        TextView issuepartycitytwo1 = (TextView) viewTwoother.findViewById(R.id.issuepartycitytwo1);
        issuepartycitytwo1.setText(orderInfo12.getString("issuePartyCity"));

        //   发货联系人 小联 二合一
        TextView issuepartycontacttwo1 = (TextView) viewTwoother.findViewById(R.id.issuepartycontacttwo1);
        issuepartycontacttwo1.setText(orderInfo12.getString("issuePartyContact"));

        String issuePartyTel = orderInfo12.getString("issuePartyTel")!=""?orderInfo12.getString("issuePartyTel"):orderInfo12.getString("issuePartyGuHua");

        //   发货联系人电话 小联  二合一
        TextView issuepartyteltwo1 = (TextView) viewTwoother.findViewById(R.id.issuepartyteltwo1);
        issuepartyteltwo1.setText(issuePartyTel);

        //   发货详细地址 小联 二合一
        TextView issuepartyaddrtwo1 = (TextView) viewTwoother.findViewById(R.id.issuepartyaddrtwo1);
        issuepartyaddrtwo1.setText(orderInfo12.getString("issuePartyDistricict") +"   "+ orderInfo12.getString("issuePartyAddr"));

        //   收货城市 小联 二合一
        TextView receivepartycitytwo1 = (TextView) viewTwoother.findViewById(R.id.receivepartycitytwo1);
        receivepartycitytwo1.setText(orderInfo12.getString("receivePartyCity"));

        //   收货联系人 小联 二合一
        TextView receivepartycontactnametwo1 = (TextView) viewTwoother.findViewById(R.id.receivepartycontactnametwo1);
        receivepartycontactnametwo1.setText(orderInfo12.getString("receivePartyContactName"));

        String receivePartyPhone = orderInfo12.getString("receivePartyPhone")!=""?orderInfo12.getString("receivePartyPhone"):orderInfo12.getString("receivePartyGuHua");

        //   收货联系人电话 小联 二合一
        TextView receivepartyphonetwo1 = (TextView) viewTwoother.findViewById(R.id.receivepartyphonetwo1);
        receivepartyphonetwo1.setText(receivePartyPhone);

        //   收货地址 小联 二合一
        TextView receivePartyAddr1two1 = (TextView) viewTwoother.findViewById(R.id.receivePartyAddr1two1);
        receivePartyAddr1two1.setText(orderInfo12.getString("receicePartyDistricict") +"   "+ orderInfo12.getString("receivePartyAddr1"));

        //   订单号  二合一
        TextView omsnotwo = (TextView) viewTwoother.findViewById(R.id.omsnotwo);
        omsnotwo.setText(orderInfo12.getString("omsNo"));

        //  生成条形码   aviationMasterNo
        ImageView imageviewbarcodetwo1 = (ImageView) viewTwoother.findViewById(R.id.imageviewbarcodetwo1);
        Bitmap barcodebitmaptwo1 = createBarcode(orderInfo12.getString("aviationMasterNo"),400);
        imageviewbarcodetwo1.setImageBitmap(barcodebitmaptwo1);

        //  生成条形码内容
        TextView productno = (TextView) viewTwoother.findViewById(R.id.productnotwo1);
        productno.setText(orderInfo12.getString("aviationMasterNo"));

        for (int i = 0; i < scanLabelInfo.size(); i++) {

            scanLabelItem = (JSONObject) scanLabelInfo.get(i);

            //  生成条形码 小联
            ImageView imageviewbarcode14 = (ImageView) viewTwoother.findViewById(R.id.imageviewbarcodetwo3);
            Bitmap barcodebitmap13 = createBarcode(scanLabelItem.getString("productNo"),300);
            imageviewbarcode14.setImageBitmap(barcodebitmap13);

            //   标签号 小联
            TextView productno16 = (TextView) viewTwoother.findViewById(R.id.productnotwo3);
            productno16.setText(scanLabelItem.getString("productNo"));

            int me = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);

            viewTwoother.measure(me,me);

            Bitmap bitmap = Bitmap.createBitmap(viewTwoother.getMeasuredWidth(), viewTwoother.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            if (Build.VERSION.SDK_INT >= 11) {
                viewTwoother.measure(View.MeasureSpec.makeMeasureSpec(viewTwoother.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(viewTwoother.getHeight(), View.MeasureSpec.EXACTLY));
                viewTwoother.layout((int) viewTwoother.getX(), (int) viewTwoother.getY(), (int) viewTwoother.getX() + viewTwoother.getMeasuredWidth(), (int) viewTwoother.getY() + viewTwoother.getMeasuredHeight());
            } else {
                viewTwoother.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                viewTwoother.layout(0, 0, viewTwoother.getMeasuredWidth(), viewTwoother.getMeasuredHeight());
            }
            viewTwoother.draw(canvas);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //设置想要的大小
            int newWidth=770;//96.25   800
            int newHeight=1144;//1155  144.375
//            int newHeight=1200;//1155  144.375

            //计算压缩的比率
            float scaleWidth=((float)newWidth)/width;
            float scaleHeight=((float)newHeight)/height;

            //获取想要缩放的matrix
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth,scaleHeight);
            matrix.postRotate(180);

            //获取新的bitmap
            bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
            bitmap.getWidth();
            bitmap.getHeight();

            zpSDK.Draw_Page_Bitmap_(bitmap,1);//打印标签，打印大数据图片，宽度不要超过576

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
            if(a==0 && i==scanLabelInfo.size()-1)
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

        Set <BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
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
