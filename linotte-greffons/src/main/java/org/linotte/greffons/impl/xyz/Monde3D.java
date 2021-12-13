package org.linotte.greffons.impl.xyz;

import com.sun.j3d.utils.universe.SimpleUniverse;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.impl.swing.ComposantDeplacable;
import org.linotte.greffons.impl.xyz.abstraction.Composant3D;
import org.linotte.greffons.impl.xyz.outils.CollisionDetector;
import org.linotte.greffons.impl.xyz.outils.KeyListener3D;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;
import java.awt.*;

/**
 * @author R.M
 */
public class Monde3D extends ComposantDeplacable {

    private Canvas3D canvas3d = null;
    // on crÃ©e le Bg principal
    private BranchGroup scene = null;

    private SimpleUniverse simpleUniverse;

    private KeyListener3D keyListener;

    static {
        // Juste pour provoquer une erreur si les librairies 3D ne sont pas prÃ©sentes ou pas initialisÃ©es !
        VirtualUniverse.getProperties();
    }

    @Override
    public void initialisation() throws GreffonException {
        if (canvas3d != null) {
            super.initEvenement();
            return;
        }
        setVisible(getAttributeAsBigDecimal("visible").intValue()==1);
        canvas3d = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        // Position :
        x = getAttributeAsBigDecimal("x").intValue();
        y = getAttributeAsBigDecimal("y").intValue();
        // initEvenement();
        // Dimensions :
        int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
        int largeur = getAttributeAsBigDecimal("largeur").intValue();
        canvas3d.setPreferredSize(new Dimension(largeur, hauteur));
        canvas3d.setMaximumSize(new Dimension(largeur, hauteur));
        canvas3d.setVisible(isVisible());

        scene = new BranchGroup();
        scene.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        scene.setCapability(Group.ALLOW_CHILDREN_WRITE);
        scene.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        scene.setCapability(BranchGroup.ALLOW_DETACH);
        simpleUniverse = new SimpleUniverse(canvas3d);
        simpleUniverse.getViewingPlatform().setNominalViewingTransform();
        simpleUniverse.addBranchGraph(scene);
        keyListener = new KeyListener3D(LinotteFacade.getToile().getPanelLaToile());
        canvas3d.addKeyListener(keyListener);

        super.initEvenement();
    }

    @Override
    public void ajouterComposant(Composant composant) throws GreffonException {
        if (composant instanceof Composant3D) {
            Composant3D c3d = (Composant3D) composant;
            BranchGroup bg = new BranchGroup();
            if (c3d.getComposant3D().getParent() == null) {
                bg.addChild(c3d.getComposant3D());
                if (c3d.getNode() != null) {
                    CollisionDetector myColDet = new CollisionDetector(c3d.getNode(), c3d.getNode().getBounds());
                    bg.addChild(myColDet);
                }
            }
            scene.addChild(bg);
        }
    }

    @Override
    public Component getJComponent() throws GreffonException {
        return canvas3d;
    }

    @Override
    public boolean fireProperty(String clef) throws GreffonException {
        if (!super.fireProperty(clef)) {
        }
        return false;
    }

    @Override
    public void destruction() throws GreffonException {
        scene.detach();
        keyListener.terminer();
        canvas3d = null;
    }

    @Override
    public boolean enregistrerPourDestruction() {
        return true;
    }

    @Slot()
    public boolean translation(float x, float y, float z) {
        Transform3D temp = new Transform3D();
        temp.set(new Vector3f(x, y, z));
        applyTransform3DToUniverse(temp);
        return true;
    }

    @Slot()
    public boolean rotationx(double l) {
        Transform3D temp = new Transform3D();
        temp.rotX(l);
        applyTransform3DToUniverse(temp);
        return true;
    }

    @Slot()
    public boolean rotationy(double l) {
        Transform3D temp = new Transform3D();
        temp.rotY(l);
        applyTransform3DToUniverse(temp);
        return true;
    }

    @Slot()
    public boolean rotationz(double l) {
        Transform3D temp = new Transform3D();
        temp.rotZ(l);
        applyTransform3DToUniverse(temp);
        return true;
    }

    @Slot()
    public boolean inverse() {
        Transform3D transformFinal = new Transform3D();
        simpleUniverse.getViewingPlatform().getViewPlatformTransform().getTransform(transformFinal);
        transformFinal.invert();
        simpleUniverse.getViewingPlatform().getViewPlatformTransform().setTransform(transformFinal);
        return true;
    }

    @Slot()
    public boolean focus() {
        if (!canvas3d.isFocusOwner())
            canvas3d.requestFocusInWindow();
        return true;
    }

    private void applyTransform3DToUniverse(Transform3D temp) {
        Transform3D transformFinal = new Transform3D();
        simpleUniverse.getViewingPlatform().getViewPlatformTransform().getTransform(transformFinal);
        transformFinal.mul(temp);
        simpleUniverse.getViewingPlatform().getViewPlatformTransform().setTransform(transformFinal);
    }

}