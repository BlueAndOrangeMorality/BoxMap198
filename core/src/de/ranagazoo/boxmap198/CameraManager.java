package de.ranagazoo.boxmap198;

import static de.ranagazoo.boxmap198.Config.TS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraManager {

	private OrthographicCamera cameraSprites;
	private OrthographicCamera cameraBox2dDebug;
	
	public CameraManager () {
	
		cameraSprites = new OrthographicCamera();
		cameraSprites.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraSprites.update();

		cameraBox2dDebug = new OrthographicCamera();
		cameraBox2dDebug.setToOrtho(false, Gdx.graphics.getWidth() / TS, Gdx.graphics.getHeight() / TS);
		cameraBox2dDebug.update();
	}

	
   public void update(Vector2 playerPos, Vector2 mapSize)
	  {
	    
	    //dynamische Camera
	    setCameraPositions(dynamicTranslate(playerPos, mapSize));
	    
	    //statische Camera
//	    setCameraPositions(playerPos);
	    
	    cameraSprites.update();
	    cameraBox2dDebug.update();
	  }

 //Aufteilung in eigene Methode eigentlich nur wegen dynamische vs statische Camera
   private void setCameraPositions(Vector2 position)
   {
     Vector3 cameraPosition = new Vector3(position.x, position.y, 0);
     cameraSprites.position.set(cameraPosition.cpy().scl(TS));
     cameraBox2dDebug.position.set(cameraPosition);
   }
   
	
	
	 /*
	   * Stellt die CameraPosition anhand von Spielerposition, Kartengröße und Bildschirmgröße ein
	   * Die Kamera folgt immer dem Spieler, bleibt aber am Rand hängen
	   */
	  public Vector2 dynamicTranslate(Vector2 playerPos, Vector2 mapSize)
	  {
	    Vector2 totalScreenSizeBox2d = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).scl(1/TS);

	    //Initial CameraPosition is the playerPosition minus half the screen size 
	    Vector2 cameraPosition  = playerPos.cpy().sub(totalScreenSizeBox2d.cpy().scl(0.5f));
	    
	    // if the camera is at the right or left edge lock it to prevent a black bar
	    if(cameraPosition.x < 0)
	      cameraPosition.x = 0;
	    if(cameraPosition.x + totalScreenSizeBox2d.x > mapSize.x)
	      cameraPosition.x = mapSize.x - totalScreenSizeBox2d.x;

	    // if the camera is at the top or bottom edge lock it to prevent a black bar
	    if(cameraPosition.y < 0)
	      cameraPosition.y = 0;
	    if(cameraPosition.y + totalScreenSizeBox2d.y > mapSize.y)
	      cameraPosition.y = mapSize.y - totalScreenSizeBox2d.y;
	    
	    //re-add center of the map
	    cameraPosition.add(totalScreenSizeBox2d.cpy().scl(0.5f));
	    
	    //workaround for small maps
	    if(mapSize.x < totalScreenSizeBox2d.x && mapSize.y < totalScreenSizeBox2d.y)
	      cameraPosition.set(mapSize.cpy().scl(0.5f));
	      
	    return cameraPosition;
	  }
	
	
	public OrthographicCamera getCameraSprites () {
		return cameraSprites;
	}

	public OrthographicCamera getCameraBox2dDebug () {
		return cameraBox2dDebug;
	}
}
