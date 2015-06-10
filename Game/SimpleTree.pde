class SimpleTree extends Item{
int size;
  
  SimpleTree(PVector location, PVector velocity, int radius, int size){
    super(location, velocity);
    this.radius = radius;
    this.size = size;
    myGeom = Geometry.Radial;
    
    PShape tree = loadShape("simpleTree.obj");
    tree.scale(size);
    addShape(tree);
    
    System.out.println("SimpleTree created");
  }
  
  void display() {
    translate(location.x, location.y, 25);
    rotateX(PI * 0.5);
    shape(super.shapes.get(0));
    rotateX(-PI * 0.5);
    translate(-location.x, -location.y, -25);
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
