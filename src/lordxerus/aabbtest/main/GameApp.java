package lordxerus.aabbtest.main;

import processing.core.PApplet;

public class GameApp extends PApplet{

	Simulation simulation;

	public void settings(){
		size(1000, 1000);
	}

	public void setup() {

		simulation = new Simulation(this.getGraphics());

		//simulation.setNaiveMethod(false);

		for(int i = 0; i < 100; i++) {
			float r = random(5, 20);
			float x = random(300 + r, width - r - 300);
			float y = random(300 + r, height - r - 300);
			float hue = random(0, 360);
			float density = random(0.01f, 1);

			simulation.createParticle(x, y, r, 0f, 0f, density, hue, 1);
		}

		simulation.createParticle(10, 10, 10, 200, 200, 10, 0, 1);
	}

	float lastMillis = 0;
	final float spring_constant = 1000;
	public void draw(){
		background(255);

		Particle p = simulation.getParticle(100);
		p.applyForce((mouseX - p.getX()) * spring_constant, (mouseY - p.getY()) * spring_constant);

 		simulation.update((millis() - lastMillis) / 1000f);

		push();
		strokeWeight(4);
		stroke(0xFFFF0000);
		line(p.getX(), p.getY(), mouseX, mouseY);
		pop();
		lastMillis = millis();
	}

	public void mousePressed(){

	}
	
	// Virtual key system
	
	public static void main(String[] args){
		String[] processingArgs = {"GameApp"};
		GameApp mySketch = new GameApp();
		PApplet.runSketch(processingArgs, mySketch);
	}
}