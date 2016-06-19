package models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
	private List<Particle> originalParticles;
	
	private double time;
	
	public Simulation() {
		originalParticles = new ArrayList<Particle>();
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
	
	public double getTime() {
		return time;
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
	
	public double getRadiusInUnit(Particle p) {
		return p.getRadius() / scale.getDistanceConversion();
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
	
	public Particle getParticleOnPoint(double x, double y) {
		for (Particle p : particles)
			if (p.hasPoint(x * scale.getDistanceConversion(), y * scale.getDistanceConversion()))
				return p;
		return null;
	}
	
	private void populateOriginal() {
		originalParticles.clear();
		for (Particle p : particles)
			originalParticles.add(p.getCopy());
	}
	
	private double calculateArea() {
		double radius = 0;
		double distance = 0;
		for (Particle p : particles) {
			distance = Math.max(Math.abs(getPositionXInUnit(p)), Math.abs(getPositionYInUnit(p)));
			if (distance > radius) 
				radius = distance;
		}
		
		return radius;
	}
	
	public void step() {
		if (time == 0)
			populateOriginal();
		time += scale.getTimeStep();
		
		Quadrant quad = new Quadrant(Point2D.ZERO, calculateArea());
		BHTree tree = new BHTree(quad);
		
		for (Particle p : particles) {
			if (quad.containsParticle(p))
				tree.insert(p);
		}
		
		for (Particle p : particles) {
			p.resetForce();
			tree.updateForce(p);
			p.update(scale.getTimeStep());
		}
		
	}
	
	public void reset() {
		time = 0;
		particles.clear();
		for (Particle p : originalParticles)
			particles.add(p.plus(new Particle("")));
	}
	
	public void loadFile(File f) {
		try (FileReader freader = new FileReader(f)) {
			JsonParser parser = new JsonParser();
			
			JsonElement element = parser.parse(freader);
			
			JsonObject obj = element.getAsJsonObject();
			scale = Scale.valueOf(obj.get("scale").getAsString());
			
			List<Particle> list = new Gson().fromJson(obj.getAsJsonArray("particles"), 
					new TypeToken<List<Particle>>(){}.getType());
			
			particles.setAll(list);
			time = 0;
			sourceFile = f;
		} catch (Exception e){
			e.printStackTrace();	
		}
		
	}	
	
	public void saveFile(File f) {
		
		try (FileWriter writer = new FileWriter(f)) {
		    Gson gson = new GsonBuilder().create();
		    
		    if (originalParticles.isEmpty())
		    	originalParticles.addAll(particles);
		    
		    JsonObject json = new JsonObject();
		    json.add("scale", gson.toJsonTree(scale));
		    json.add("particles", gson.toJsonTree(originalParticles));
		    
		    gson.toJson(json, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
