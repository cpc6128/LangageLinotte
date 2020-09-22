package org.linotte.greffons.impl.xyz.abstraction;

import org.linotte.greffons.impl.xyz.outils.CollisionDetector;
import org.linotte.greffons.outils.ObjetLinotteFactory;

import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;
import java.math.BigDecimal;
import java.util.Map.Entry;

public abstract class Object3D extends Composant3D {

    protected TransformGroup transformGroup = null;

    protected Node node = null;

    public void initialisation(Node pnode) throws GreffonException {
        node = (Node) pnode;
        node.setUserData(this);
        transformGroup = new TransformGroup(new Transform3D());
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        transformGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        transformGroup.addChild(pnode);

        // mode debug
		/*
		Appearance app = new Appearance();
		PolygonAttributes polattr = new PolygonAttributes();
		polattr.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		app.setPolygonAttributes(polattr);

		if (node instanceof Primitive) {
			((Primitive) node).setAppearance(app);
		} else
		if (node instanceof Shape3D) {
			((Shape3D) node).setAppearance(app);
		}*/

    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public boolean fireProperty(String clef) throws GreffonException {
        if (!super.fireProperty(clef)) {
        }
        return false;
    }

    @Override
    public Node getComposant3D() {
        return transformGroup;
    }

    @Slot()
    public boolean rotationx(double l) {
        Transform3D temp = new Transform3D();
        Transform3D transformFinal = new Transform3D();
        transformGroup.getTransform(transformFinal);
        temp.rotX(l);
        transformFinal.mul(temp);
        transformGroup.setTransform(transformFinal);
        return true;
    }

    @Slot()
    public boolean rotationy(double l) {
        Transform3D temp = new Transform3D();
        Transform3D transformFinal = new Transform3D();
        transformGroup.getTransform(transformFinal);
        temp.rotY(l);
        transformFinal.mul(temp);
        transformGroup.setTransform(transformFinal);
        return true;
    }

    @Slot()
    public boolean rotationz(double l) {
        Transform3D temp = new Transform3D();
        Transform3D transformFinal = new Transform3D();
        transformGroup.getTransform(transformFinal);
        temp.rotZ(l);
        transformFinal.mul(temp);
        transformGroup.setTransform(transformFinal);
        return true;
    }

    @Slot()
    public boolean translation(float x, float y, float z) {
        Transform3D temp = new Transform3D();
        Transform3D transformFinal = new Transform3D();
        transformGroup.getTransform(transformFinal);
        temp.set(new Vector3f(x, y, z));
        transformFinal.mul(temp);
        transformGroup.setTransform(transformFinal);
        return true;
    }

    @Override
    public void destruction() throws GreffonException {
        transformGroup = null;
    }

    @Slot
    public boolean estencollisionavec(Espece espece) throws GreffonException {

        Acteur __prototype = espece.getAttribut(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT);
        BigDecimal prototypeid = (BigDecimal) __prototype.getValeur();

        synchronized (CollisionDetector.etatCollisions) {
            // Il faut supprimer les couples de collisions : A,B et B,A :
            for (Entry<Object3D, Object3D> entry : CollisionDetector.etatCollisions.entrySet()) {
                if (entry.getValue() == this
                        && entry.getKey().getAttributeAsBigDecimal(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT).equals(prototypeid)) {
                    return true;
                } else if (entry.getKey() == this
                        && entry.getValue().getAttributeAsBigDecimal(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT).equals(prototypeid)) {
                    return true;
                }
            }
        }
        return false;
    }

}