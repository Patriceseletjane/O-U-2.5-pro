package com.ou25pro.app;
import android.app.Activity; import android.os.Bundle; import android.webkit.WebView; import android.webkit.WebSettings;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b); WebView w=new WebView(this); WebSettings s=w.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); w.loadUrl("file:///android_asset/www/index.html"); setContentView(w);}
}