package lordxerus.aabbtest.engine;

public class AABB {
    public final Vector2 lower;
    public final Vector2 upper;
    
    public final Vector2 center;
    public final Vector2 extents;
    
    public final float width;
    public final float height;
    public final float perimeter;
    
    
	public AABB(Vector2 a, Vector2 b) {
		this.lower = Vector2.min(a, b);
		this.upper = Vector2.max(a, b);
		
		this.center = new Vector2((lower.x + upper.x) / 2.0f, (lower.y + upper.y) / 2.0f);
		this.extents = new Vector2((upper.x - lower.x) / 2.0f, (upper.y - lower.y) / 2.0f);
		
		this.width = upper.x - lower.x;
		this.height = upper.y - lower.y;
		this.perimeter = 2 * (this.width + this.height);
	}
	
	public AABB(Vector2 center, float width, float height){
		if (width < 0.0f || height < 0.0f) throw new IllegalArgumentException();
		
		this.center = center;
		
		this.width = width;
		this.height = height;
		this.perimeter = 2 * (this.width + this.height);
		
	    float ww = width / 2.0f;
	    float hh = height / 2.0f;
	    
	    this.extents = new Vector2(ww, hh);
	    this.lower = new Vector2(center.x - ww, center.y - hh);
	    this.upper = new Vector2(center.x + ww, center.y + hh);
	    
	}

    public boolean contains(Vector2 p) {
        return 
        (this.lower.x <= p.x && p.x <= this.upper.x) &&
        (this.lower.y <= p.y && p.y <= this.upper.y);
    }
    public boolean contains(AABB other) {
    	// if this lower < is before other { and other } is before this >
    	// <{}> contains is true
    	
        return 
		(this.lower.x <= other.lower.x && other.upper.x <= this.upper.x) &&
		(this.lower.y <= other.lower.y && other.upper.y <= this.upper.y);
    }
    public boolean intersects(AABB other) {
    	// if this lower < can leap left over other } === {-<-}->
    	// if this upper > can leap right over other { === <-{->-}
    	// they should be touching
        return 
		(this.lower.x <= other.upper.x || this.upper.x >= other.lower.x) &&
		(this.lower.y <= other.upper.y || this.upper.y >= other.lower.y);
    }
    
    
    public static AABB merge(AABB a, AABB b) {
        return new AABB(
            Vector2.min(a.lower, b.lower),
            Vector2.max(a.upper, b.upper)
        );
    }
    public static AABB intersection(AABB a, AABB b) {
    	Vector2 lower = Vector2.max(a.lower, b.lower);
    	Vector2 upper = Vector2.min(a.upper, b.upper);
    	
    	// TODO: what if they're not intersecting?
    	
        return new AABB(
            lower,
            upper
        );
    } 
}