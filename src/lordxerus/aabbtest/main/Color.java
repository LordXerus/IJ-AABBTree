package lordxerus.aabbtest.main;

public final class Color {
	public static final Color black      = new Color(0  , 0  , 0  , 255);
	public static final Color blue       = new Color(0  , 0  , 255, 255);
	public static final Color cyan       = new Color(0  , 255, 255, 255);
	public static final Color green      = new Color(0  , 255, 0  , 255);
	public static final Color magenta    = new Color(255, 0  , 255, 255);
	public static final Color red        = new Color(255, 0  , 0  , 255);
	public static final Color yellow     = new Color(255, 235, 4  , 255);
	public static final Color pureyellow = new Color(255, 255, 0  , 255);
	public static final Color white      = new Color(255, 255, 255, 255);
	public static final Color gray       = new Color(127, 127, 127, 255);
	public static final Color grey       = gray;
	public static final Color c          = new Color(1  , 0  , 0  , 0  );
	
	private static float clamp(float val, float min, float max) {
		return (val > max) ? max : Math.max(val, min);
	}
	
	private static float lerp(float from, float to, float t) {
		return Math.fma(to - from, t, from);
	}
	public static Color lerp(Color from, Color to, float t) {
		float tClamped = clamp(t, 0.0f, 1.0f);
		return lerpUnclamped(from, to, tClamped);
	}
	public static Color lerpUnclamped(Color from, Color to, float t) {
		return new Color(
			lerp(from.r, to.r, t),
			lerp(from.g, to.g, t),
			lerp(from.b, to.b, t),
			lerp(from.a, to.a, t)
		);
	}
	
	// h [0, 360]
	// s [0, 1]
	// v [0, 1]
	// a [0. 1]
	// https://stackoverflow.com/a/6930407
	public static Color fromHSV(float h, float s, float v, float a) {
		
		float ss = clamp(s, 0.0f, 1.0f);
		float vv = clamp(v, 0.0f, 1.0f) * 255.0f; // scales rgb by 255
		float aa = clamp(a, 0.0f, 1.0f) * 255.0f;
		
		if (s <= 0.0) return new Color(v, v, v);
		
		float hh = h / 60.0f;
		long ipart = (long) hh;
		float fpart = hh - ipart;
		
		while(ipart >= 6) ipart -= 6;
		while(ipart < 0) ipart += 6;
		
		float p = vv * (1.0f - ss);
		float q = vv * (1.0f - (ss * fpart));
		float t = vv * (1.0f - (ss * (1.0f - fpart)));


		return switch ((int) ipart) {
			case 0 -> new Color(vv, t, p, aa);
			case 1 -> new Color(q, vv, p, aa);
			case 2 -> new Color(p, vv, t, aa);
			case 3 -> new Color(p, q, vv, aa);
			case 4 -> new Color(t, p, vv, aa);
			default -> new Color(vv, p, q, aa); //case 5
		};
	}

	public static Color fromHSV(float h, float s, float v) {
		return fromHSV(h, s, v, 1.0f);
	}

	public static Color fromBits(int c){
		return new Color(
				(c >> 16) & 0xFF,
				(c >> 8)  & 0xFF,
				c        & 0xFF,
				(c >> 24) & 0xFF
		);
	}
	public final float r;
	public final float g;
	public final float b;
	public final float a;
	
	public final float grayscale;
	public final float maxComponent;
	
	public final int bits;



	public Color(float r, float g, float b) {
		this(r, g, b, 255);
	}
	public Color(float r, float g, float b, float a){
		this.r = clamp(r, 0, 255);
		this.g = clamp(g, 0, 255);
		this.b = clamp(b, 0, 255);
		this.a = clamp(a, 0, 255);
		
		grayscale = (r + g + b) / 3;
		maxComponent = Math.max(Math.max(r, g), b);
		
		bits = (int)a << 24 | (int)r << 16 | (int)g << 8 | (int)b;
		
		// TODO: toHsv? Hsl?
	}
}
