package dora.tempview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        tv = findViewById(R.id.tv);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone0");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone2");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone3");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone4");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone5");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone14");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone16");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone19");
                new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone21");
                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone0");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone2");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone3");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone4");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone5");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone14");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone16");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone19");
        new asyncGrabTemp().execute("/sys/class/thermal/thermal_zone21");
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private class asyncGrabTemp extends AsyncTask<String, String, String> {

        String result;
        String comp;
        String dir;

        @Override
        protected String doInBackground(String... params) {
            try {
                // Executes the command.
                Process process = Runtime.getRuntime().exec("cat " + params[0] + "/temp");

                // Reads stdout.
                // NOTE: You can write to stdin of the command using
                //       process.getOutputStream().
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();

                // Waits for the command to finish.
                process.waitFor();

                String[] tmp = params[0].split("/");
                dir = tmp[tmp.length-1];

                result = output.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (dir){
                case "thermal_zone0":{
                    comp = "battery"; break;
                }
                case "thermal_zone5":{
                    comp = "emmc"; break;
                }
                case "thermal_zone2":{
                    comp = "flash_therm"; break;
                }
                case "thermal_zone4":{
                    comp = "msm_therm"; break;
                }
                case "thermal_zone3":{
                    comp = "pm8994_tz"; break;
                }

                case "thermal_zone14":
                case "thermal_zone16":{
                    comp = "cpu0,1"; break;
                }

                case "thermal_zone19":
                case "thermal_zone21":{
                    comp = "cpu2,3"; break;
                }

            }
            tv.append(comp + " (" + dir + ") => " + result + "\n");
        }

        @Override
        protected void onPreExecute() {
        }
    }

}
