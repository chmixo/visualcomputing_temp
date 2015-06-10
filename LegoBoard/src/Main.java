/**

 * Created by Johan on 05.05.2015.

 */



import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//import HoughComparator.java;

public class Main extends PApplet {


	PImage image;
	PImage image_fond;
	PImage buffer;                      ////////////////////////////////////////////////
	Capture cam;                        //============================================//
	Boolean camActive = false;    //<== //== Use this to activate your webcam (*o*) ==//
	HScrollbar Scroll;                  //============================================//
	HScrollbar Scroll2;                 ////////////////////////////////////////////////
	HScrollbar Scroll3;
	Timer fpstimer;
	TimerTask fpstask;
	int fps, white, black;
	QuadGraph quads = new QuadGraph();

	public static void main(String args[]) {
		PApplet.main("Main");               //needed because we run the code as an application, not an applet
	}

	public void setup() {
		white = color(255);
		black = color(0);
		size(800, 660);                                     //window size
		image_fond = loadImage("images/board1.jpg");
		image = image_fond.get();
		buffer = createImage(image.width, image.height, RGB);//buffer we will use to send what we want to the screen
		Scroll = new HScrollbar(this, 0, 0, 800, 20);
		Scroll2 = new HScrollbar(this,0, 620, 800, 20);
		Scroll3 = new HScrollbar(this,0, 640, 800, 20);

		if (camActive) {
			cam = new Capture(this);                //works only if there is a webcam, can be surrounded by a if or try/catch
			cam.start();                            //activate the camera
		}

		fpstimer = new Timer(true);                 //timer used to count FPS and print it on the title of the window
		fpstask = new TimerTask() {

			@Override
			public void run() {
				frame.setTitle("fps : " + fps + "    scroll 1 : " + round(Scroll.getPos() * 100)+ "    scroll 2 : " + round(Scroll2.getPos() * 100)+ "    scroll 3 : " + round(Scroll3.getPos() * 100));

				fps = 0;

			}

		};

		fpstimer.scheduleAtFixedRate(fpstask, 0, 1000);

	}



	public void draw() {
        println("=============\n== refresh ==\n=============");

		image((buffer), 0, 20);                   //print the buffer on the screen, at coordinates (0, 20)
		if(camActive) {                         //if we use the camera, set it as the source of our image
			image(flip(image), 0, 20);
			cam.read();
			image.copy(cam.get(), 0, 0, cam.width, cam.height, 0, 0, image.width, image.height);//copy and expand cam on the source image
		}else{
			//image((image), 0, 20);
		}
		Scroll.display();
		Scroll.update();
		Scroll2.display();
		Scroll2.update();
		Scroll3.display();
		Scroll3.update();
		fps++;                                  //increment for frame rate computation
		//buffer = sobel(image);                  //render function/transformation
		buffer.copy(fixSobelNoCam(image), 0, 0, image.width, image.height, 0, 0, buffer.width, buffer.height);
		ArrayList<PVector> lines =  hough(buffer, 6);
		getIntersections(lines);
		displayQuads(buffer, lines);

	}

	public PImage fixSobelNoCam(PImage img){            //as described in assignment
		int dim = img.width * img.height;
		PImage result = createImage(img.width, img.height, ALPHA);

		float[] buffer = new float[dim];
		float sum_h, sum_v, sum;
		float max = 0;
		int i, j;
		for(i = 0; i < dim; i++){
			result.pixels[i] = 0;
		}
		for(i = 1; i < img.width - 1; i++){
			for(j = 1; j < img.height - 1; j++){
				if(hue(img.get(i, j)) < 255 * Scroll2.getPos() && hue(img.get(i, j)) > 255 * Scroll3.getPos()) {
					sum_h = img.get(i, j - 1) - img.get(i, j + 1);
					sum_v = img.get(i - 1, j) - img.get(i + 1, j);
					sum = sqrt(sum_h * sum_h + sum_v * sum_v);
					max = max(max, sum);
				}else{
					sum = 0;   //hue threshold
				}
				buffer[j * img.width + i] = sum;
			}
		}

		for(i = 1; i < img.width - 1; i++){
			for(j = 1; j < img.height; j++){
				if(buffer[j * img.width + i] > max * Scroll.getPos()){
					result.set(i, j, color(255));
				}else{
					result.set(i, j, color(0));
				}
			}
		}
		return result;
	}

	public PImage flip(PImage img){
		PImage result = createImage(img.width, img.height, RGB);
		for(int i = 0; i < img.width; i++){
			int revers = img.width - i;
			for(int j = 0; j < img.height; j++){
				result.set(revers, j, img.get(i, j));
			}
		}
		return result;
	}

	public PImage sobel(PImage img){            //as described in assignment
		PImage result = createImage(img.width, img.height, ALPHA);
		int dim = img.width * img.height;
		float[] buffer = new float[dim];
		float sum_h, sum_v, sum;
		float max = 0;
		int i, j, jmult;
		for(i = 0; i < dim; i++){
			result.pixels[i] = 0;
		}
		float rightThreshold = Scroll2.getPos() * 255;
		float leftThreshold = Scroll3.getPos() * 255;
		for(j = 1; j < img.height - 1; j++){
			jmult = j * img.width;
			for(i = 1; i < img.width - 1; i++){
				if(hue(img.get(i, j)) < rightThreshold && hue(img.get(i, j)) > leftThreshold) {
					sum_h = img.get(i, j - 1) - img.get(i, j + 1);
					sum_v = img.get(i - 1, j) - img.get(i + 1, j);
					sum = sqrt(sum_h * sum_h + sum_v * sum_v);
					max = max(max, sum);
				}else{
					sum = 0;   //hue threshold
				}
				if(camActive){
					buffer[jmult + img.width - i] = sum;
				}else {
					buffer[jmult + i] = sum;
				}
			}
		}
		float convolute = max * Scroll.getPos();
		for(j = 1; j < img.height; j++){
			jmult = j * img.width;
			for(i = 1; i < img.width - 1; i++){
				if(buffer[jmult + i] > convolute){
					result.set(i, j, white);
				}else{
					result.set(i, j, black);
				}
			}
		}
		return result;
	}



	public PImage gaussianBlur(PImage img) {    //see assignment pdf for more info

		float[][] kernel = {{9, 12, 9},
				{12, 15, 12},
				{9, 12, 9}};
		float weight = Scroll.getPos() * 200;
		PImage result = createImage(img.width, img.height, ALPHA);
		colorMode(ALPHA);
		float sum;

		for(int i = 1; i < img.width - 1; i++){

			for(int j = 1; j < img.height - 1; j++){
				sum = saturation(img.get(i - 1, j - 1)) * kernel[0][0];
				sum += saturation(img.get(i, j - 1)) * kernel[0][1];
				sum += saturation(img.get(i + 1, j - 1)) * kernel[0][2];
				sum += saturation(img.get(i - 1, j)) * kernel[1][0];
				sum += saturation(img.get(i, j)) * kernel[1][1];
				sum += saturation(img.get(i + 1, j)) * kernel[1][2];
				sum += saturation(img.get(i - 1, j + 1)) * kernel[2][0];
				sum += saturation(img.get(i, j + 1)) * kernel[2][1];
				sum += saturation(img.get(i + 1, j + 1)) * kernel[2][2];

				if(camActive) {
					result.pixels[j * img.width + img.width - i] = color(sum / weight); //mirror effect because of webcam...
				}else{
					result.pixels[j * img.width + i] = color(sum / weight);
				}
			}
		}

		result.updatePixels();
		return result;

	}



	public PImage convolute(PImage img) {
		float[][] kernel = {{0, 0, 0},
				{1, 0, -1},
				{0, 0, 0}};

		float weight = Scroll.getPos() * 2;

		// create a greyscale image (type: ALPHA) for output

		PImage result = createImage(img.width, img.height, ALPHA);
		colorMode(ALPHA);
		// kernel size N = 3
		//
		// for each (x,y) pixel in the image:
		//     - multiply intensities for pixels in the range
		//       (x - N/2, y - N/2) to (x + N/2, y + N/2) by the
		//       corresponding weights in the kernel matrix
		//     - sum all these intensities and divide it by the weight
		//     - set result.pixels[y * img.width + x] to this value

		float sum;

		for(int i = 1; i < img.width - 1; i++){
			for(int j = 1; j < img.height - 1; j++){
				sum = saturation(img.get(i - 1, j - 1)) * kernel[0][0];
				sum += saturation(img.get(i, j - 1)) * kernel[0][1];
				sum += saturation(img.get(i + 1, j - 1)) * kernel[0][2];
				sum += saturation(img.get(i - 1, j)) * kernel[1][0];
				sum += saturation(img.get(i, j)) * kernel[1][1];
				sum += saturation(img.get(i + 1, j)) * kernel[1][2];
				sum += saturation(img.get(i - 1, j + 1)) * kernel[2][0];
				sum += saturation(img.get(i, j + 1)) * kernel[2][1];
				sum += saturation(img.get(i + 1, j + 1)) * kernel[2][2];
				if(camActive) {
					result.pixels[j * img.width + img.width - i] = color(sum / weight); //mirror effect because of webcam...
				}else{
					result.pixels[j * img.width + i] = color(sum / weight);
				}
			}
		}

		result.updatePixels();
		return result;

	}

	public PImage filter(PImage image){

		PImage buffer = createImage(image.width, image.height, RGB);
		image.loadPixels();

		for(int i = 0; i < image.width * image.height; i++){

			int c = image.pixels[i];
			int a = c >>> 24;
			int r = (c << 8) >>> 24;                //warrior-style color extraction, and absolutely correct and optimised
			int g = (c << 16) >>> 24;
			int b = (c << 24) >>> 24;

			colorMode(HSB, 255);
			if(brightness(c) < Scroll.getPos() * 255){
				buffer.pixels[i] = color(hue(c), saturation(c), brightness(c));
			}else {
				buffer.pixels[i] = color(hue(c), saturation(c), Scroll.getPos() * 255);
			}

			colorMode(RGB, 255);
			if(brightness(c) > Scroll2.getPos() * 255){
				buffer.pixels[i] = color(r, g, b, a);
			}
		}

		buffer.updatePixels();
		return buffer;
	}



	public PImage hueThreshold(PImage img){
		PImage result = createImage(img.width, img.height, HSB);
		img.loadPixels();
		for(int i = 0; i < img.width * img.height; i++){
			float h = hue(img.pixels[i]);
			if (h < Scroll2.getPos() * 255 && h > Scroll3.getPos() * 255){
				result.pixels[i] = img.pixels[i];
			}else{
				result.pixels[i] = color(0);
			}
		}

		result.updatePixels();
		return result;

	}



	ArrayList<PVector> hough(PImage edgeImg, int nLines) {
		edgeImg.loadPixels();
		float discretizationStepsPhi = 0.06f;
		float discretizationStepsR = 2.5f;
		ArrayList<Integer> bestCandidates = new ArrayList<Integer>();

		ArrayList<PVector> lines = new ArrayList<PVector>();

		// dimensions of the accumulator
		int phiDim = (int) (Math.PI / discretizationStepsPhi);
		int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);
		// our accumulator (with a 1 pix margin around)
		int[] accumulator = new int[(phiDim + 2)*(rDim + 2)];
		
		/*utiliser pour determiner accR
		int maxRadius = (int)Math.ceil(Math.hypot(edgeImg.width, edgeImg.height));
		int halfrDim = rDim >>> 1 ;*/
		
		//put sin and cos values in a buffer
	    float[] sinCache = new float[phiDim]; 
	    float[] cosCache = new float[phiDim];
	    
	    for (int accPhi = 0; accPhi < phiDim ; accPhi++) { 
            float phi = (accPhi * discretizationStepsPhi); 
            sinCache[accPhi] = sin(phi); 
            cosCache[accPhi] = cos(phi); 
        } 
		
		// Fill the accumulator: on edge points (ie, white pixels of the edge
		// image), store all possible (r, phi) pairs describing lines going
		// through the point.
		for (int y = 0; y < edgeImg.height; y++) {
			for (int x = 0; x < edgeImg.width; x++) {
				// Are we on an edge?
				if (brightness(edgeImg.pixels[ y * edgeImg.width + x]) != 0) {
					for(int accPhi= 0; accPhi < (phiDim); accPhi++){
						float r = x*cosCache[accPhi] +  y *sinCache[accPhi];
						
						//double r = (((x-centerX)*cosCache[accPhi])+ ((y-centerY)* sinCache[accPhi]));
						
						/*if (accR < 0){
							accR += rDim/2;
						}*/
						
						int accR = Math.round(((r/discretizationStepsR) + (rDim-1)*(0.5f)));
						
						//int accR = (int) (Math.round(r * halfrDim / maxRadius) + (halfrDim));
						//A changer
						accumulator[(accPhi+1) * (rDim+2) + accR + 1 +(rDim)] += 1;
					}


				}
			}
		}

		//Affichage
		/*PImage houghImg = createImage((rDim + 2), (phiDim + 2), ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
        houghImg.pixels[i ] = color(min(255, accumulator[i]));
        }
        houghImg.updatePixels();*/

		// size of the region we search for a local maximum
		int neighbourhood = 10;
		int halfNeighbourhood = neighbourhood / 2;
		// only search around lines with more that this amount of votes
		// (to be adapted to your image)
		int minVotes = 100;

		for (int accR = 0; accR < rDim; accR++) {

			for (int accPhi = 0; accPhi < phiDim; accPhi++) {
				// compute current index in the accumulator
				int idx = (accPhi + 1) * (rDim + 2) + accR + 1;

				if (accumulator[idx] > minVotes) {
					boolean bestCandidate=true;
					// iterate over the neighbourhood
					for(int dPhi=-halfNeighbourhood; dPhi < halfNeighbourhood+1; dPhi++) {
						// check we are not outside the image
						if( accPhi+dPhi < 0 || accPhi+dPhi >= phiDim) continue;

						for(int dR=-halfNeighbourhood; dR < halfNeighbourhood +1; dR++) {
							// check we are not outside the image
							if(accR+dR < 0 || accR+dR >= rDim) continue;
							int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2) + accR + dR + 1;
							if(accumulator[idx] < accumulator[neighbourIdx]) {
								// the current idx is not a local maximum!
								bestCandidate=false;
								break;
							}
						}
						if(!bestCandidate) break;
					}
					if(bestCandidate) {
						// the current idx *is* a local maximum
						bestCandidates.add(idx);
					}
				}
			}
		}

		Collections.sort(bestCandidates, new HoughComparator(accumulator));
		//println("best : " + bestCandidates.size());
		int max = min(nLines, bestCandidates.size());
		for (int i = 0; i < max ; ++i) {
			// compute current index in the accumulator
			// first, compute back the (r, phi) polar coordinates:
			int accPhi = (int) (bestCandidates.get(i) / (rDim + 2)) - 1;
			int accR = bestCandidates.get(i) - (accPhi + 1) * (rDim + 2) - 1;
			float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
			float phi = accPhi * discretizationStepsPhi;
			PVector coordinate = new PVector(r, phi);
			lines.add(coordinate);

			// Cartesian equation of a line: y = ax + b
			// in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
			// => y = 0 : x = r / cos(phi)
			// => x = 0 : y = r / sin(phi)
			// compute the intersection of this line with the 4 borders of
			// the image
			float sinPhi = sin(phi);
			float invSinPhi = 1 / sinPhi;
			float cosPhi = cos(phi);
			float invCosPhi = 1 / cosPhi;
			float rOnSinPhi = r * invSinPhi;
			int x0 = 0;
			int y0 = (int) (r * invSinPhi);
			int x1 = (int) (r * invCosPhi);
			int y1 = 0;
			int x2 = edgeImg.width;
			int y2 = (int) (-cosPhi * invSinPhi * x2 + rOnSinPhi);
			int y3 = edgeImg.width;
			int x3 = (int) (-(y3 - rOnSinPhi) * (sinPhi * invCosPhi));

			// Finally, plot the lines
			stroke(204,102,0);
			if (y0 > 0) {
				if (x1 > 0) {
					line(x0, y0, x1, y1);
				} else if (y2 > 0) {
					line(x0, y0, x2, y2);
				} else {
					line(x0, y0, x3, y3);
				}
			} else {
				if (x1 > 0) {
					if (y2 > 0) {
						line(x1, y1, x2, y2);
					} else {
						line(x1, y1, x3, y3);
					}
				} else {
					line(x2, y2, x3, y3);
				}
			}
		}
		return lines;
	}

	public ArrayList<PVector> getIntersections(List<PVector> lines) {
		ArrayList<PVector> intersections = new ArrayList<PVector>();
		for (int i = 0; i < lines.size() - 1; i++) {
			PVector line1 = lines.get(i);
			for (int j = i + 1; j < lines.size(); j++) {
				PVector line2 = lines.get(j);
				// compute the intersection and add it to 'intersections'
				double r1 = Math.sqrt(pow(line1.x*cos(line1.y), 2 ) + pow(line1.x*sin(line1.y), 2 ));
				double r2 = Math.sqrt(pow(line2.x*cos(line2.y), 2 ) + pow(line2.x*sin(line2.y), 2 ));
				double d = (line2.x*cos(line2.y) / r2) * (line1.x*sin(line1.y)/r1) - 
						(line1.x*cos(line1.y)/r1)*(line2.x*sin(line2.y)/r2);
				float x = (float) ((r2*(line1.x*sin(line1.y)/r1)-r1*(line2.x*sin(line2.y)/r2)) / d) ;
				float y = (float) (((-r2)*(line1.x*cos(line1.y)/r1)+r1*(line2.x*cos(line2.y) / r2)) / d) ;

				PVector une_intersection = new PVector(x, y);
				intersections.add(une_intersection);

				// draw the intersection
				fill(255, 128, 0);
				ellipse(x, y, 10, 10);
			}
		}
		return intersections;
	}

	public PVector intersection(PVector line1, PVector line2) {

		double r1 = Math.sqrt(pow(line1.x*cos(line1.y), 2 ) + pow(line1.x*sin(line1.y), 2 ));
		double r2 = Math.sqrt(pow(line2.x*cos(line2.y), 2 ) + pow(line2.x*sin(line2.y), 2 ));
		double d = (line2.x*cos(line2.y) / r2) * (line1.x*sin(line1.y)/r1) - 
				(line1.x*cos(line1.y)/r1)*(line2.x*sin(line2.y)/r2);
		float x = (float) ((r2*(line1.x*sin(line1.y)/r1)-r1*(line2.x*sin(line2.y)/r2)) / d) ;
		float y = (float) (((-r2)*(line1.x*cos(line1.y)/r1)+r1*(line2.x*cos(line2.y) / r2)) / d) ;

		PVector une_intersection = new PVector(x, y);

		return une_intersection;

	}

	public void displayQuads(PImage edgeIm, ArrayList<PVector> lines) {

		quads.build(lines, edgeIm.width, edgeIm.height);
		quads.cycles = quads.findCycles();

        for (int[] quad : quads.cycles) {
            PVector l1 = lines.get(quad[0]);
            PVector l2 = lines.get(quad[1]);
            PVector l3 = lines.get(quad[2]);
            PVector l4 = lines.get(quad[3]);
            if (quads.isConvex(l1, l2, l3, l4) && quads.nonFlatQuad(l1, l2, l3, l4) && quads.validArea(l1, l2, l3, l4, 3, 4)) {
                println("====== good quad found ======");
                // (intersection() is a simplified version of the
                // intersections() method you wrote last week, that simply
                // return the coordinates of the intersection between 2 lines)
                PVector c12 = intersection(l1, l2);
                PVector c23 = intersection(l2, l3);
                PVector c34 = intersection(l3, l4);
                PVector c41 = intersection(l4, l1);
                // Choose a random, semi-transparent colour
                Random random = new Random();
                fill(color(min(255, random.nextInt(300)),
                        min(255, random.nextInt(300)),
                        min(255, random.nextInt(300)), 50));
                quad(c12.x, c12.y, c23.x, c23.y, c34.x, c34.y, c41.x, c41.y);
            }
        }
	}
}

