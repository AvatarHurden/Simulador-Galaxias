package models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Simulation {

	public enum Scale {
		PLANET, STAR_SYSTEM, STAR_NEIGHBORHOOD, GALAXY, LOCAL_GROUP, LOCAL_SUPERCLUSTER, SUPERCLUSTERS; 
	}
	
	private File sourceFile;
	
	private Scale scale;
	private ObservableList<Particle> particles;
	
	public Simulation() {
		particles = FXCollections.observableArrayList();
		
		scale = Scale.STAR_SYSTEM;
	}

	public File getSourceFile() {
		return sourceFile;
	}
	
	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
	}

	public ObservableList<Particle> getParticles() {
		return particles;
	}
	
	public Particle createNewParticle(double x, double y) {
		Particle p = new Particle("Particle " + (particles.size() + 1));
		p.setPositionX(x);
		p.setPositionY(y);

		Random rand = new Random();
		p.setColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		
		particles.add(p);
		return p;
	}
	
	public void loadFile(File f) {
		try (FileReader freader = new FileReader(f)) {
			
			Type token = new TypeToken<List<Particle>>(){}.getType();
			
			JsonReader reader = new JsonReader(freader);
			List<Particle> list = new Gson().fromJson(reader, token);
			
			System.out.println(list);
//			for (Particle p : list) {
//				System.out.println(p.getColor().getClass().getName());
//				particles.add(p);
//			}
//				System.out.println(p.getColor().getRed());
			particles.setAll(list);
			sourceFile = f;
		} catch (Exception e){
			e.printStackTrace();	
		}
		
	}	
	
	public void saveFile(File f) {
		
		try (FileWriter writer = new FileWriter(f)) {
		    Gson gson = new GsonBuilder().create();
		    gson.toJson(particles, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
