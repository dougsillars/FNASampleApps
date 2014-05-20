package com.example.scrollviewexample;

/*
 * 
 * Sample app by Doug Sillars at AT&T Developer Program
 This sample code is based on code found in sample apps on Stack Overflow and on the internet.  
 Use at your own risk, neither AT&T or Doug are responsible for any errors, core meltdowns or accidental ballistic missile launches
  that may occur using this code.
 
 
this extends a scroll view sample app found on the web to include RTT measurements.
To accurately measure the distances in the scroll view, I need to know when the scrolling has stopped. 
I grabbed that got from StackOverflow (referenced in the class

What is added here are the measurements of latency (RTT).
Note that there can be 3 round trips not 2 on initial query due to DNS lookup. 
Since this sample app grabs 10 items from the same domain, but averages the last 5 for RTT, 
I figure that the 3*RTT sample will quickly fall out of the measurement window.
Its a sample app for goodness sake :)

The 2 round trips can have a lot of variance (150 and 400 average to 225ms), 
so I take the 5 most recent connections to measure the Average RTT across 10 measurements.

Then based on the AvgRTT, the app can make decisions on how to pre-fetch content in the scroll view




*/

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.scrollviewexample.MyScrollView.OnScrollStoppedListener;

public class MainActivity extends Activity {
	 private static final String[] urls = {
	    	"http://i661.photobucket.com/albums/uu340/dougtest/001-20140412_124543_000_zpsee3872d4.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/002-20140412_124543_001_zpsd2637741.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/005-20140412_124543_004_zpsff83e911.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/007-20140412_124543_006_zps4663f753.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/008-20140412_124543_007_zpsd008389d.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/009-20140412_124543_008_zps853339fa.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/010-20140412_124543_009_zpsfb88b8c9.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/011-20140412_124543_010_zps37198ad2.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/012-20140412_124543_011_zps3ae462ce.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/013-20140412_124543_012_zps6b1f4ea9.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/014-20140412_124543_013_zps2ca4fb81.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/015-20140412_124543_014_zps24bed07b.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/016-20140412_124543_015_zps99db6841.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/017-20140412_124543_016_zpsbc5a0a30.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/018-20140412_124543_017_zps745dddaa.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/019-20140412_124543_018_zpsd27df026.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/020-20140412_124543_019_zps7e12ac38.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/021-20140412_131435_000_zpsbbe4db57.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/022-20140412_131435_001_zpse9930d1b.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/023-20140412_131435_002_zpse9f8559a.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/024-20140412_131435_003_zps8bf54244.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/025-20140412_131435_004_zpsbda95e71.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/026-20140412_131435_005_zps45b7c45d.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/027-20140412_131435_006_zpscfbd9a10.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/028-20140412_131435_007_zps8af20f4b.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/029-20140412_131435_008_zps27391320.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/030-20140412_131435_009_zps8914eb07.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/031-20140412_131435_010_zps28d4abda.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/032-20140412_131435_011_zps108a4c15.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/033-20140412_131435_012_zps9666d598.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/034-20140412_131435_013_zpsf37a951a.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/035-20140412_131435_014_zps7fec282b.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/036-20140412_131435_015_zps01926434.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/037-20140412_131435_016_zps29947e82.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/038-20140503_100914_zps09a5fbf6.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/039-20140503_1009140_zpsce778d94.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/040-20140503_100915_zps084a088d.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/041-20140503_1009170_zpsa45a6b3c.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/042-20140503_100917_zps054bd999.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/043-20140503_100918_zpsa74d853f.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/044-20140503_100919_zps7c633112.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/045-20140505_163515_000_zps174ef524.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/046-20140505_163515_001_zps32068e33.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/047-20140505_163515_002_zpsfd656222.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/048-20140505_163515_003_zps18500890.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/049-20140505_163515_004_zpsa7894179.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/050-20140505_163515_005_zpsd3a82b94.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/051-20140505_163515_006_zps3a5a2ecf.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/052-20140505_163515_007_zps393efd74.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/053-20140505_163515_008_zps90f498a5.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/054-20140505_163515_009_zpsaf7f227c.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/055-20140505_163515_010_zps6974ed06.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/056-20140505_163515_011_zpsd7c82b25.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/057-20140505_163515_012_zps66447321.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/058-20140505_163515_013_zps82da5d95.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/059-20140506_181502_009_zps54b019b1.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/060-20140506_181502_010_zps1bd2a02a.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/061-20140506_181502_011_zpsd0b1e529.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/062-20140506_181502_012_zps02f62118.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/063-20140506_181502_013_zpsf6027c34.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/064-20140506_181502_014_zpsb67e7d14.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/065-20140506_181502_015_zpsffa92627.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/066-20140506_181502_016_zpsdf02de36.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/067-20140506_181502_017_zps6f5e8457.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/068-20140506_181502_018_zps2d161588.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/069-20140506_181502_019_zpsd6432bd6.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/070-20140506_181502_020_zpsd94c2e6f.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/071-20140506_181502_021_zpsf1edb1d0.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/072-20140506_181502_022_zpsa928cf07.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/073-20140506_181502_023_zpsaf5c3638.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/074-20140506_181502_024_zpsde9694aa.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/075-20140506_181513_000_zps58d2d4db.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/076-20140506_181513_001_zps8235c926.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/077-20140506_181513_002_zpsce256b41.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/078-20140506_181513_003_zps35d9e540.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/079-20140506_181513_004_zps931950d5.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/080-20140506_181513_005_zps092c3120.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/081-20140506_181513_006_zps1e34b1c2.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/082-20140506_181513_007_zpsb7454b49.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/083-20140506_181513_008_zps51e9ce6d.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/084-20140506_181513_009_zps98625a1d.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/085-20140506_181513_010_zps0411bf20.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/086-20140506_181513_011_zps165363ba.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/087-20140506_181513_012_zps249379f5.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/088-20140506_181513_013_zpsbd977965.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/089-20140506_181513_014_zpsa497b61e.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/090-20140506_181513_015_zpsa0a43e53.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/091-20140506_181513_016_zps61d1ba70.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/092-20140506_181513_017_zps376e8528.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/093-20140506_181513_018_zps4b94c446.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/094-20140506_181513_019_zps9d05a590.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/095-20140506_181513_020_zps3dd6efe7.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/096-20140506_181513_021_zps6c3e9396.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/097-20140506_181513_022_zps57e29be8.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/098-20140506_181513_023_zps3c08116a.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/099-20140506_181513_024_zpsd42f16ea.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/100-20140506_181513_025_zps7a2e0964.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/101-20140506_181513_026_zpsb87051ce.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/102-20140506_181513_027_zps339293a6.jpg",
	    	"http://i661.photobucket.com/albums/uu340/dougtest/103-20140506_181513_028_zps0fb73c35.jpg",


	    };
    public static int counter = 0;
	RelativeLayout rl1,rl2;
    MyScrollView sv;
    public float Swipedto;
    
    private static ImageView[] b;
    //need initial scroll view height for comparisons later 
    int InitialScrollViewHeight=30;
    int ScrollViewHeight=InitialScrollViewHeight;
	int imageheight = 500;
	int totalimagecount = 0;
	int ImagestoAdd = 10;
	Long responsetime;
	Long imagetime;
	Long RTT;
	Double AvgRTT;
	
	public static  ArrayList<Integer> RTTtimes=  new ArrayList<Integer>();
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        rl1=(RelativeLayout) findViewById(R.id.rl);
        sv=new MyScrollView(MainActivity.this, null);
        final RelativeLayout rl2 = new RelativeLayout(MainActivity.this);
        b=new ImageView[100];
        sv.addView(rl2);
        rl1.addView(sv);
        
        //get screen height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int screenHeight = size.y;
        //could get width too if needed
        
        //add images
        
        
        totalimagecount = Imagelooper(ImagestoAdd, totalimagecount, rl2);
        //the height of the Scrollview has increased by the # of images added * the height of the images
       // ScrollViewHeight = ScrollViewHeight +ImagestoAdd*imageheight;


        sv.setOnTouchListener(new OnTouchListener() {
	

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				//call the class to figure out where we stop
				if (event.getAction() == MotionEvent.ACTION_MOVE) {

	                sv.startScrollerTask();
	            }

				return false;
			}});
        	  
        sv.setOnScrollStoppedListener(new OnScrollStoppedListener() {

            public void onScrollStopped() {
            	//get location in scrollview after stop
            	//getScrollY() is the top of the screen.. I want the bottom
            	sv.getScrollY();
            	int bottomofscreen = sv.getScrollY() + screenHeight +InitialScrollViewHeight;
            	//how much scrollview is blwo the fold? How many images?
		    	  int scrollBelowtheFold = ScrollViewHeight  - bottomofscreen;
		    	  //#images belwo the fold
		    	   int ImagesBelowtheFold = Math.round(scrollBelowtheFold/imageheight);

                Log.i("scrolling stopped", "stopped");
                Log.i("pixels below fold ", scrollBelowtheFold + " #images: "+ImagesBelowtheFold);
     
                
                //ok  now build in the logic to request more images.
                AvgRTT = RTTAverage();
                Log.i("averageRTT", AvgRTT.toString());
                //if we are dealing with *normal latency*
               
                if  (AvgRTT< 500){
                	//latency is under 1second
                	///begin download when #of images remaining is 1
                	//download default images to add
                	if (ImagesBelowtheFold<2){
                		totalimagecount =Imagelooper(ImagestoAdd, totalimagecount, rl2);
                	}
                	
                }
                //medium latency
                else if(AvgRTT< 1500){
                	//initiate download earlier
                	if (ImagesBelowtheFold<5){
                		//no extra images
                		totalimagecount =Imagelooper(ImagestoAdd, totalimagecount, rl2);
                	}
                	
                }
                else{
                	//latency is OVER 1.5s
                	//start earler and get more images
                	//
                	if (ImagesBelowtheFold<5){
                		totalimagecount =Imagelooper(ImagestoAdd*2, totalimagecount, rl2);
                	}
                	
                }
                
            }
    });
          
    
    

    	
    	
    	
    }
    
     public int Imagelooper(int numberofaddedimages, int TotalImageCount, RelativeLayout rl){
  	  for(int i=0;i<numberofaddedimages;i++)
        {
  		  	//we are adding numberofaddedimages to the already populated list TotalImageCount
  		  	//to correctly indext this, add i to Total image count
  		  	TotalImageCount = 1+TotalImageCount;
        	 b[TotalImageCount]=new ImageView(this);
        	
	//    b[i]=new ImageView(this);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(
            		(int)LayoutParams.WRAP_CONTENT,(int)LayoutParams.WRAP_CONTENT);
	   
            params.topMargin=ScrollViewHeight;
            params.leftMargin=50;
            
	  //  b[i].setText("Button "+i);
 //           counter =i;
            rl.addView(b[TotalImageCount]);
           b[TotalImageCount].setLayoutParams(params);
            new ImageDownloader().execute(TotalImageCount);
            ScrollViewHeight = ScrollViewHeight+imageheight;
			
        }  
  	
  	return TotalImageCount;
	 
}  
    
     public  Double RTTAverage() {
     	//have RTT as Long
     	//add it to the array
     //	RTTtimes.add(RTT);
     	//average 5 values
     	int arraylength = RTTtimes.size();
     	
     	int count =5;
     	int sum = 0;
     	//unless there are less than 5 in the array
     	//do not use the average - return null and use teh default.
     	
     	if (arraylength>=count){
     		
 		    	for (int i=0; i< count; i++) {
 		    		if (RTTtimes.get(arraylength -1 -i) != null){
 		    			sum = sum + RTTtimes.get(arraylength -1 -i);
 		    		}
 		    	}
     	}
     	
     	double average = sum/count;
     	
     	return average;
     	
     	} 
     
     
     
    
  
	private class ImageDownloader extends AsyncTask<Integer, Void,Bitmap>  {


			@Override
	        protected Bitmap doInBackground(Integer... param) {
	            // TODO Auto-generated method stub
			  
	            return downloadBitmap(param[0]);
	        }
	 
	   

			@Override
	        protected void onPreExecute() {
				
	            Log.i("Async-Example", "onPreExecute Called");

	 
	        }
	 
	        @Override
	        protected void onPostExecute(Bitmap result) {
	            Log.i("Async-Example", "onPostExecute Called");
	          
				b[counter].setImageBitmap(result);

	        }
	 
	        private Bitmap downloadBitmap(Integer j) {
	        	//from interger counter, get url
	        	
	        	String url = urls[j];
	             counter =j;
	            

	        	
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
	                    Log.w("ImageDownloader", "Error " + statusCode + 
	                            " while retrieving bitmap from " + url);
	                    return null;
	 
	                }
	 
	                final HttpEntity entity = response.getEntity();
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
	                        //since data is from same server assume just 2 RTT
	                        RTT = responsetime/2;
	                        RTTtimes.add(RTT.intValue());
	                        
	                        
	                        Log.i("ImageDownloader", "image" + j +"responsetime (2RTT): "+ responsetime.toString());
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
    
    
}