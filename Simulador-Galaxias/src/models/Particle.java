package models;

import javafx.scene.paint.Color;

public class Particle {

    // gravitational constant
    private static final double G = 6.67e-11;
    
	private String name;
	
	private transient Color color;
	private String colorValue;
	
	private double mass;
	private double posX, posY, velX, velY, forceX, forceY;

	public Particle(String name) {
		
		this.name = name;
	
		posX = 0;
		posY = 0;
		velX = 0;
		velY = 0;
		
		resetForce();
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
	
	public double getRadius() {
		return Math.pow((mass / 1500)/(4.0*Math.PI/3.0), 1.0/3.0);
	}

	public double getPositionY() {
		return posY;
	}

	public void setPositionY(double Y) {
		posY = Y;
	}

	public double getPositionX() {
		return posX;
	}

	public void setPositionX(double X) {
		posX = X;
	}

	public double getVelocityY() {
		return velY;
	}

	public void setVelocityY(double Y) {
		velY = Y;
	}

	public double getVelocityX() {
		return velX;
	}

	public void setVelocityX(double X) {
		velX = X;
	}
	
	public double distanceTo(Particle p) {
		double dx = p.getPositionX() - getPositionX();
        double dy = p.getPositionY() - getPositionY();
        return Math.sqrt(dx*dx + dy*dy);
	}
	
	public boolean hasPoint(double x, double y) {
		double distance = Math.sqrt((getPositionX() - x) * (getPositionX() - x) + (getPositionY() - y) * (getPositionY() - y));
		
		return distance <= getRadius();
	}

	public void update(double dt) {
		velY += forceY * dt / mass;
		velX += forceX * dt / mass;
		
		posY += velY * dt;
		posX += velX * dt;
	}
	
	public void resetForce() {
		forceX = 0;
		forceY = 0;
	}
	
	public void addForce(Particle p) {
        double EPS = 3E4;
        
        double dx = p.getPositionX() - getPositionX();
        double dy = p.getPositionY() - getPositionY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        
        double F = (G * mass * p.mass) / (dist*dist + EPS*EPS);
     
        forceX += F * dx / dist;
        forceY += F * dy / dist;
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
