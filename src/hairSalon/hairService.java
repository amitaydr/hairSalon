package hairSalon;

public class hairService {
	int duration=1; 
	String name;

	public hairService(String s) {
		name = s;
		System.out.println("hairService string received: "+ s);
		if (s.equals("Women haircut")) duration = 2;
		else if (s.equals("Bride treatment")) duration=3;
		System.out.println("hairService.duration: "+ duration);
	}
	
	public int getDuration() {
		return duration;
	}

	public String getName() {
		return name;
	}

}
