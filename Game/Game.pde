import java.util.List;

float angle;
float depth = 2000;
float e = 0;
float vitesse = 1;
float virtualX;
float virtualY;
float oldX;
float oldY;
Ball ball;
byte gameMode = 0; //0: playing mode. 1: edition mode, activated by 'SHIFT' key.
List<Item> obstacles = new ArrayList<Item>();
PGraphics backgroundSurface; //Object represented by the black rectangle


void setup() {
  size(500, 500, P3D);
  //CREATION BLACK RECTANGLE
  backgroundSurface = createGraphics(5*width, 700, P2D);
  noStroke();
  ball = new Ball(new PVector(0,0,0), -0.5, new PVector(0.0,0.0,0.0));
}

void draw() {

  if (gameMode == 0){  

    camera(width/2, height/2, depth, 250, 250, 0, 0, 1, 0);
    directionalLight(50, 100, 125, 0, -1, 0);
    ambientLight(102, 102, 102);
  
    background(200);
    
    //DRAWING BLACK RECTANGLE
    drawBackgroundSurface();
    image(backgroundSurface, -2*width, 1.5*height);
    // END DRAWING BLACK RECTANGLE
    
    translate(width/2, height/2, 0);
    float ry = map(virtualY, 0, height, 0, -PI/3);
    float rx = map(virtualX, 0, width, 0, -PI/3);
  
    rotateY(angle);
    rotateX(PI/2 + ry * 2);
    rotateY(-rx * 2);
    box(1000, 1000, 50);

    if (obstacles.size() > 0){
      for(int i = 0; i < obstacles.size(); i++){
        obstacles.get(i).display();
        if (obstacles.get(i).collide(ball)){
          ball.bounce(obstacles.get(i));
          System.out.println("collision");
        }
      }
    }
    ball.update(rx, ry);
    ball.checkEdges();
    ball.display();
  
  } else {
  
     translate(width, 0, 0);
     
     rect(-width, -height, 2 * width, 2 * height);
     beginContour();
     ellipse(ball.location.x, ball.location.y, 2 * ball.radius, 2 * ball.radius);
     endContour();  
     
     if (obstacles.size() > 0) {
        for(int i = 0; i < obstacles.size(); i++) {
          obstacles.get(i).display();
        }
     }   
  }
}

//BLACK RECTANGLE
void drawBackgroundSurface() {
  backgroundSurface.beginDraw();
  backgroundSurface.background(255, 150, 0);
  backgroundSurface.rect(3*width, 3*height, 3*width, 3*height);
  backgroundSurface.endDraw();
}


void mouseDragged() {
  if (gameMode == 1){
    return;
  }
  virtualX += (mouseX - 250 - oldX)*vitesse * cos(angle) + (mouseY - 250 - oldY)*vitesse * sin(-angle);
  virtualY += (mouseY - 250 - oldY)*vitesse * cos(angle) + (mouseX - 250 - oldX)*vitesse * sin(angle);
  
  if (abs(virtualX) > 250) {
    virtualX *= 250 / abs(virtualX);
  }
  
  if (abs(virtualY) > 250) {
    virtualY *= 250 / abs(virtualY);
  }
  
  oldX = mouseX - 250;
  oldY = mouseY - 250;
}

void mouseClicked(){
  if (gameMode == 0) {
    return;
  }
  obstacles.add(new SimpleTree(new PVector((mouseX -250) * 2, (mouseY - 250) * 2, 0), new PVector(0, 0, 0), 50, 150));
}

void mouseMoved() {
  oldX = mouseX - 250;
  oldY = mouseY - 250;
}

void mouseWheel(MouseEvent event) {
  e = event.getCount();
  if ( e > 0 && vitesse < 1.45 ) {
    vitesse += 0.1;
  }
  if ( e < 0 && vitesse > 0.25 ) {
    vitesse -= 0.1;
  }
  println(e);
}


void keyPressed() {
  if (key == CODED) {
    if (keyCode == SHIFT){
      gameMode = 1;
      stroke(255, 0, 0);
      ortho(0, 2*width, 0, 2*height);
    } else {
      if (keyCode == LEFT) {
        if (gameMode == 0 ) {
          angle -= PI/16;
        }
      } else if (keyCode == RIGHT) {
        if (gameMode == 0 ) {
          angle += PI/16;
        }
      } else if (keyCode == UP) {
        depth -= 50;
      } else if (keyCode == DOWN) {
        depth += 50;
      }
    }
  }
}

void keyReleased(){
  if (key == CODED){
      if (keyCode == SHIFT){
        gameMode = 0;
        noStroke();
        perspective();
      }
  }
}
