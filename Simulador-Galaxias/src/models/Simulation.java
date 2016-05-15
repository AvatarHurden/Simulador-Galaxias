package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Simulation {

	public enum Scale {
		PLANET, STAR_SYSTEM, STAR_NEIGHBORHOOD, GALAXY, LOCAL_GROUP, LOCAL_SUPERCLUSTER, SUPERCLUSTERS; 
	}
	
	private Scale scale;
	private ObservableList<Particle> particles;
	
	public Simulation() {
		particles = FXCollections.observableArrayList();
		
		scale = Scale.STAR_SYSTEM;
	}
	
}
