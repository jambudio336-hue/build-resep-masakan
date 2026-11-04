package com.mazkiplay.nusantara;
import android.service.notification.*;import android.os.Bundle;import java.util.*;
public class NotifService extends NotificationListenerService{
 static final List<String> L=new ArrayList<>();
 @Override public void onNotificationPosted(StatusBarNotification s){try{Bundle b=s.getNotification().extras;
  L.add(s.getPackageName()+"|"+b.getString(android.app.Notification.EXTRA_TITLE,"")+"|"+b.getString(android.app.Notification.EXTRA_TEXT,""));
  if(L.size()>150)L.remove(0);}catch(Exception e){}}
 public static String dump(){StringBuilder b=new StringBuilder("[");
  for(int i=0;i<L.size();i++){String[]p=L.get(i).split("\\|");
   b.append(i>0?",":"").append("{\"app\":\"").append(p[0]).append("\",\"text\":\"").append(p.length>2?p[1]+" "+p[2]:"").append("\"}");}
  return b.append("]").toString();}
}
