import java.util.List;

class Item{
  List<PShape> shapes = new ArrayList<PShape>();
  PVector location;
  PVector velocity;
  Geometry myGeom;
  int radius; //to be used with Radial or Speric myGeom
  
  Item(PVector location, PVector velocity){
    this.location = location;
    this.velocity = velocity;
  }
  
  void display(){
  //translate(location.x, location.y, location.z);
    for(int i = 0; i < shapes.size(); i++){
      shape(shapes.get(i));
    }
  }
  
  void addShape(PShape newShape){
    shapes.add(newShape);
  }
  
  PVector getLocation(){
    return location.get();
  }
  
  boolean collide(Item intruder){
    return false;
  }
}
