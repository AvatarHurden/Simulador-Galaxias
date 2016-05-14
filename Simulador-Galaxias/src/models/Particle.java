package models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Particle {

	private String name;
	private Color color;
	
	private double mass;
	private Point2D position, velocity;

	public Particle(String name) {
		
		this.name = name;
		
		position = new Point2D(0, 0);
		velocity = new Point2D(0, 0);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
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
	
	public String toString() {
		return name;
	}
	
}
