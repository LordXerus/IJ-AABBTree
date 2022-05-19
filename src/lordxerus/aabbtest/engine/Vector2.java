package lordxerus.aabbtest.engine;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import processing.core.PConstants;

@NotNullByDefault
public final class Vector2 {
	public static final float EPSILON = 1e-4f;
	
	public static final Vector2 left  = new Vector2(-1,  0); 
	public static final Vector2 right = new Vector2( 1,  0); 
	public static final Vector2 down  = new Vector2( 0, -1); 
	public static final Vector2 up    = new Vector2( 0,  1); 
	
	public static final Vector2 one   = new Vector2( 1,  1); 
	public static final Vector2 zero  = new Vector2( 0,  0);

	public static boolean isFinite(Vector2 v) {
		return Float.isFinite(v.x) && Float.isFinite(v.y);
	}
	
	public static float angle(Vector2 from, Vector2 to) {
		float raw = (to.heading - from.heading);
		if (raw > PConstants.PI) return raw - PConstants.TWO_PI;
		if (raw < PConstants.PI) return raw + PConstants.TWO_PI;
		return raw;
	}
	public static Vector2 clamp(Vector2 vector, float maxLength) {
		return (vector.magnitude <= maxLength) ? vector : 
		       Vector2.mul(vector.getNormalized(), maxLength);
	}
	
	public static Vector2 fromAngle(float angle, float magnitude) {
		return new Vector2(magnitude * (float)Math.cos(angle), magnitude * (float)Math.sin(angle));
	}
	
	public static float distance(Vector2 a, Vector2 b) {
		// return engine.Vector2.sub(a, b).magnitude;
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		return (float)Math.sqrt(dx*dx + dy*dy);
	}
	public static float dot(Vector2 a, Vector2 b) {
		return a.x * b.x + a.y * b.y;
	}
	
	private static float lerp(float from, float to, float t) {
		return Math.fma(to - from, t, from);
	}
	public static Vector2 lerp(Vector2 from,Vector2 to, float t) {
		float tClamped = t > 1.0f ? 1.0f : Math.max(t, 0.0f);
		return lerpUnclamped(from, to, tClamped);
	}
	public static Vector2 lerpUnclamped(Vector2 from, Vector2 to, float t) {
		return new Vector2(lerp(from.x, to.x, t), lerp(from.y, to.y, t));
	}
	public static Vector2 moveTowards(Vector2 current, Vector2 target, float maxDelta) {
		final Vector2 delta = sub(target, current);
		if (delta.magnitude < maxDelta) return target;
		return add(current, mul(delta.getNormalized(), maxDelta));
	}
	
	public static Vector2 max(Vector2 a, Vector2 b) {
		return new Vector2(Math.max(a.x, b.x), Math.max(a.y, b.y));
	}
	public static Vector2 min(Vector2 a, Vector2 b) {
		return new Vector2(Math.min(a.x, b.x), Math.min(a.y, b.y));
	}
	
	public static Vector2 rotate(Vector2 vector, float angle) {
		return fromAngle(vector.heading + angle, vector.magnitude);
	}
	
	public static Vector2 reflect(Vector2 vector, float normalHeading) {
		float delta = normalHeading - vector.heading;
		return rotate(vector, 2 * delta);
	}
	public static Vector2 reflect(Vector2 vector, Vector2 normal) {
		// reflect off dashed line drawn by normal
		// all tails sit at origin
		if(Math.abs(normal.x) < EPSILON) return new Vector2(-vector.x, vector.y);
		if(Math.abs(normal.y) < EPSILON) return new Vector2(vector.x, -vector.y);
		
		if(Math.abs(normal.y/normal.x - 1) < EPSILON) // noinspection SuspiciousNameCombination
			return new Vector2(vector.y, vector.x);
		if(Math.abs(normal.y/normal.x + 1) < EPSILON) return new Vector2(-vector.y, -vector.x);

		float delta = normal.heading - vector.heading;
		return rotate(vector, 2 * delta);
	}
	
	public static Vector2 mirror(Vector2 incident, float normalHeading) {
		float delta = normalHeading - incident.heading;
		return rotate(incident, Math.fma(delta, 2, PConstants.PI));
	}
	public static Vector2 mirror(Vector2 incident, Vector2 normal) {
		// same as reflect, but head of incident is tail of result
		// same as reflect, but the x and y of incident are negated
		if(Math.abs(normal.x) < EPSILON) return new Vector2(incident.x, -incident.y);
		if(Math.abs(normal.y) < EPSILON) return new Vector2(-incident.x, incident.y);
		
		if(Math.abs(normal.y/normal.x - 1) < EPSILON) return new Vector2(-incident.y, -incident.x);

		if(Math.abs(normal.y/normal.x + 1) < EPSILON) // noinspection SuspiciousNameCombination
			return new Vector2(incident.y, incident.x);

		float delta = normal.heading - incident.heading;
		return rotate(incident, Math.fma(delta, 2, PConstants.PI));
	}
	
	// Pls Help
	public static float getHeadingNormal(Vector2 from, Vector2 to) {
		float raw = (to.heading + from.heading) / 2;
		if(Math.abs(to.heading - from.heading) < PConstants.PI) return raw;
		if(raw < 0) return raw + PConstants.PI;
		return raw - PConstants.PI;
	}
//	public static float getMirrorNormal(engine.Vector2 from, engine.Vector2 to) {
//		throw new RuntimeException();
//		float raw = (to.heading + from.heading + PConstants.PI) / 2;
//		
//		if(Math.abs(to.heading - from.heading) < PConstants.PI) return raw;
//		if(raw < 0) return raw + PConstants.PI;
//		return raw - PConstants.PI;
//	}
	
	// TODO: Insert Critically Damped Spring
	
	
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x + b.x, a.y + b.y);
	}
	public static Vector2 sub(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	public static Vector2 mul(Vector2 a, final float d) {
		return new Vector2(a.x * d, a.y * d);
	}
	public static Vector2 div(Vector2 a, final float d) {
		return new Vector2(a.x / d, a.y / d);
	}
	public static Vector2 fma(Vector2 a, final float multiply, Vector2 add) {
		return new Vector2(
			Math.fma(a.x, multiply, add.x),
			Math.fma(a.y, multiply, add.y)
		);
	}
	public static Vector2 fsa(Vector2 a, Vector2 scale, Vector2 add) {
		return new Vector2(
				Math.fma(a.x, scale.x, add.x),
				Math.fma(a.y, scale.y, add.y)
		);
	}
	
	public static Vector2 increase(Vector2 v, final float d) {
		return new Vector2(v.x + d, v.y + d);
	}
	public static Vector2 decrease(Vector2 v, final float d) {
		return new Vector2(v.x - d, v.y - d);
	}
	public static Vector2 scale(Vector2 v, Vector2 s) {
		return new Vector2(v.x * s.x, v.y * s.y);
	}
	public static Vector2 invscale(Vector2 v, Vector2 s) {
		return new Vector2(v.x / s.x, v.y / s.y);
	}
	
	public final float x;
	public final float y;
	public final float magnitudeSq;
	public final float magnitude;
	public final float heading;
		
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
		
		this.magnitudeSq = x*x + y*y;
		this.magnitude = (float)Math.sqrt(magnitudeSq);

		this.heading = (float)Math.atan2(y, x);
	}

	public Vector2 getNormalized() {
		return Vector2.div(this, this.magnitude);
	}
}
