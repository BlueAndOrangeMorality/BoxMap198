package de.ranagazoo.boxmap198;

import com.badlogic.gdx.physics.box2d.Body;


public class Obstacle
{
  private Body groundBody;
 
  public Obstacle(Body body)
  { 
    groundBody = body;
    groundBody.setUserData(this);
  }  
  
  //Obstacles are not rendered, they are drawn "behind" map parts
  //Obstacles do not move!

 
}
