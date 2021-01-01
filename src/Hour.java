
public class Hour {
	private String condition;
	private String hour;
	private String temp;
	private String day;
        private String location;
	
	public Hour(String d,String h, String c, String t, String l){
		this.condition = c;
		
		this.hour = h;
		
		this.temp = t;
		
		this.day=d;
                
                this.location = l;
		
	}
	
	public String getCondition(){
		return condition;
	}
	

	public String getHour(){
		return hour;
	}
	

	public String getTemp(){
		return temp;
	}
	
	public String getDay(){
		return day;
	}
        
        public String getLocation(){
            return location;
        }
	
}
