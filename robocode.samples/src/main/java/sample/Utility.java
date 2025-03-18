package sample;

import java.awt.*;

public class Utility {
    public static void drawRedHair(Graphics2D g,int aimX, int aimY) {
        g.setColor(Color.RED);
        g.drawOval(aimX - 15, aimY - 15, 30, 30);
        g.drawLine(aimX, aimY - 4, aimX, aimY + 4);
        g.drawLine(aimX - 4, aimY, aimX + 4, aimY);
    }
}
