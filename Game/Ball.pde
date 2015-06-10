class Ball extends Item{
  float gravity;
  float mu = 0.03;
  
  Ball(PVector location, float gravity, PVector velocity) {
    super(location, velocity);
    this.gravity = gravity;
    radius = 50;
    myGeom = Geometry.Speric;
  }
  
  void update(float angleX, float angleY) {
    velocity.add(new PVector(sin(angleX)*gravity, sin(angleY)*gravity, 0));
    
    float normalForce = sqrt(1 - pow(sin(angleX),2) - pow(sin(angleY),2));
 
    float frictionMagnitude = normalForce * mu;
    PVector friction = velocity.get();
    friction.mult(-1);
    friction.normalize();
    friction.mult(frictionMagnitude);
    velocity.add(friction);
    location.add(velocity);    
  }

  void display() {
    translate(location.x, location.y, 75);
    sphere(50);
  }
  
  void checkEdges() {
    
    if (location.x > 475) {
        location.x = 475;
        velocity.x = abs(velocity.x) * -1;
    } else if (location.x < -475) {
        location.x = -475;
        velocity.x = abs(velocity.x);
    }
    
    if (location.y > 475) {
        location.y = 475;
        velocity.y = abs(velocity.y) * -1  ;
    } else if (location.y < -475) {
        location.y = -475;
        velocity.y = abs(velocity.y);
    }
 }
  
  void bounce(Item obstacle){
      location.sub(velocity);
      PVector norm = PVector.sub(location, obstacle.getLocation());
      float angle = PVector.angleBetween(norm, velocity);
      float speed = velocity.mag();
      float bounceAngle = 2 * angle - PI;
      norm.normalize();
      norm.mult(2 * sin(bounceAngle / 2) * speed);
      velocity.add(norm);
      velocity.normalize();
      velocity.mult(speed);
      return;
  }
}
