package org.linotte.greffons.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.linotte.greffons.externe.Graphique;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class Horloge extends Graphique {

    // Attributs de l'objet Horloge
    int heure = 0;
    int minute = 0;
    int seconde = 0;

    // Methodes liees a l aspect graphique
    @Override
    public void projette(Graphics2D dessin) throws GreffonException {

        int x = getAttributeAsBigDecimal("x").intValue();
        int y = getAttributeAsBigDecimal("y").intValue();
        int rayon = getAttributeAsBigDecimal("rayon").intValue();
        dessin.setPaint(Color.DARK_GRAY);
        dessin.setStroke(new BasicStroke(3));
        dessin.drawOval(x, y, rayon * 2, rayon * 2);
        int x2 = x + rayon;
        int y2 = y + rayon;
        // Affichage de l'heure
        dessin.setPaint(Color.BLUE);
        dessin.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        dessin.drawLine(x2, y2, (int) (x2 + rayon * 0.6 * Math.cos((heure - 3) * 2 * Math.PI / 12)),
                (int) (y2 + rayon * 0.6 * Math.sin((heure - 3) * 2 * Math.PI / 12)));
        // Affichage des minutes
        dessin.setPaint(Color.BLACK);
        dessin.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        dessin.drawLine(x2, y2, (int) (x2 + rayon * 0.85 * Math.cos((minute - 15) * 2 * Math.PI / 60)),
                (int) (y2 + rayon * 0.85 * Math.sin((minute - 15) * 2 * Math.PI / 60)));
        // Affichage de la trotteuse
        dessin.setPaint(Color.RED);
        dessin.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        dessin.drawLine(x2, y2, (int) (x2 + rayon * 0.9 * Math.cos((seconde - 15) * 2 * Math.PI / 60)),
                (int) (y2 + rayon * 0.9 * Math.sin((seconde - 15) * 2 * Math.PI / 60)));

        // Les chiffres
        dessin.setPaint(Color.BLACK);
        for (int c = 1; c < 14; c++)
            dessin.drawString(String.valueOf(c), (int) (x2 + rayon * 1.2 * Math.cos((c - 15) * 2 * Math.PI / 12)), (int)
                    (y2 + rayon * 1.2 * Math.sin((c - 15) * 2 * Math.PI / 12)));

        return;
    }

    // Methodes liee au greffo Labyrinthe

    @Slot
    public boolean heure(int h) {
        heure = h;
        //Force le rafraichissement de la toile :
        getRessourceManager().
                setChangement();
        return true;
    }

    @Slot
    public boolean minute(int m) {
        minute = m;
        //Force le rafraichissement de la toile :
        getRessourceManager().
                setChangement();
        return true;
    }

    @Slot
    public boolean seconde(int s) {
        seconde = s;
        //Force le rafraichissement de la toile :
        getRessourceManager().
                setChangement();
        return true;
    }


    @Override
    public Shape getShape() {
        return null;
    }

}