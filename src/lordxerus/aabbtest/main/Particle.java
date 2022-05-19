package lordxerus.aabbtest.main;

import processing.core.PGraphics;


import static processing.core.PApplet.*;

public final class Particle {

    private float x;
    private float y;
    private final float radius;
    private float density;
    private float vx, vy;

    private float friction;

    private Color clear, middle;
    private Color color;
    private float hue;


    Particle(float x, float y, float radius, float vx, float vy, float density, float hue, float friction) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.vx = vx;
        this.vy = vy;
        setHue(hue);
        setDensity(density);

        this.friction = friction;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVx() { return vx; }

    public float getVy() { return vy; }

    public float getV() { return vx*vx + vy*vy; }

    public void flipVx() { vx = -vx; }

    public void flipVy() { vy = -vy; }

    public float getFriction(){
        return friction;
    }
    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void applyForce(float x, float y) {
        vx += x / getMass();
        vy += y / getMass();
    }


    public float getRadius () {
        return radius;
    }

    public float getHue() {
        return hue;
    }
    public void setHue(float hue) {
        this.hue = hue;
        this.middle = Color.fromHSV(hue, 1f, 1f);
        this.clear = Color.fromHSV(hue, 1f, 1f, 0f);
        updateColor();
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
        updateColor();
    }

    public float getMass() {
        return density * (float)Math.PI * radius * radius;
    }
    private void updateColor() {
        if(density > 0.5) {
            float value = (float)Math.log10(2f - density * 2f) * .5f + 1f;
            this.color = Color.lerp(Color.black, this.middle, value);
        } else {
            //this.color = Color.fromHSV(hue, 1f, 1f, (float)Math.log10(density * 2f) * .5f + 1f);
            this.color = Color.lerp(this.clear, this.middle, density * 2f);
        }
    }

    public void advance(float dt) {
        x += vx * dt;
        y += vy * dt;

        if(vx > friction) vx -= friction;
        else if (vx < -friction) vx += friction;
        else vx = 0;

        if(vy > friction) vy -= friction;
        else if (vy < -friction) vy += friction;
        else vy = 0;
    }

    public void handleWallCollision(float width, float height) {
        //int collided = 0;

        if (this.x - this.radius < 0) {
            this.flipVx();
            this.x = radius;
            //collided |= 1;
        }
        else if (this.getX() + this.getRadius() > width) {
            this.flipVx();
            this.x = width - radius;
            //collided |= 1;
        }

        if (this.getY() - this.getRadius() < 0) {
            this.flipVy();
            this.y = radius;
            //collided |= 1;
        } else if (this.getY() + this.getRadius() > height) {
            this.flipVy();
            this.y = height - radius;
            //collided |= 1;
        }

        //return collided != 0;
    }

    public void handleCollision(Particle other) {
        float rsum = this.radius + other.radius;
        float d = dist(this.x, this.y, other.x, other.y);
        if(d > rsum) return; //no collision

        // will be used for dis-collision later.
        float col_x = this.x + (other.x - this.x) * this.radius/rsum;
        float col_y = this.y + (other.y - this.y) * this.radius/rsum;

        float u1x = this.vx;
        float u1y = this.vy;
        float u2x = other.vx;
        float u2y = other.vy;

        float m1 = this.getMass();
        float m2 = other.getMass();

        float msum = m1 + m2;

        // consult wikipedia elastic collisions
        this.vx = u1x * (m1 - m2) / msum + u2x * 2 * m2 / msum;
        this.vy = u1y * (m1 - m2) / msum + u2y * 2 * m2 / msum;

        other.vx = u1x * 2 * m1 / msum + u2x * (m2 - m1) / msum;
        other.vy = u1y * 2 * m1 / msum + u2y * (m2 - m1) / msum;



        // dis-collision

        // distance to collision
        float dc1 = dist(col_x, col_y, this.x, this.y);

        // other's distance to collision
        float dc2 = dist(col_x, col_y, other.x, other.y);

        // adjustment factor???
        float adj1 = (this.radius / dc1) - 1;

        float adj2 = (other.radius / dc2) - 1;

        this.x = this.x + (this.x - col_x) * adj1;
        this.y = this.y + (this.y - col_y) * adj1;

        other.x = other.x + (other.x - col_x) * adj2;
        other.y = other.y + (other.y - col_y) * adj2;


    }
    public void draw(PGraphics surface) {
        //surface.push();
        surface.fill(color.bits);
        surface.circle(x, y, radius * 2);
        //surface.pop();
    }


}
