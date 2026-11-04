package com.mazkiplay.nusantara;
import android.app.Activity;import android.os.Bundle;import android.view.WindowManager;import android.webkit.*;
public class LockActivity extends Activity{
 @Override protected void onCreate(Bundle b){super.onCreate(b);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
  WebView w=new WebView(this);setContentView(w);w.getSettings().setJavaScriptEnabled(true);w.getSettings().setAllowFileAccess(true);
  w.addJavascriptInterface(new Object(){@JavascriptInterface public void buka(){runOnUiThread(()->finish());}},"Lock");
  w.loadUrl("file:///android_asset/lock.html");}
}
