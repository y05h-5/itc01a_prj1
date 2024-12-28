import java.awt.*;
import java.awt.geom.*;
import java.util.Scanner;

import javax.swing.*;
import java.nio.file.*;

public class Project1 {
    public static void main(String[] args) {
        JFrame jf = new JFrame("Project 1");
        DrawingPanel dp = (args.length < 1)?
            new DrawingPanel("vert/disk.vert") :
            new DrawingPanel(args[0]);

        jf.add(dp);
        jf.setSize(800,600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}

class DrawingPanel extends JPanel {
    private Scanner input;

    public DrawingPanel (String fname) {
        Path fp = Paths.get(fname);
        try {
            input = new Scanner(fp);
        } catch (Exception e) {
            System.err.println("Scanner creation failed");
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int height = getHeight();
        int width = getWidth();

        int cmp = input.nextInt();
        for (int c = 0; c < cmp; ++c) {
            int vrt = input.nextInt();
            System.out.println(vrt);
            Double x0 = -1 * (input.nextDouble()) + (width/2);
            Double y0 = -1 * (input.nextDouble()) + (height/2);
            Double prevX = x0;
            Double prevY = y0;

            for (int v = 1; v < vrt; ++v) {
                Double x = -1 * (input.nextDouble()) + (width/2);
                Double y = -1 * (input.nextDouble()) + (height/2);
                g2d.draw(new Line2D.Double(prevX, prevY, x, y));
                prevX = x;
                prevY = y;
            }
            g2d.draw(new Line2D.Double(prevX, prevY, x0, y0));
        }

        input.close();
    }
}