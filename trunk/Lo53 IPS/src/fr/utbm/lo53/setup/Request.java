package fr.utbm.lo53.setup;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class Request extends AsyncTask<Void, String, Void>
{
	public static final int OFFLINE_REQUEST = 0;
	public static final int ONLINE_REQUEST = 1;
	
	private MainActivity activity;
	private SharedPreferences SharedPreferences;
	private int request_type;
	
	private float posx;
	private float posy;
	private int mapid;

	public Request(MainActivity activity, int request_type)
	{
		this.activity = activity;
		this.request_type = request_type;
	}
	
	public void setOfflineParams(float posx, float posy, int mapid)
	{
		this.posx = posx;
		this.posy = posy;
		this.mapid = mapid;
	}
	
	@Override
	protected Void doInBackground(Void... arg)
	{
		SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		
		HttpClient client = new DefaultHttpClient(); 
		
		String server_address = SharedPreferences.getString("server_address", "");
	    String server_port = SharedPreferences.getString("server_port", "");
	    
	    if(server_address.compareTo("") == 0 || server_port.compareTo("") == 0)
	    {
	    	publishProgress("Fail: Server IP:Port not Defined");
	    	return null;
	    }
	    
	    String getURL = "";
	    
	    if(request_type == OFFLINE_REQUEST)
	    {
	    	getURL = "http://"+server_address+":"+server_port+"/ServerPositionning/MobileCalibrationListener?x="+posx+"&y="+posy+"&map="+mapid;
	    }
	    else if(request_type == ONLINE_REQUEST)
	    {
	    	getURL = "http://"+server_address+":"+server_port+"/ServerPositionning/MobileLocationListener";
	    }
	    	    
	    HttpGet get = new HttpGet(getURL);
	    
		try
		{
			HttpResponse resp = client.execute(get);
			
			if(request_type == ONLINE_REQUEST)
			{
				HttpEntity ent = resp.getEntity();
				BufferedReader is = new BufferedReader(new InputStreamReader(ent.getContent()));
				
				publishProgress(is.readLine());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected void onProgressUpdate(String... obj)
	{
		activity.updateStatus(obj[0]);
	}
}
