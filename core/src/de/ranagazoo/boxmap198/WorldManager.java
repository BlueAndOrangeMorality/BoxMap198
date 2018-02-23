package de.ranagazoo.boxmap198;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class WorldManager implements Disposable
{
  private World world;
  private Box2DDebugRenderer debugRenderer;
  private BoxEntityFactory boxEntityFactory;
  private WaypointGroup waypointGroup;
  
  public WorldManager()
  {
    world = new World(new Vector2(0, 0), true);
    world.setContactListener(new BoxMapContactListener());
    debugRenderer = new Box2DDebugRenderer();
    boxEntityFactory = new BoxEntityFactory();
    waypointGroup = new WaypointGroup();
  }
  
  public  Array<BoxEntity> loadEntities(TiledMap tiledMap)
  {
    Array<BoxEntity> boxEntities = new Array<BoxEntity>();
//    
//    Hier werden die ganzen entities mit world.clearForces();.WorldManager   erzeugt
//    am Besten werden hier die Entities auch schon eingeteilt und dann an das Spiel zurückgeliefert, damit beim rendern da drüberiteriert wird.
//    für render und move
//    
//    quasi den teil hier aus boxmap richtig durchlaufen lassen
//  
//    tiledMap.getLayers().get(1).getObjects().getByType(PolygonMapObject.class);
//    tiledMap.getLayers().get(1).getObjects().getByType(PolylineMapObject.class);
//    tiledMap.getLayers().get(1).getObjects().getByType(RectangleMapObject.class);
//    tiledMap.getLayers().get(1).getObjects().getByType(TiledMapTileMapObject.class);
//    
    
    
    for (MapObject mapObject : tiledMap.getLayers().get(1).getObjects())
    {
      String type = boxEntityFactory.getTypeFromMapObject(mapObject);
      Vector2 pos = boxEntityFactory.getPositionFromMapObject(mapObject);
      
      
      //TODO: Falls WaypontGroup, das hier erstellen!
      if(type.equals(Config.TYPE_WAYPOINTGROUP))
      {
        waypointGroup = new WaypointGroup(mapObject, world, boxEntityFactory);
      }
      //Alles was keine WaypointGroup ist
      else 
      {
        
        
        //Sinnvoll ist ggf., immer nur ein Shape pro Objekt anzunehmen
        //und per Parameter in der Klasse einfach nur den sensorenradius anzugeben.
        //Das ist zumindestens aktuell sinnvoll, solange entities nur ein fixture haben
        //---->Das if/else kann entfallen
        
        BodyDef bodyDef = boxEntityFactory.getBodyDefFromMapObject(type);
        bodyDef.position.set(pos);
        Body body = world.createBody(bodyDef);
        
        //Sonderfall: Separater Shape gefunden
        //Gegebenenfalls mehrere FixtureDefs als Array holen?
        //Aber jedes braucht ein Shape
        
        FixtureDef fixtureDef = boxEntityFactory.getFixtureDefFromMapObject(type);
        //TODO Aktuell bekommen nur Obstacles ein Shape ausgelesen
        if(type.equals(Config.TYPE_OBSTACLE))
          fixtureDef.shape = boxEntityFactory.getShapeFromMapObject(mapObject);
        body.createFixture(fixtureDef);
        
        //Sonderfall Enemy: Sensor hinzugefügt
        if(type.equals(Config.TYPE_ENEMY1))
        {
          FixtureDef fixtureDefSensor = boxEntityFactory.getFixtureDefSensor();
          body.createFixture(fixtureDefSensor);
        }
        
        BoxEntity boxEntity2 = new BoxEntity(type, body);
        
        //Aktuell werden nur Player und Enemies zurückgeliefert, da nur diese move() enthalten
        if(type.equals(Config.TYPE_PLAYER1) || type.equals(Config.TYPE_ENEMY1))
        {
          boxEntities.add(boxEntity2);
        }
        
      }
      
    }
    

    //Muss nachträglich passieren, weil WaypointGroup nach dem ersten Entity geladen worden sein könnte.
    for (BoxEntity boxEntity : boxEntities)
    {
      if(boxEntity.getType().equals(Config.TYPE_ENEMY1))
      {
        //Aktuell nur eine WaypointGroup vorgesehen
        boxEntity.setWaypointGroup(waypointGroup);
      }
    }

    
    return boxEntities;    
  }
  
  
  
  
  public void step()
  {
    world.step(1 / 60f, 6, 2);
  }
  
  public void render(Matrix4 combined)
  {
    debugRenderer.render(world, combined);
  }
  
 

  
  @Override
  public void dispose()
  {
    world.dispose();
    boxEntityFactory.dispose();
  }

  public WaypointGroup getWaypointGroup()
  {
    return waypointGroup;
  }
}
