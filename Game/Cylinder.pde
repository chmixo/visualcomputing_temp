class Cylinder extends Item{
  int size;
  
  Cylinder(PVector location, PVector velocity, int radius, int size){
    super(location, velocity);
    this.radius = radius;
    this.size = size;
    myGeom = Geometry.Radial;
    
    float angle;
    PVector[] points = new PVector[30];
    for(int i = 0; i < points.length; i++){
      angle = (TWO_PI / 29) * i;
      points[i] = new PVector(sin(angle) * radius + location.x, cos(angle) * radius + location.y);
    }
    
    PShape openCylinder = createShape();
    openCylinder.beginShape(QUAD_STRIP);
    for(int i = 0; i < points.length; i++) {
      openCylinder.vertex(points[i].x, points[i].y , location.z);
      openCylinder.vertex(points[i].x, points[i].y , location.z + size);
    }
    openCylinder.endShape();
    addShape(openCylinder);
    
    PShape bottom = createShape();
    bottom.beginShape(TRIANGLES);
    for(int i = 0; i < points.length-1; i++) { 
      bottom.vertex(points[i].x, points[i].y , location.z);
      bottom.vertex(points[i + 1].x, points[i + 1].y , location.z);
      bottom.vertex(location.x, location.y, location.z);
    }
    bottom.endShape();
    addShape(bottom);
    
    PShape top = createShape();
    top.beginShape(TRIANGLES);
    for(int i = 0; i < points.length-1; i++) { 
      top.vertex(points[i].x, points[i].y , location.z + size);
      top.vertex(points[i + 1].x, points[i + 1].y , location.z + size);
      top.vertex(location.x, location.y, location.z + size);
    }
    top.endShape();
    addShape(top);
    System.out.println("Cylinder created");
  }
  
  boolean collide(Item intruder){
    switch(intruder.myGeom){
      case Speric:
      case Radial:
        if ((location.dist(intruder.location) - abs(location.z - intruder.location.z)) <= radius + intruder.radius)
          return true;
        break;
      default:
        break;
    }
    return false;
  }
  
}
