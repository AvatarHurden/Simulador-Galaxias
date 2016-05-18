package models;

import java.math.BigInteger;

public enum Scale {
	PLANET("Planet", new BigInteger("100")), 
	STAR_SYSTEM("Star System", new BigInteger("10.000"));
//	STAR_NEIGHBORHOOD, 
//	GALAXY, 
//	LOCAL_GROUP, 
//	LOCAL_SUPERCLUSTER,
//	SUPERCLUSTERS; 

	private String name;
	private BigInteger conversion;
	
	private Scale(String name, BigInteger conversion) {
		this.name = name;
		this.conversion = conversion;
	}
	
}
