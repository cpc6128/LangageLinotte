package org.linotte.greffons.impl.xyz.outils;

import org.linotte.greffons.impl.xyz.abstraction.Object3D;

import javax.media.j3d.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * A simple collision detector class. This responds to a collision event by
 * printing a message with information about the type of collision event and the
 * object that has been collided with.
 * <p>
 * source : http://www.java-tips.org/other-api-tips/java3d/collision-detection-with-java3d.html
 *
 * @author I.J.Palmer
 * @version 1.0
 */

public class CollisionDetector extends Behavior {

    public class CollisionDetectorData {
        /**
         * The shape that is watched for collision.
         */
        public Node collidingShape;
    }

    public static Map<Object3D, Object3D> etatCollisions = new HashMap<Object3D, Object3D>();

    protected CollisionDetectorData data = new CollisionDetectorData();

    private WakeupOr wwakeUpCriteria;

    /**
     * @param theShape  Shape3D that is to be watched for collisions.
     * @param theBounds Bounds that define the active region for this behaviour
     * @param node
     */
    public CollisionDetector(Node theShape, Bounds theBounds) {
        data.collidingShape = theShape;
        setSchedulingBounds(theBounds);
    }

    /**
     * This creates an entry, exit and movement collision criteria. These are
     * then OR'ed together, and the wake up condition set to the result.
     */
    public void initialize() {
        setSchedulingInterval(1);
        WakeupCriterion criterionArray[] = new WakeupCriterion[3];
        criterionArray[0] = new WakeupOnCollisionEntry(data.collidingShape);
        criterionArray[1] = new WakeupOnCollisionExit(data.collidingShape);
        criterionArray[2] = new WakeupOnCollisionMovement(data.collidingShape);
        wwakeUpCriteria = new WakeupOr(criterionArray);
        wakeupOn(wwakeUpCriteria);
    }

    /**
     * Where the work is done in this class. A message is printed out using the
     * userData of the object collided with. The wake up condition is then set
     * to the OR'ed criterion again.
     */
    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if (theCriterion instanceof WakeupOnCollisionEntry) {
                processCollision(theCriterion);
            } else if (theCriterion instanceof WakeupOnCollisionMovement) {
                processCollisionMouvement(theCriterion);
            } else if (theCriterion instanceof WakeupOnCollisionExit) {
                processNoCollision(theCriterion);
            }
        }
        wakeupOn(wwakeUpCriteria);
    }

    private void processNoCollision(WakeupCriterion theCriterion) {
        Node theLeaf = ((WakeupOnCollisionExit) theCriterion).getTriggeringPath().getObject();
        synchronized (etatCollisions) {
            List<Entry<Object3D, Object3D>> aSupprimer = new ArrayList<Map.Entry<Object3D, Object3D>>();
            // Il faut supprimer les couples de collisions : A,B et B,A :
            for (Entry<Object3D, Object3D> entry : etatCollisions.entrySet()) {
                if (entry.getValue() == (Object3D) data.collidingShape.getUserData() && entry.getKey() == (Object3D) theLeaf.getUserData()) {
                    aSupprimer.add(entry);
                } else if (entry.getKey() == (Object3D) data.collidingShape.getUserData() && entry.getValue() == (Object3D) theLeaf.getUserData()) {
                    aSupprimer.add(entry);
                }
            }
            for (Entry<Object3D, Object3D> entry : aSupprimer) {
                etatCollisions.remove(entry.getKey());
            }
        }
        //System.out.println(data.collidingShape.getUserData() + " stopped colliding with " + toString(theLeaf));
    }

    private void processCollision(WakeupCriterion theCriterion) {
        //System.out.println("processCollision");
        Node theLeaf = ((WakeupOnCollisionEntry) theCriterion).getTriggeringPath().getObject();
        synchronized (etatCollisions) {
            etatCollisions.put((Object3D) data.collidingShape.getUserData(), (Object3D) theLeaf.getUserData());
        }
        //System.out.println(data.collidingShape.getUserData() + " collided with " + toString(theLeaf));
    }

    private void processCollisionMouvement(WakeupCriterion theCriterion) {
        Node theLeaf = ((WakeupOnCollisionMovement) theCriterion).getTriggeringPath().getObject();
        synchronized (etatCollisions) {
            etatCollisions.put((Object3D) data.collidingShape.getUserData(), (Object3D) theLeaf.getUserData());
        }
        //System.out.println(data.collidingShape.getUserData() + " collided with " + toString(theLeaf));
    }

    @SuppressWarnings("unused")
    private Object toString(Node theLeaf) {
        return theLeaf.getUserData() == null ? theLeaf.getClass().getSimpleName() : theLeaf.getUserData();
    }
}