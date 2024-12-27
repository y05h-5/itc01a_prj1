import java.awt.*;
import java.awt.geom.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Project1 {
    public static void main(String[] args) {
        JFrame jf = new JFrame("Project 1");
        DrawingPanel dp = new DrawingPanel();
        jf.add(dp);
        jf.setSize(800,600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}

class DrawingPanel extends JPanel {
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Point2D.Double p1 = new Point2D.Double(100,100);
        Point2D.Double p2 = new Point2D.Double(200,300);
        g2d.draw(new Line2D.Double(p1,p2));
    }
}