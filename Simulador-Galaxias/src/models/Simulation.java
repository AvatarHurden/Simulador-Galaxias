package models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Simulation {
	
	private File sourceFile;
	
	private Scale scale;
	private ObservableList<Particle> particles;
	
	public Simulation() {
		particles = FXCollections.observableArrayList();
		
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
	
	public void step() {
		
		Quadrant quad = new Quadrant(Point2D.ZERO, 400);
		BHTree tree = new BHTree(quad);
		
		
		for (Particle p : particles) {
			if (quad.containsParticle(p))
				tree.insert(p);
		}
		
		for (Particle p : particles) {
			p.resetForce();
			tree.updateForce(p);
			p.update(1);
		}
		
	}
	
	public void loadFile(File f) {
		try (FileReader freader = new FileReader(f)) {
			JsonReader reader = new JsonReader(freader);
			List<Particle> list = new Gson().fromJson(reader, new TypeToken<List<Particle>>(){}.getType());
			
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
