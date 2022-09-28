package com.boras.rate_calc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    private String TAG = WebViewActivity.class.getSimpleName();

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        //상태바 숨기기*/

        setContentView(R.layout.activity_mainview);

        webView = (WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient());  // 새 창 띄우기 않기


        webView.getSettings().setLoadWithOverviewMode(true);  // WebView 화면크기에 맞추도록 설정 - setUseWideViewPort 와 같이 써야함
        webView.getSettings().setUseWideViewPort(true);  // wide viewport 설정 - setLoadWithOverviewMode 와 같이 써야함

        webView.getSettings().setSupportZoom(false);  // 줌 설정 여부
        webView.getSettings().setBuiltInZoomControls(false);  // 줌 확대/축소 버튼 여부

        webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 사용여부
//        webview.addJavascriptInterface(new AndroidBridge(), "android");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
        webView.getSettings().setSupportMultipleWindows(true); // 멀티 윈도우 사용 여부

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
            {
                // 사용자가 웹뷰에서 클릭한 정보를 획득
                WebView.HitTestResult result = view.getHitTestResult();

                // url 획득
                String data = result.getExtra();

                // url을 open
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }

            //웹뷰에 alert창에 url을 제거한다.
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            }
            //웹뷰에 Confirm창에 url을 제거한다.
            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {

                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .create()
                        .show();

                return true;
            }
        });


        webView.getSettings().setDomStorageEnabled(true);  // 로컬 스토리지 (localStorage) 사용여부

        boolean netWorkCheck = netWork();

        if (!netWorkCheck)
            showDialog();

    }

    private boolean netWork(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();
        if(ninfo == null){
            return false;
        }else{
            webView.loadUrl("http://221.143.40.24:58080");
            return true;
        }
    }

    void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("네트워크 오류")
                .setMessage("네트워크가 연결되어 있지 않습니다.\n재연결 후 실행 해주세요")
                .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

}
