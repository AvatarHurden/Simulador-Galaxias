package models;

public enum Scale {
	
	BASIC("Basic", 1e4, 0.4, 5e5),
	PLANET("Planet", 5.972e24, 1.496e9, 3600*24),
	STAR_SYSTEM("Star System", 5.972e24, 1.496e7, 3600*3);
//	STAR_NEIGHBORHOOD, 
//	GALAXY, 
//	LOCAL_GROUP, 
//	LOCAL_SUPERCLUSTER,
//	SUPERCLUSTERS; 

	private String name;
	// Mass converts to kg, distance converts to m
	private double massConversion, distanceConversion;
	// Simulation step, in seconds
	private double timeStep;
	

	private Scale(String name, double massConversion , double distanceConversion, double timeStep) {
		this.name = name;
		this.massConversion = massConversion;
		this.distanceConversion = distanceConversion;
		this.timeStep = timeStep;
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

	public double getTimeStep() {
		return timeStep;
	}
}
