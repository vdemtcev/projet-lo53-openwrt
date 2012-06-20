package fr.utbm.lo53.setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	LinearLayout mainLayout;
	
	Button setPoint;
	Button measure;
	
	TextView posX;
	TextView posY;
	TextView mapId;
	
	TextView Result;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        setPoint = (Button) findViewById(R.id.btn_set_point);
        setPoint.setOnClickListener(this);
        
        measure = (Button) findViewById(R.id.btn_measure);
        measure.setOnClickListener(this);
        
        posX = (TextView) findViewById(R.id.positionx);
        posY = (TextView) findViewById(R.id.positiony);
        mapId = (TextView) findViewById(R.id.mapid);
        
        Result = (TextView) findViewById(R.id.result);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	int id = item.getItemId();
    	
        // Handle item selection
        switch (id)
        {
            case R.id.item_prefs:
            	Intent settingsActivity = new Intent(getBaseContext(), PreferencesActivity.class);
            	startActivity(settingsActivity);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    public void updateStatus(String status)
    {
    	Result.setText(status);
    }

    
	@Override
	public void onClick(View view)
	{		
		switch(view.getId())
		{
			case R.id.btn_set_point:
			{
				// let's make a http request.
				float positionx = 0;
				float positiony = 0;
				int mapid = 0;
				
				try
				{
					positionx = Float.parseFloat(posX.getText().toString());
					positiony = Float.parseFloat(posY.getText().toString());
					mapid = Integer.parseInt(mapId.getText().toString());
				}
				catch(NumberFormatException e)
				{
					Toast.makeText(getApplicationContext(), "NaN Exception.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Toast.makeText(getApplicationContext(), "SetPoint in progress...", Toast.LENGTH_SHORT).show();
				
				Request req = new Request(this, Request.OFFLINE_REQUEST);
				req.setOfflineParams(positionx, positiony, mapid);
				req.execute();
				
				break;
			}
			case R.id.btn_measure:
			{
				Toast.makeText(getApplicationContext(), "Measure in progress...", Toast.LENGTH_SHORT).show();
				
				Request req = new Request(this, Request.ONLINE_REQUEST);
				req.execute();
				
				break;
			}
		}
	}
}

