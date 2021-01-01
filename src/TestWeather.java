import java.util.ArrayList;

public class TestWeather
{

	public static void main(String[] args)
	{
		WeatherAPI api = new WeatherAPI("London");
		ArrayList<ArrayList<ArrayList<String>>> ls = api.getForecast();
                System.out.println(ls.size());
		
	}
}
