package com.handict.superapp_mobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.handict.superapp_mobile.AlPay.PayResult;
import com.handict.superapp_mobile.AlPay.ssa;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import java.io.File;
import java.util.Map;

import android.support.v4.content.FileProvider;
import android.os.Environment;

import static android.content.ContentValues.TAG;
import static com.alipay.b.a.a.b.b.s;

//public class MainActivity extends UnityPlayerActivity {
public class MainActivity extends Activity {
    Context mContext = null;
    private String mUnityObjStr;
    public String custOrderId = "dingdanweishengcheng";
    private final String TAG = getClass().getSimpleName();
    private static final String INNER_URL = "http://api.yolkworld.net/Payment/AsgWxPay";
    String sign;
    private static final int SDK_PAY_FLAG = 10;
    String key = "#YIE9HNqIMvRaU8u";
    String alAppId = "2017090108503276";
    String wxAppId = "wx52d31ccb73e0222f";
    private static String tag = "Unity MainActivity";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    OpenFile();
                    break;
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付成功123", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付失败123", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private AsyncTaskUtil mDownloadAsyncTask;
    private String curAPKName;
    public float size;
    public static MainActivity instance;
    UMWeb web ;
    UMImage image ;
    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        setContentView(R.layout.activity_main);

        instance = this;

//        custOrderId = getCustOrderId("ASG");
        mContext = this;
        UMGameAgent.setDebugMode(true);//设置输出运行时日志
        UMGameAgent.init(this);
    }


    //    public void StartActivity1(String url)
//    {
//        Log.d("1111", "StartActivity1: ----->");
//        Intent intent = new Intent();
//        intent.setClass(this, SkinActivity.class);
//
//        intent.putExtra("url", url);
//        this.startActivity(intent);
//    }
    private void StartActivity1(String url) {

//        String url = urlEdit.getText().toString();
//        String url="http://handict-supperapp-course.oss-cn-hangzhou.aliyuncs.com/ASGVIDEO/SG_dazuiyu.mp4";
        Intent intent = new Intent();
        intent.setClass(this, SkinActivity.class);
        intent.putExtra("type", "localSource");
        intent.putExtra("url", url);
        startActivity(intent);
    }

    //友盟
    public void onResume() {
        super.onResume();
        UMGameAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        UMGameAgent.onPause(this);
    }

    //ID
    public String getAndroidId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    //获取渠道号
    public String getChannelId() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString("UMENG_CHANNEL");
            return msg;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //自定义事件
    public void customEvent(String eventId) {
        MobclickAgent.onEvent(mContext, eventId);
    }

    //获得订单号
    public String GetCustId() {
        return custOrderId;
    }

    private String getCustOrderId(String s) {
        return System.currentTimeMillis() + (int) (Math.random() * 100 + 1) + "_" + s + "_wxapp";
    }

    //获得实体名称
    public void SetObjString(String objStr) {
        mUnityObjStr = objStr;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //获得IP
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    //############################################################### 添加整包更新功能
    public void SetSize(float size) {
        size = this.size;
        Log.d(tag, this.size + "");
    }

    public void StartLoad(String httpUrl, String apkName) {
        curAPKName = apkName;
        mDownloadAsyncTask = new AsyncTaskUtil(MainActivity.this, mHandler);
        mDownloadAsyncTask.execute(httpUrl, apkName);//必须传入两个参数——参数1：url；参数2：文件名（可以为null）
    }

    private void OpenFile() {
        String str = curAPKName;
        Log.d(tag, "Call OpenFile Function : " + str + "   " + android.os.Build.VERSION.SDK_INT + " " + android.os.Build.VERSION_CODES.N);
        String fileName = mDownloadAsyncTask.getFolderPath() + str;
        File file = new File(fileName);
        if (!file.exists()) {
            Log.d(tag, "file not exists ,fileName:" + fileName);
        }

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
//        Log.d(tag, "fileName:" + fileName);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File apkFile = new File(Environment.getExternalStorageDirectory() + "/AsyncTaskDownload/" + curAPKName);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.handict.superapp_mobile.provider", apkFile);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }

        startActivity(install);
    }

    public void onClick(View V) {
//        StartLoad("http://vd.handict.net/MobileReleaseVersion/Android/AppFiles/test.apk", "test.apk");
        switch (V.getId()) {
            case R.id.btn1:
                postKeyValueAl();
                break;
            case R.id.btn2:
//                ShareImage("http://img3.imgtn.bdimg.com/it/u=2955069885,888428178&fm=27&gp=0.jpg");
                ssa.ass();
                break;
            case R.id.btn3:
//                ShareWeb("https://www.baidu.com/baidu?word=%E5%8F%8B%E7%9B%9F&ie=utf-8&tn=myie2dg&ch=6","百度搜索","https://ss3.baidu.com/-rVXeDTa2gU2pMbgoY3K/it/u=3344021281,2244064717&fm=202&mola=new&crop=v1","友盟（Umeng），2010年4月在北京成立，是中国最专业、最有数据凝聚力的移动开发者服务平台");
                com.handict.superapp_mobile.demo.ssa.ass();
                break;
        }
    }



    //--------------------------------------支付宝支付分割线-------------------------------------------------------
    private void postKeyValueAl() {
        //http://zhushou.72g.com/app/gift/gift_list/
        //请求参数`platform=2&gifttype=1&compare=60841c5b7c69a1bbb3f06536ed685a48
        OkHttpClient okHttpClient = new OkHttpClient();
        //TODO 和Get方式不一样的地方是， Post方式的请求参数 必须放入到请求体中才可以。
        //如果提交键值对，我们需要通过new FormBody.Builder()添加键值对。
        custOrderId = getCustOrderId("ASG");
        String stringA = "appId=wx52d31ccb73e0222f&orderNo=" + custOrderId + "&key=#YIE9HNqIMvRaU8u";
        String s = "appId=" + alAppId + "&orderNo=" + GetCustId() + "&key=" + key;
        sign = md5(s);//注：MD5签名方式
        RequestBody requestBody = new FormBody.Builder()
                .add("ProductCode", "QUICK_MSECURITY_PAY")
                .add("OrderNo", custOrderId)
                .add("Price", "0.01")
                .add("ClientIp", getIPAddress(this))
                .add("ProductName", "爱手工VIP会员·季卡")
                .add("Sign", sign)

                .build();
        Log.i(TAG, "custOrderId: " + custOrderId);
        Log.i(TAG, "Sign: " + sign);
        Request request = new Request.Builder()
                .url("http://api.yolkworld.net/Payment/AsgAliPay")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                final String result33 = body.string();
                Log.i(TAG, "result33 : " + result33);
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        Log.i(TAG, "run: " + "123");
                        PayTask alipay = new PayTask(MainActivity.this);
                        Map<String, String> result = alipay.payV2(result33, true);
                        Log.i(TAG, "result:" + result.toString());

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                Thread payThread = new Thread(payRunnable);
                payThread.start();
//                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
//				Toast.makeText(PayActivity.this, result, Toast.LENGTH_SHORT).show();

            }
        });
    }
   /* public void postKeyValueAl(){
        Log.i(TAG, "postKeyValueAl: qqqqqqqqqqqqqqqqqqqqqqq");
    }*/

    //--------------------------------------微信分享分割线-------------------------------------------------------


    private void setUMImage(UMImage image) {
        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
//        压缩格式设置
        image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


    }
    UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调，可以用来处理等待框，或相关的文字提示
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
  /**
     *
     *
     * @param imageUrl 缩略图链接地址
     */
    public void ShareImage(String imageUrl) {

        image= new UMImage(this,imageUrl);//资源文件
        setUMImage(image);
        new ShareAction(MainActivity.this)
//                .withMedia(web)
                .withMedia(image)
                .setDisplayList( SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener).open();
    }
    /**
     *
     * @param webUrl :分享链接地址
     * @param title :分享标题
     * @param imageUrl :缩略图链接地址
     * @param description   :分享描述
     */


    public void ShareWeb(String webUrl,String title,String imageUrl,String description) {
        image= new UMImage(this,imageUrl);//资源文件
        setUMImage(image);
        web= new UMWeb(webUrl);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(description);//描述
        new ShareAction(MainActivity.this)
                .withMedia(web)
                .setDisplayList( SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener).open();
    }
}