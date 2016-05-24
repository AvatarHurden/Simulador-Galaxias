package models;

public enum Scale {
	
	PLANET("Planet", 5.972e24, 1.496e7),
	STAR_SYSTEM("Star System", 5.972e24, 1.496e7);
//	STAR_NEIGHBORHOOD, 
//	GALAXY, 
//	LOCAL_GROUP, 
//	LOCAL_SUPERCLUSTER,
//	SUPERCLUSTERS; 

	private String name;
	// Mass converts to kg, distance converts to km
	private double massConversion, distanceConversion;
	// Time converts to milliseconds, with the step being multiplied by the conversion (
	private double timeConversion, timeStep;
	

	private Scale(String name, double massConversion , double distanceConversion) {
		this.name = name;
		this.massConversion = massConversion;
		this.distanceConversion = distanceConversion;
	}

	public String getName() {
		return name;
	}

	public double getMassConversion() {
		return massConversion;
	}

	public double getDistanceConversion() {
		return distanceConversion;
	}

	public double getTimeUnit() {
		return timeConversion;
	}

	public double getTimeStep() {
		return timeStep;
	}
}
