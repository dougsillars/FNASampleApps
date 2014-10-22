package com.example.networkactivitysample;
/*
* 
* Sample app by Doug Sillars at AT&T Developer Program
This sample code is based on code found in sample apps on Stack Overflow and on the internet.  
Use at your own risk, neither AT&T or Doug are responsible for any errors, core meltdowns or accidental ballistic missile launches
 that may occur using this code.

This app requires root to use the AT&T Network Attenuator libraries.  
If you want to play with this without Root access, just remove the library and references to it.

What this application does is read the current network state

It then decides whether the network is "fast" "medium" or "slow" based on the network tech.
based on the network speed that the device receives, the application chooses what files to download and serve 
the end user.

The network attenuator library just reads the status from the Network Attenuator. and categorizes into fast mediumand slow


*/

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

//import com.att.android.networkattenuatorlib.api.ARONetworkAttenuator;
//import com.att.android.networkattenuatorlib.api.AbstractNetworkAttenuator;
//import com.att.android.networkattenuatorlib.bean.NetworkConfig;

public class MainActivity extends Activity {
	//AbstractNetworkAttenuator an;
	BroadcastReceiver changeReceiver;
	TextView Networkstate;
	Button button;
	CheckBox FNAcheck;
	int signalStrengthValue;
	int contentlength;
	ImageView BackgroundImage;
	String urlbig = "http://i661.photobucket.com/albums/uu340/dougtest/bridge_525_zps6dd074cb.jpg";
	String urlmed = "http://i661.photobucket.com/albums/uu340/dougtest/bridge_525_50_zps30a24b7d.jpg";
	String urlsmall = "http://i661.photobucket.com/albums/uu340/dougtest/bridge_525_10_zpsf0678e67.jpg";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		Networkstate = (TextView) findViewById(R.id.NetworkState);
		 button = (Button) findViewById(R.id.button1);
		 FNAcheck = (CheckBox) findViewById(R.id.FNAcheck);
		 BackgroundImage =(ImageView) findViewById(R.id.bgimage);
	//	if (savedInstanceState == null) {
		//	getFragmentManager().beginTransaction()
			//		.add(R.id.container, new PlaceholderFragment()).commit();
		//}
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BackgroundImage.setImageDrawable(null);
				Networkstate.setText("");
				System.out.println("button clicked");
				
				
				try {
			//		an = new ARONetworkAttenuator(getApplicationContext());
			//		NetworkConfig nc = an.getNetworkConfig();
					int downlinkspeed = 0;
			//		downlinkspeed = nc.getDownlinkSpeed();
					
					//data from the network
					TelephonyManager teleMan = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				//	PhoneStateListener psl = new PhoneStateListener();  
					//teleMan.listen(psl,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
					
				
				
					//'get operator name
					String operatorname =teleMan.getNetworkOperatorName();
					//'are we roaming?'
					boolean roaming = teleMan.isNetworkRoaming();
					
					//describe the network technology
					String netType = "";
					
					//lets assume the network is fast
					String NetworkSpeed = "fast";
					
					if (downlinkspeed == 0) {
						//'we are not using AT&T Network Attenuator
						//otherwise downlinkspeed will be greater than zero
						
						//'get network type from device'
						int networkType = teleMan.getNetworkType();
						//'convert integer to string'
						
						switch (networkType)
						{
						case 7:
						    netType = "1xRTT";
						    NetworkSpeed = "slow";
						    break;      
						case 4:
							netType = "CDMA";
						    break;      
						case 2:
							netType = "EDGE";
							NetworkSpeed = "slow";
						    break;  
						case 14:
							netType = "eHRPD";
							NetworkSpeed = "slow";
						    break;      
						case 5:
							netType = "EVDO rev. 0";
							NetworkSpeed = "slow";
						    break;  
						case 6:
							netType = "EVDO rev. A";
							NetworkSpeed = "medium";
						    break;  
						case 12:
							netType = "EVDO rev. B";
							NetworkSpeed = "medium";
						    break;  
						case 1:
							netType = "GPRS";
							NetworkSpeed = "slow";
						    break;      
						case 8:
						    netType = "HSDPA";
						    NetworkSpeed = "medium";
						    break;      
						case 10:
							netType = "HSPA";
							NetworkSpeed = "fast";
						    break;          
						case 15:
							netType = "HSPA+";
							NetworkSpeed = "fast";
						    break;          
						case 9:
							netType = "HSUPA";
							NetworkSpeed = "fast";
						    break;          
						case 11:
							netType = "iDen";
							NetworkSpeed = "slow";
						    break;
						case 13:
							netType = "LTE";
							NetworkSpeed = "fast";
						    break;
						case 3:
							netType = "UMTS";
							NetworkSpeed = "medium";
						    break;          
						case 0:
							netType = "Unknown";
							NetworkSpeed = "medium";
						    break;

						}

						Networkstate.setText("Current Setting: \nActiveNetwork: " + netType+"\nSuggested Speed: " +NetworkSpeed+ "\nCarrier: "+ operatorname +"\nRoaming: " +roaming);
					} else {
						
						//network speed is applied by Attenuator
					
						
						switch(downlinkspeed){
						case 80:
							netType = "GPRS";
							NetworkSpeed = "slow";
							break;
						case 236:
							netType = "EDGE";
							NetworkSpeed = "slow";
							break;
						case 1920:
							netType = "UMTS";
							NetworkSpeed = "medium";
							break;
						case 14400:
							netType = "HSPA+";
							NetworkSpeed = "fast";
							break;
						case 22000:
							netType = "LTE";
							NetworkSpeed = "fast";
							break;
					    default:
					    	//if the downlink speed is something else - there must be a custom setting
					    	netType = "Custom";
					    	break;	
						}
						
					Networkstate.setText("Current Setting:\nNetwork Attenuator " + netType + "\nSuggested Speed: " +NetworkSpeed+"\nCarrier: "+ operatorname +"\nRoaming: " +roaming);//"\n" + nc.toString()+
					}
					//now apply steeting determined by FNA checking above..
					//only if the FNA chackbox is enabled
					if (FNAcheck.isChecked() ) {
							switch(NetworkSpeed){
							case "fast":
								new ImageDownloader().execute(urlbig);
								break;
							case "medium":
								new ImageDownloader().execute(urlmed);
								break;
							case "slow":
								new ImageDownloader().execute(urlsmall);
								break;
							}
							Networkstate.setTextColor(Color.WHITE);
					}
					else{
						//we do not want to apply the FNA settings, so use the big image
						new ImageDownloader().execute(urlbig);
						Networkstate.setTextColor(Color.RED);
						Networkstate.append("FNA Settings IGNORED");
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
			

		
	}
	
	
	
	
	
	 private class ImageDownloader extends AsyncTask<String, Void,Bitmap>  {
		 TextView downloadtimes; 
         Long responsetime;
         Long imagetime;
         Long contentlength;
         Double Throughput;

	        @Override
	        protected Bitmap doInBackground(String... param) {
	            // TODO Auto-generated method stub
	            return downloadBitmap(param[0]);
	        }
	 
	        @Override
	        protected void onPreExecute() {
	            Log.i("Async-Example", "onPreExecute Called");
	            downloadtimes =  (TextView) findViewById(R.id.Downloadtimes);
	         
	       //     simpleWaitDialog = ProgressDialog.show(ImageDownladerActivity.this,
	        //            "Wait", "Downloading Image");
	 
	        }
	 
	        @Override
	        protected void onPostExecute(Bitmap result) {
	            Log.i("Async-Example", "onPostExecute Called");
	            BackgroundImage.setImageBitmap(result);
                downloadtimes.append("\n200 response: " + responsetime +"\nDownload: " + imagetime + "\nsize: "+ contentlength +"\nThroughput: " +Throughput);
                downloadtimes.setTextColor(Color.RED);
	            
	       //     simpleWaitDialog.dismiss();
	 
	        }
	 
	        private Bitmap downloadBitmap(String url) {
	        	Long start = System.currentTimeMillis();
	            // initilize the default HTTP client object
	            final DefaultHttpClient client = new DefaultHttpClient();
	 
	            //forming a HttoGet request 
	            final HttpGet getRequest = new HttpGet(url);
	            try {
	 
	                HttpResponse response = client.execute(getRequest);
	 
	                //check 200 OK for success
	                final int statusCode = response.getStatusLine().getStatusCode();
	                Long gotresponse = System.currentTimeMillis();
	                if (statusCode != HttpStatus.SC_OK) {
	                    return null;
	                }
	            
	 
	                final HttpEntity entity = response.getEntity();
	                contentlength = entity.getContentLength();
	                if (entity != null) {
	                    InputStream inputStream = null;
	                    try {
	                        // getting contents from the stream 
	                        inputStream = entity.getContent();
	 
	                        // decoding stream data back into image Bitmap that android understands
	                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	                        Long gotimage = System.currentTimeMillis();
	                        responsetime  = gotresponse - start;
	                        imagetime     = gotimage-start;
	                        Throughput    = ((double)contentlength/1024)/((double)imagetime/1000);  //KB/s
	                        return bitmap;
	                    } finally {
	                        if (inputStream != null) {
	                            inputStream.close();
	                        }
	                        entity.consumeContent();
	                    }
	                }
	            } catch (Exception e) {
	                // You Could provide a more explicit error message for IOException
	                getRequest.abort();
	                Log.e("ImageDownloader", "Something went wrong while" +
	                        " retrieving bitmap from " + url + e.toString());
	            } 
	 
	            return null;
	        }

			
	    }	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        if (changeReceiver == null){
        	changeReceiver = new BroadcastReceiver(){
        		
        		@Override
        		public void onReceive(Context context, Intent intent) {
        			System.out.println("Network config changed");
       // 			System.out.println("New settings: " + an.getNetworkConfig());
        		}
        		
        	};
        }
        
//		registerReceiver(changeReceiver, 
  //       new IntentFilter(AbstractNetworkAttenuator.NETWORK_CONFIG_CHANGED_INTENT));
    }
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (changeReceiver != null){
			unregisterReceiver(changeReceiver);
			changeReceiver = null;
		}
	}


}

