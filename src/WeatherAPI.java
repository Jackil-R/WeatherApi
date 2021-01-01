import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WeatherAPI {
	static Hour currentWI;
	static ArrayList<Hour> weatherData;
	static ArrayList<Location> locations;
	static ArrayList<ArrayList<ArrayList<String>>> weatherDataString;
	static String dayName = "";
	static String location = "";
	public WeatherAPI(String location){
		this.location = location;
		weatherDataString = new ArrayList<ArrayList<ArrayList<String>>>();


		weatherData = new ArrayList<Hour>();
		ArrayList<String> locationRSS = getLocationRSS();
		locations = storeLocation(locationRSS);
		String code = "";

		for(int i=0; i<locations.size(); i++){
			Location locat = locations.get(i);
			if(locat.getCity().equalsIgnoreCase(location)){
				code = locat.getCode();
				break;
			}
		}

		String url6 = "http://uk.weather.com/weather/hourByHour/London+GLA+United+Kingdom+"+code+":1:UK?pagenum=1&nextbeginIndex=0";

		String url12A = "http://uk.weather.com/weather/hourByHour/London+GLA+United+Kingdom+"+code+":1:UK?pagenum=2&nextbeginIndex=6";

		String url12B =  "http://uk.weather.com/weather/hourByHour/London+GLA+United+Kingdom+"+code+":1:UK?pagenum=3&nextbeginIndex=18";

		String url2C = "http://uk.weather.com/weather/hourByHour/London+GLA+United+Kingdom+"+code+":1:UK?pagenum=4&nextbeginIndex=30";

		String url2D = "http://uk.weather.com/weather/hourByHour/London+GLA+United+Kingdom+"+code+":1:UK?pagenum=5&nextbeginIndex=42";

		String current = "http://uk.weather.com/weather/today/London+GLA+United+Kingdom+"+code+":1:UK";

		try {

			ArrayList<String> currentRSS = getRSS(code, new URL(current));
			currentWI = parseCurrent(currentRSS);

			ArrayList<String> firstSix = getRSS(code, new URL(url6));
			parse(firstSix, weatherData);

			ArrayList<String> nextDataA = getRSS(code, new URL(url12A));
			parse(nextDataA, weatherData);

			ArrayList<String> nextDataB = getRSS(code, new URL(url12B));
			parse(nextDataB, weatherData);

			ArrayList<String> nextDataC = getRSS(code, new URL(url2C));
			parse(nextDataC, weatherData);

			ArrayList<String> nextDataD = getRSS(code, new URL(url2D));
			parse(nextDataD, weatherData);

		} catch(Exception e){

		}

	}

	public ArrayList<Hour> getHourlyWeather(){
		return weatherData;
	}

	public Hour getCurrentWeather(){
		return currentWI;
	}

	public ArrayList<ArrayList<ArrayList<String>>> getForecast(){
		return weatherDataString;
	}

	public static Hour parseCurrent(ArrayList<String>currentRss){
		int line=0;
		for(int i=0;i<currentRss.size();i++){
			if(currentRss.get(i).contains("wx-summary-data")){
				line=i;
				break;
			}
		}

		String temp = currentRss.get(line+2);
		temp=temp.substring(temp.indexOf(">")+1, temp.length());
		temp=temp.substring(0,temp.indexOf("<"));
		temp=temp+"º";

		String condition = currentRss.get(line+5);
		condition=condition.substring(condition.indexOf(">")+1, condition.length());
		condition=condition.substring(0,condition.indexOf("<"));
		return new Hour("day","current hour",condition,temp, location);

	}

	static ArrayList<String> getLocationRSS()
	{
		ArrayList<String> lines = new ArrayList<String>();
		try{
			URL url = new URL("https://www.edg3.uk/snippets/weather-location-codes/united-kingdom/");
			URLConnection spoof = url.openConnection();
			spoof.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)" );
			BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
			String strLine = "";
			String finalHTML = "";
			while ((strLine = in.readLine()) != null){
				lines.add(strLine);
			}
			return lines;
		}
		catch(Exception e) {System.err.println("Weather API Exception: "+e);}
		return null;
	}

	int dayCount = 0;
	int dataCount = 0;

	public static void parse(ArrayList<String> rss, ArrayList<Hour> hourly){

		ArrayList<ArrayList<String>> days = new ArrayList<ArrayList<String>>();


		for(int i =0; i<rss.size();i++){
			if(rss.get(i).contains("<h3 class=\"wx-time\">")){
				if(rss.get(i).contains(":"))
					continue;
				else {
					String[] data = parseLines(i, rss);
					hourly.add(new Hour(data[0], data[1], data[2],data[3], location));

					ArrayList<String> hours = new ArrayList<String>();
					hours.add(data[0]);
					hours.add(data[1]);
					hours.add(data[2]);
					hours.add(data[3]);
					hours.add(location);

					System.out.println(dayName);

					if(!dayName.equals(data[0])){
						System.out.println("change");
						weatherDataString.add(days);
						days = new ArrayList<ArrayList<String>>();
						dayName = data[0];
					} else {
						System.out.println("no change");
						days.add(hours);
					}


				}
			}


		}

	}

	public static String[] parseLines(int i,ArrayList<String> a){

		String[] array= new String[4];


		//Gets Day
		String day = "";
		String lineDay = a.get(i+1);
		if(lineDay.contains("<span class=\"wx-label\">")){
			day = lineDay.substring(lineDay.indexOf(">")+1, lineDay.length());
			day = day.substring(0,day.indexOf("<"));
		}

		//Gets Time
		String lineTime = a.get(i);
		String time = lineTime.substring(lineTime.indexOf(">")+1, lineTime.length());

		//Gets Temp
		String temp = "";
		for(int j=i; j<a.size(); j++){
			if(a.get(j).contains("<p class=\"wx-temp\">")){
				String tempLine = a.get(j+1);
				temp = tempLine.substring(0, tempLine.indexOf("<"));
				break;
			}
		}

		//Gets Condition
		String condition = "";
		for(int j=i; j<a.size(); j++){
			if(a.get(j).contains("<p class=\"wx-phrase\">")){
				String conditionLine = a.get(j);
				condition = conditionLine.substring(conditionLine.indexOf(">")+1, conditionLine.length());
				condition = condition.substring(0,condition.indexOf("<"));
				break;
			}
		}

		array[0]=day;
		array[1]=time;
		array[2]=condition;
		array[3]=temp+"º";
		return array;
	}	

	public static ArrayList<Location> storeLocation(ArrayList<String> arr){
		ArrayList<Location> locations = new ArrayList<Location>();
		for(int i=0; i<arr.size(); i++){
			String line = arr.get(i);
			if(line.contains("UKXX")){
				String city = line.substring(0, 8);
				String code = line.substring(10, line.length());
				locations.add(new Location(code, city));
			}
		}
		return locations;
	}

	public static ArrayList<String> getRSS(String code, URL url)
	{
		ArrayList<String> lines = new ArrayList<String>();
		try{
			URLConnection spoof = url.openConnection();
			spoof.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)" );
			BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
			String strLine = "";
			String finalHTML = "";
			while ((strLine = in.readLine()) != null){
				lines.add(strLine);
			}
			return lines;
		}
		catch(Exception e) {System.err.println("Weather API Exception: "+e);}
		return null;
	} 

}
