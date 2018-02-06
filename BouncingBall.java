
PVector acceleration;
ArrayList<PVector> velocity;
ArrayList<PVector> position;
ArrayList<Float> lifetime;
float radius;
float MAX_LIFE;
float lifeDecay;
float SCENE_SIZE;

float render_x, render_y, render_z;

float startTime;
float elapsedTime;

PFont f;

//Initialization
void setup(){
  size(1024, 960, P3D);
  
  acceleration = new PVector(0, 9.8, 0);
  velocity = new ArrayList<PVector>();
  position = new ArrayList<PVector>();
  lifetime = new ArrayList<Float>();
  radius = 1;
  MAX_LIFE = 25;
  lifeDecay = 255.0 / MAX_LIFE;
  SCENE_SIZE = 100;
  
  render_x = 0;
  render_y = 0;
  render_z = 0;
  
  addBall();  //get our first ball ready
  
  fill(#F0B433); noStroke();
  
  float cameraZ = ((SCENE_SIZE-2.0) / tan(PI*60.0 / 360.0));
  perspective(PI/3.0, 1, 0.1, cameraZ*10.0);
  
  camera(SCENE_SIZE/2, SCENE_SIZE/2, (SCENE_SIZE/2.0) / tan (PI*30.0 / 180.0), SCENE_SIZE/2.0, SCENE_SIZE/2.0, 0, 0, 1, 0);
    
  startTime = millis();
}


//Called every frame
void draw(){
  background(#7C7C7C);
  
  TimeStep();  // calculate how many seconds have passed since last frame
  Update(elapsedTime/1000.0); // calculate how far to move balls
  UserInput();  // check for "wasd" to simulate camera
  Simulate(); // actually render balls
}
    
    
//next step in time
void TimeStep(){
  elapsedTime = millis() - startTime;
  startTime = millis();
}


// calculate how far to move balls
void Update(float dt){
  for(int i = 0; i < position.size(); i++){
    position.get(i).x += (velocity.get(i).x * dt);
    position.get(i).y += velocity.get(i).y * dt;
    position.get(i).z += (velocity.get(i).z * dt);
    
    velocity.get(i).y += acceleration.y * dt;
    
    lifetime.set(i, lifetime.get(i) - dt);
    
    CheckBounds(position.get(i), velocity.get(i));  //simulate wall collision
  }
}


//getting user input for camera
void UserInput(){
  if(keyPressed){
    if(key == 'w'){
        render_z += 2;
    }
    if(key == 's'){
        render_z -= 2;
    }
    if(key == 'a'){
       render_x += 2;
    }
    if(key =='d'){
        render_x -= 2;
    }
  }
}


// render the entire scene
void Simulate(){
  setupScene();  //setup lights and floor
  addBall();  //add information to arraylists for a new ball
  renderBalls();  //transpose stored balls, including our new ball
}



/*  ~  HELPER FUNCTIONS  ~  */



//check if ball has gone outside the bounds
//if it has, send it back in
void CheckBounds(PVector pos, PVector vel){
  //energy lost due to collisions
  float energyLost = .80;
  float r = radius;
  
    if(pos.x < 0){
      pos.set(r, pos.y, pos.z);
      vel.x *= (-1 * energyLost);
    }
    if(pos.x > SCENE_SIZE){
      pos.set(SCENE_SIZE, pos.y, pos.z);
      vel.x *= (-1 * energyLost);
    }
    if(pos.y > SCENE_SIZE){
      pos.set(pos.x, SCENE_SIZE, pos.z);
      vel.y *= (-1 * energyLost);
    }
    if(pos.y < 0){
      pos.set(pos.x, 0, pos.z);
      vel.y *= (-1 * energyLost);
    }
    if(pos.z < -SCENE_SIZE/2){
      pos.set(pos.x, pos.y, -SCENE_SIZE/2);
      vel.z *= (-1 * energyLost);
    }
    if(pos.z > SCENE_SIZE/2){
      pos.set(pos.x, pos.y, SCENE_SIZE/2);
      vel.z *= (-1 * energyLost);
    }
}

// go through all existing balls and kill, or, color and move them
void renderBalls(){
  for(int i = position.size()-1; i >= 0; i--){
    
    //if ball has been there too long, kill it before we move it
    if(lifetime.get(i) < 0){
      position.remove(i);
      velocity.remove(i);
      lifetime.remove(i);
    }
    
    //color over time
    fill(255-(lifetime.get(i) * lifeDecay), lifetime.get(i) * lifeDecay, 172);
    
    //moving to new position
    pushMatrix();
    translate(render_x, 0, render_z);
    //stroke(255);
    //point(position.get(i).x, position.get(i).y, position.get(i).z);
    translate(position.get(i).x, position.get(i).y, position.get(i).z);
    sphere(radius);
    popMatrix();
  }
}


//add a new ball's information to our arrays
void addBall(){
  velocity.add(new PVector(random(-15,15), random(3.75, 7.5), random(-15,15)));
  position.add(new PVector(random(0, SCENE_SIZE), radius, random(-SCENE_SIZE/2, SCENE_SIZE/2)));
  lifetime.add(MAX_LIFE);
}


//renders our lights and walls
void setupScene(){
  //floor
  fill(0);
  pushMatrix();
  stroke(0);
  translate(render_x, 0, render_z);
  //back
  translate(SCENE_SIZE/2, SCENE_SIZE/2 + 1.5*radius, -SCENE_SIZE/2 - 1.5*radius);
  box(SCENE_SIZE + 3*radius, SCENE_SIZE, 1);
  //floor
  translate(0, SCENE_SIZE/2, SCENE_SIZE/2);
  box(SCENE_SIZE + 3*radius, 1, SCENE_SIZE);
  //left wall
  translate(-SCENE_SIZE/2 - 1.5*radius, -SCENE_SIZE/2);
  box(1, SCENE_SIZE, SCENE_SIZE);
  //right waall
  translate(SCENE_SIZE + 3*radius, 0);
  box(1, SCENE_SIZE, SCENE_SIZE);
  fill(#F0B433);
  noStroke();
  popMatrix();
}