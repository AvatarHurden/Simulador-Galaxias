package models;

import java.awt.Point;

import javafx.geometry.Point2D;

public class Particle {

	private double mass;
	private Point2D position, velocity;

	public Particle() {
		
		position = new Point2D(0, 0);
		velocity = new Point2D(0, 0);
		
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
	
}
