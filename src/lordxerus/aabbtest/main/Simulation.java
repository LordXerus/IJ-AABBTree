package lordxerus.aabbtest.main;

import lordxerus.aabbtest.engine.Vector2;
import lordxerus.aabbtest.engine.aabb_tree.AABBItem;
import lordxerus.aabbtest.engine.aabb_tree.AABBTree;
import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import lordxerus.aabbtest.engine.AABB;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

@NotNullByDefault
public final class Simulation {
    private final List<Particle> particles = new ArrayList<>();

    private final List<AABBItem<Particle>> aabbs = new ArrayList<>();
    private boolean naiveMethod = false;

    private final PGraphics surface;

    private final AABBTree<Particle> particleTree = new AABBTree<>();

    public Simulation(PGraphics surface) {
        this.surface = surface;
    }

    public boolean isNaiveMethod() {
        return naiveMethod;
    }
    public void setNaiveMethod(boolean naiveMethod) {
        this.naiveMethod = naiveMethod;
    }

    public AABB toAABB(Particle p) {
        float x = p.getX(), y = p.getY(), r = p.getRadius();
        return new AABB(
                new Vector2(x - r, y - r),
                new Vector2(x + r, y + r)
        );
    }

    public void createParticle(float x, float y, float r, float vx, float vy, float density, float hue, float friction) {
        Particle p = new Particle(x, y, r, vx, vy, density, hue, friction);
        particles.add(p);
        AABB p_aabb = new AABB(
                new Vector2(x - r, y - r),
                new Vector2(x + r, y + r)
        );
        aabbs.add(particleTree.create(p_aabb, p));
    }

    private void advance_naive(float dt) {
        for (Particle p: particles) {
            for (Particle p2: particles) {
                if (p == p2) continue;

                p.handleCollision(p2);
            }

            p.handleWallCollision(surface.width, surface.height);

            p.advance(dt);
        }

    }

    public Particle getParticle(int index) {
        return particles.get(index);
    }

    private void advance(float dt) {
        for(AABBItem<Particle> item : aabbs) {
            assert item.getData() != null;
            Particle item_p = item.getData();

            for(AABBItem<Particle> other : particleTree.query(item.getAABB())) {
                assert other.getData() != null;
                Particle other_p = other.getData();

                if(item_p == other_p) continue;
                item_p.handleCollision(other_p);
            }

            item_p.handleWallCollision(surface.width, surface.height);

            item_p.advance(dt);
        }

    }
    private void draw_naive() {
        for(Particle p : particles) {
            p.draw(surface);
        }
    }
    private void draw() {
        for(AABBItem<Particle> aabb : aabbs) {
            assert aabb.getData() != null;
            aabb.getData().draw(surface);
        }
    }


    public void update(float dt) {
        if(naiveMethod) {
            advance_naive(dt);
            draw_naive();
        } else {

            advance(dt);
            draw();
        }
    }




}
