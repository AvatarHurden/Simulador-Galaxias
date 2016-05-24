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
	
	private double time;
	
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
		setPositionXInUnit(p, x);
		setPositionYInUnit(p, y);
		setMassInUnit(p, 1);

		Random rand = new Random();
		p.setColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		
		particles.add(p);
		return p;
	}
	
	public void setMassInUnit(Particle p, double mass) {
		p.setMass(mass * scale.getMassConversion());	
	}
	 
	public double getMassInUnit(Particle p) {
		return p.getMass() / scale.getMassConversion();
	}
	
	public void setPositionXInUnit(Particle p, double posX) {
		p.setPositionX(posX * scale.getDistanceConversion());
	}
	
	public double getPositionXInUnit(Particle p) {
		return p.getPositionX() / scale.getDistanceConversion();
	}
	
	public void setPositionYInUnit(Particle p, double posY) {
		p.setPositionY(posY * scale.getDistanceConversion());
	}
	
	public double getPositionYInUnit(Particle p) {
		return p.getPositionY() / scale.getDistanceConversion();
	}
	
	public void setVelocityXInUnit(Particle p, double velX) {
		p.setVelocityX(velX * scale.getDistanceConversion());
	}

	public double getVelocityXInUnit(Particle p) {
		return p.getVelocityX() / scale.getDistanceConversion();
	}
	
	public void setVelocityYInUnit(Particle p, double velY) {
		p.setVelocityY(velY * scale.getDistanceConversion());
	}
	
	public double getVelocityYInUnit(Particle p) {
		return p.getVelocityY() / scale.getDistanceConversion();
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
