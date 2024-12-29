import java.awt.*;
import java.awt.geom.*;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.*;
import java.nio.file.*;

public class Project1 {
    public static void main(String[] args) {
        JFrame jf = new JFrame("Project 1");
        DrawingPanel dp = (args.length < 1)?
            new DrawingPanel("vert/riderr.vert") :
            new DrawingPanel(args[0]);

        jf.add(dp);
        jf.setSize(800,600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
}

class DrawingPanel extends JPanel {
    private Scanner input;
    private int nComponent;
    private ArrayList<Integer> nVertex;
    private ArrayList<Line2D.Double> lines;
    private Stroke s;

    public DrawingPanel (String fname) {
        Path fp = Paths.get(fname);
        try {
            input = new Scanner(fp);
        } catch (Exception e) {
            System.err.println("Scanner creation failed");
        }
        nComponent = input.nextInt();
        nVertex = new ArrayList<Integer>();
        lines = new ArrayList<Line2D.Double>();
        s = new BasicStroke(1);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int height = getHeight();
        int width = getWidth();

        AffineTransform t1 = new AffineTransform();
        t1.translate(width, height);
        t1.rotate(Math.PI);
        t1.scale(10,10);

        parseLines(g2d);
        g2d.setTransform(t1);
        drawLines(g2d);
        input.close();
    }

    private void parseLines(Graphics2D g2d) {
        for (int c = 0; c < nComponent; ++c) {
            nVertex.add(input.nextInt());
            // System.out.println(nVertex.get(c)); // debug message
            Double x0 = input.nextDouble();
            Double y0 = input.nextDouble();
            Double prevX = x0, prevY = y0;

            for (int v = 1; v < nVertex.get(c); ++v) {
                Double x = input.nextDouble();
                Double y = input.nextDouble();
                lines.add(new Line2D.Double(prevX, prevY, x, y));
                prevX = x;
                prevY = y;
            }
            lines.add(new Line2D.Double(prevX, prevY, x0, y0));
        }
    }

    private void drawLines(Graphics2D g2d) {
        int offset = 0;
        for (int c = 0; c < nComponent; ++c) {
            for (int v = offset; v < nVertex.get(c)+offset; ++v)
                g2d.draw(lines.get(v));
            offset += nVertex.get(c);
        }
    }
}