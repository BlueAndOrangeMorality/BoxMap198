package de.ranagazoo.boxmap198;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class BoxEntity
{
//  Die Elemente sollten nie die Hauptklasse kriegen
//  Bei Enemy wird das schwieriger, hier muss beim Scannen des entities die Position im ContactListener übergeben werden
//  und beim zurück-zum-Wegpunkt muss auf die Wegpunktliste verwiesen werden, die am Besten schon im Entity enthalten ist
//  
  
  private String type;
  private Body body;

  private int targetIndex;
  private WaypointGroup waypointGroup;
  
  private int status;
  private Body targetBody;
  
  public BoxEntity(String type, Body body)
  {
    this.type = type;
    this.body = body;
    this.body.setUserData(this);
    this.status = Config.STATUS_IDLE;
  }
  
  
  public void move()
  {
    if(type.equals(Config.TYPE_PLAYER1))
      movePlayer();
    else if(type.equals(Config.TYPE_ENEMY1))
      moveEnemy();
    else
      Gdx.app.log("BoxEntity2.move", "Wrong type");
  }
  
  public void movePlayer()
  {
    if (Gdx.input.isKeyPressed(Keys.A))
      body.applyLinearImpulse(new Vector2(-1.2f, 0), body.getPosition(), true);
    if (Gdx.input.isKeyPressed(Keys.D))
      body.applyLinearImpulse(new Vector2(1.2f, 0), body.getPosition(), true);
    if (Gdx.input.isKeyPressed(Keys.W))
      body.applyLinearImpulse(new Vector2(0, 1.2f), body.getPosition(), true);
    if (Gdx.input.isKeyPressed(Keys.S))
      body.applyLinearImpulse(new Vector2(0, -1.2f), body.getPosition(), true);
  }
  
  public void moveEnemy()
  {
    if (status == Config.STATUS_ATTACK)
    {
      //falls targetBody der Player ist
      //moveToPosition(box2dMovement.getPlayerPosition());
      moveToPosition(targetBody.getPosition());
    }
    else if (status == Config.STATUS_NEW_TARGET)
    {
//      currentTargetIndex = box2dMovement.getNextWaypointIndex(currentTargetIndex);
//      // currentTargetIndex =
//      // box2dMovement.getRandomWaypointIndex(currentTargetIndex);
      status = Config.STATUS_IDLE;
    }
    else
    {
      //falls targetBody der nächste Waypoint ist
      //moveToPosition(box2dMovement.getWaypoint(currentTargetIndex).getPosition());
      moveToPosition(targetBody.getPosition());
    }
  }

  /*
   * Hier geschieht die Magie: Nach komplexen Formeln dreht das Enemy sich in
   * die Richtung der angegebenen Position und bewegt sich auf diese zu.
   */
  public void moveToPosition(Vector2 position)
  {

    // Berechnung findet folgendermaßen statt:
    // triangleBody.getAngle() gibt die aktuelle Rotation des Dreiecks in
    // Polarkoordinaten (Radial) an
    // Um die notwendige Rotation festzustellen, wird ein (normalisierter)
    // Differenzvektor zwischen dem Spieler und dem Dreieck errechnet
    Vector2 normalizedDifferenceVector = body.getPosition().sub(position).nor();
    //
    // Mittels atan2-Funktion, die den ArcusTangenz dieses Vektors ermittelt,
    // hat man die gewünschte Rotation (Radial)
    float normalizedDifferenceRotationRad = MathUtils.atan2(-normalizedDifferenceVector.x, normalizedDifferenceVector.y);
    //
    // Das dreht den Wert um 90° oder 180°
    // Leider weiß ich noch nicht, wass 0° eigentlich sind
    // float normalizedDifferenceRotationRad = MathUtils.atan2(
    // normalizedDifferenceVector.y, normalizedDifferenceVector.x );
    // float normalizedDifferenceRotationRad = MathUtils.atan2(
    // -normalizedDifferenceVector.y, -normalizedDifferenceVector.x );

    // Eine direkte Rotation mit Torque ist möglich, aber nicht sinnvoll, dies
    // übergeht die Physik
    // float nextAngle = triangleBody.getAngle() +
    // triangleBody.getAngularVelocity() / 60.0f;
    // float totalRotation = normalizedDifferenceRotationRad - nextAngle;//use
    // angle in next time step
    // if(totalRotation < -180 * MathUtils.degreesToRadians) totalRotation +=
    // 360*MathUtils.degreesToRadians;
    // if(totalRotation > 180 * MathUtils.degreesToRadians) totalRotation -=
    // 360*MathUtils.degreesToRadians;
    // triangleBody.applyTorque((totalRotation < 0 ? -10 : 10), true);

    // Alternativ Schönere Variante, berücksichtigt Geschwindigkeit und Masse
    //
    // nextAngle erhält nicht nur die aktuelle Rotation sondern auch die
    // aktuelle Rotationsgeschwindigkeit
    float nextAngle = body.getAngle() + body.getAngularVelocity() / 60.0f;
    //
    // dies wird von dr gewünschten Rotation abgezogen, so dreht sich das
    // Dreieck bei jedem render hin und her, bis die Rotation korrekt ist
    float totalRotation = normalizedDifferenceRotationRad - nextAngle;
    //
    // Hier wird dafür gesorgt, dass sich das Dreick nicht bis unendlich dreht,
    // sondern im 360°-Bereich bleibt
    while (totalRotation < -180 * MathUtils.degreesToRadians)
      totalRotation += 360 * MathUtils.degreesToRadians;
    while (totalRotation > 180 * MathUtils.degreesToRadians)
      totalRotation -= 360 * MathUtils.degreesToRadians;
    //
    // Drehgeschwindigkeit, 60 ist quasi direkt auf dem Spieler, kleinere Werte
    // (0.1f) sorgen für langsamere Drehung, wie ein Kompass
    // Achtung, vergößert wegen kleinerem Körper, also weniger masse
    float desiredAngularVelocity = totalRotation * 120;
    //
    // Angabe, um wieviel Grad pro timestep rotiert werden darf (aktuell -20° -
    // +20°)
    // Sorgt auch für unterschiedliche Drehgeschwindigkeit, in Anhängigkeit der
    // Masse des Dreiecks
    // Achtung, vergößert wegen kleinerem Körper, also weniger masse
    float change = 90 * MathUtils.degreesToRadians;
    desiredAngularVelocity = Math.min(change, Math.max(-change, desiredAngularVelocity));
    float impulse = body.getInertia() * desiredAngularVelocity;
    //
    // if(Gdx.input.isKeyPressed(Keys.R))

    // Ehemals if, jetzt else
    // Drehung
    body.applyAngularImpulse(impulse, true);
    // Aktuell manuell angetriggerte Verfolgung
    Vector2 achtMalVier = position.cpy().sub(body.getPosition()).nor().cpy().scl(0.01f);
    body.applyLinearImpulse(achtMalVier, body.getPosition(), true);
    // Ehemals if, jetzt else

  }
  
  
  

  public String getType()
  {
    return type;
  }
  
  public Body getBody()
  {
    return body;
  }


  public void setWaypointGroup(WaypointGroup waypointGroup)
  {
    this.waypointGroup = waypointGroup;
    this.targetIndex = 0;
    this.targetBody = waypointGroup.get(targetIndex);
  }
  
  public void setStatus(int status)
  {
    this.status = status;
  }
  
  public void setTargetBody(Body body)
  {
    this.targetBody = body;
  }
  
  //Gets the last saved Waypoint (Body) from the Enemys WaypointGroup
  public void resetTargetBodyToWaypoint()
  {
    this.targetBody = this.waypointGroup.get(targetIndex);
  }

//  public void setNextWaypoint()
//  {
//    targetIndex = this.waypointGroup.getNextWaypoint(targetIndex);
//    targetBody = this.waypointGroup.get(targetIndex);
//  }
  
  public void setNextWaypoint(int oldTargetIndex)
  {
    targetIndex = this.waypointGroup.getNextWaypoint(oldTargetIndex);
    targetBody = this.waypointGroup.get(targetIndex);
  }
}
