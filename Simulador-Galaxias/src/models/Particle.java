package models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Particle {

    // gravitational constant
    private static final double G = 6.67e-11;
    
	private String name;
	
	private transient Color color;
	private String colorValue;
	
	private double mass;
	private Point2D position, velocity, force;

	public Particle(String name) {
		
		this.name = name;
		
		position = new Point2D(0, 0);
		velocity = new Point2D(0, 0);
		force = new Point2D(0, 0);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Color getColor() {
		if (color == null)
			color = Color.valueOf(colorValue);
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		colorValue = color.toString();
	}
	
	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public Point2D getPosition() {
		return position;
	}

	public double getPositionY() {
		return position.getY();
	}

	public void setPositionY(double Y) {
		position = new Point2D(position.getX(), Y);
	}

	public double getPositionX() {
		return position.getX();
	}

	public void setPositionX(double X) {
		position = new Point2D(X, position.getY());
	}

	public double getVelocityY() {
		return velocity.getY();
	}

	public void setVelocityY(double Y) {
		velocity = new Point2D(velocity.getX(), Y);
	}

	public double getVelocityX() {
		return velocity.getX();
	}

	public void setVelocityX(double X) {
		velocity = new Point2D(X, velocity.getY());
	}
	
	public double distanceTo(Particle p) {
		double dx = p.getPositionX() - getPositionX();
        double dy = p.getPositionY() - getPositionY();
        return Math.sqrt(dx*dx + dy*dy);
	}
	
	public boolean hasPoint(double x, double y) {
		double radius = (1 + getMass() / 100)/2;
		
		double distance = Math.sqrt((getPositionX() - x) * (getPositionX() - x) + (getPositionY() - y) * (getPositionY() - y));
		
		return distance <= radius;
	}
	
	public void addForce(Particle p) {
        double EPS = 3E4;
        
        double dx = p.getPositionX() - getPositionX();
        double dy = p.getPositionY() - getPositionY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        
        double F = (G * mass * p.mass) / (dist*dist + EPS*EPS);
        
        force = force.add(F * dx / dist, F * dy / dist);
	}
	
	
	public String toString() {
		return name;
	}
	
	
	public Particle plus(Particle p) {
		Particle ret = new Particle(this.name);
		ret.setColor(this.color);
		
		ret.setMass(this.mass + p.mass);
		 
		ret.setPositionX((this.getPositionX() * this.mass + p.getPositionX() * p.mass) / ret.mass);
	    ret.setPositionY((this.getPositionY() * this.mass + p.getPositionY() * p.mass) / ret.mass);

	    ret.setVelocityX((this.getVelocityX() * this.mass + p.getVelocityX() * p.mass) / ret.mass); 
	    ret.setVelocityY((this.getVelocityY() * this.mass + p.getVelocityY() * p.mass) / ret.mass); 
	    
	    return ret;
	}
}
