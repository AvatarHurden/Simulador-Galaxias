package models;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class Quadrant {

	private Point2D center;
	private double length;
	
	public Quadrant(Point2D center, double length) {
		this.center = center;
		this.length = length;
	}
	
	public double getLength() {
		return length;
	}
	
	public boolean containsPoint(Point2D p) {
		Rectangle2D area = new Rectangle2D(center.getX() - length / 2, center.getY() - length / 2, length, length);
		return area.contains(p);
	}
	
	public boolean containsParticle(Particle p) {
		return containsPoint(p.getPosition());
	}
	
	public Quadrant getNW() {
		return new Quadrant(new Point2D(center.getX() - length / 4, center.getY() + length / 4), length / 2);
	}
		
	public Quadrant getNE() {
		return new Quadrant(new Point2D(center.getX() + length / 4, center.getY() + length / 4), length / 2);		
	}
	
	public Quadrant getSW() {
		return new Quadrant(new Point2D(center.getX() - length / 4, center.getY() - length / 4), length / 2);
	}
		
	public Quadrant getSE() {
		return new Quadrant(new Point2D(center.getX() + length / 4, center.getY() - length / 4), length / 2);
	}
	
}
