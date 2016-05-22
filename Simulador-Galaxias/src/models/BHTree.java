package models;

public class BHTree {

    // threshold value
    private final double Theta = 0.5;
    
	private Particle particle;
	private Quadrant quadrant;
	
	private BHTree NW, NE, SW, SE;
	
	public BHTree(Quadrant q) {
		this.quadrant = q;
	}
	
	public boolean isExternal() {
		return NW == null && NE == null && SW == null && SE == null;
	}
	
	public void insert(Particle p) {
		if (this.particle == null)
			particle = p;
		else if (!isExternal()) {
			particle = particle.plus(p);
			
			insertParticle(p);
		} else {
            NW = new BHTree(quadrant.getNW());
            NE = new BHTree(quadrant.getNE());
            SE = new BHTree(quadrant.getSE());
            SW = new BHTree(quadrant.getSW());

            insertParticle(particle);
            insertParticle(p);

            particle = particle.plus(p);
		}
	}
	
	public void insertParticle(Particle p) {
		if (quadrant.getNW().containsParticle(p))
            NW.insert(p);
        else if (quadrant.getNE().containsParticle(p))
            NE.insert(p);
        else if (quadrant.getSE().containsParticle(p))
            SE.insert(p);
        else if (quadrant.getSW().containsParticle(p))
            SW.insert(p);
	}
	
	public void updateForce(Particle p) {
		if (particle == null || particle.equals(p))
			return;
		
		if (isExternal())
			p.addForce(particle);
		else {
			
			double s = quadrant.getLength();
			double d = p.distanceTo(particle);
			
			if (s/d < Theta)
				p.addForce(particle);
			else {
				NW.updateForce(p);
                NE.updateForce(p);
                SW.updateForce(p);
                SE.updateForce(p);
			}
			
		}
	}
}
