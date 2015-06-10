float cylinderBaseSize = 50;
float cylinderHeight = 50;
int cylinderResolution = 40;
PShape openCylinder = new PShape();
PShape bottomBase = new PShape();
PShape topBase = new PShape();

void setup() {
  size(400, 400, P3D);
  float angle;
  float[] x = new float[cylinderResolution + 1];
  float[] y = new float[cylinderResolution + 1];
  
  //get the x and y position on a circle for all the sides
  for(int i = 0; i < x.length; i++) {
    angle = (TWO_PI / cylinderResolution) * i;
    x[i] = sin(angle) * cylinderBaseSize;
    y[i] = cos(angle) * cylinderBaseSize;
  }
  
  openCylinder = createShape();
  openCylinder.beginShape(QUAD_STRIP);
  
  //draw the border of the cylinder
  for(int i = 0; i < x.length; i++) {
    openCylinder.vertex(x[i], y[i] , 0);
    openCylinder.vertex(x[i], y[i], cylinderHeight);
  }
  openCylinder.endShape();
  
  bottomBase = createShape();
  bottomBase.beginShape(TRIANGLES);
  
  for(int i = 0; i < x.length-1; i++) {
    
    bottomBase.vertex(x[i], y[i] , 0);
    bottomBase.vertex(x[i+1], y[i+1], 0);
    bottomBase.vertex(0, 0, 0);
  }
  
  bottomBase.endShape();
  
  topBase = createShape();
  topBase.beginShape(TRIANGLES);
  
  for(int i = 0; i < x.length-1; i++) {
    angle = (TWO_PI / cylinderResolution) * i;
    topBase.vertex(x[i], y[i] , cylinderHeight);
    topBase.vertex(x[i+1], y[i+1], cylinderHeight);
    topBase.vertex(0, 0, cylinderHeight);
    
  }
  
  topBase.endShape();
  
  //openCylinder.addChild(topBase);
  //openCylinder.addChild(bottomBase, 1);
}

void draw() {
  background(255);
  translate(mouseX, mouseY, 0);
  shape(openCylinder);
  shape(bottomBase);
  shape(topBase);
}
