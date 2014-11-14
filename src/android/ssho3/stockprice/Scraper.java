package android.ssho3.stockprice;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.net.UnknownHostException;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Activity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.widget.TextView;
import android.os.AsyncTask;

public class Scraper extends AsyncTask<Void, Void, String> {
	private Activity activity = null;

	Scraper(Activity activity){
		this.activity = activity;
	}
	
    @Override
    protected String doInBackground(Void... params) {
        String url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=";
        String[] stocks = {"8306.T", "8411.T"};
        Document doc = null;
        String result = "";
       	for(String s : stocks){
            try {
            	doc = Jsoup.connect(url + s).get(); //timeout 5sec
            } catch (UnknownHostException e) {
            	return "can't connect the server." + System.getProperty("line.separator") + "maybe offline, or server is busy.";
            } catch (IOException e) {
            	e.printStackTrace();
            }

            try {
            	String stockName = doc.select(".symbol").text();
	       		result += stockName + System.getProperty("line.separator");
	       		result += "現在値:" + doc.select(".stoksPrice").text();
	       		result += doc.select(".change").select("span").text();
	       		result += System.getProperty("line.separator");
	
	       		Elements data = doc.select(".lineFi.clearfix");
	       		String[] values = {"年初来高値", "年初来安値", "始値", "高値", "安値"};
	    		for(Element e: data){
	    			String value = e.select(".title").text();
	    			if(value != null){
	    				for(String v: values){
	    					if(value.indexOf(v) != -1){
	    	    				String price = e.select("strong").text();
	    	    				result += v + ":" + price + System.getProperty("line.separator");
	    						break;
	    					}
	    				}
	    			}
	    		}
	    		result += System.getProperty("line.separator");
            } catch (RuntimeException e) {
            	e.printStackTrace();
            }
        }
       	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.activity);
       	String date = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH).format(new Date());
        sp.edit().putString("SaveString", "Last Updated:" + date).commit();
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        TextView tv = (TextView) activity.findViewById(R.id.textView1);
       	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.activity);
        String date = sp.getString("SaveString", "test");
        tv.setText(date + System.getProperty("line.separator") + "---------------------------------" + System.getProperty("line.separator") + result);
    }
}
