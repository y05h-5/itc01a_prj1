import java.awt.*;
import java.awt.geom.*;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.*;
import java.nio.file.*;

public class Project1 {
    public static void main(String[] args) {
        JFrame jf = new JFrame("Project 1");
        DrawingPanel dp = (args.length >= 1)? new DrawingPanel(args[0]) : new DrawingPanel("vert/riderr.vert");

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
    private Path2D.Double lines;
    private Path2D.Double tangents;
    private Path2D.Double normals;
    private float scalingfactor;
    private ArrayList<Point2D.Double> points;
    private ArrayList<Curvature> curvature;

    public DrawingPanel(String fname) {
        Path fp = Paths.get(fname);
        try {
            input = new Scanner(fp);
        } catch (Exception e) {
            System.err.println("Scanner creation failed");
        }

        nComponent = input.nextInt();
        nVertex = new ArrayList<Integer>();
        scalingfactor = 20.0f;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int height = getHeight();
        int width = getWidth();

        AffineTransform t1 = new AffineTransform();
        t1.translate(width/2, height/2); // use width and height instead when on retina display??
        // t1.translate(width, height); // use width and height instead when on retina display??
        t1.rotate(Math.PI); // since the rider vert file is somehow flipped??
        t1.scale(scalingfactor, scalingfactor);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // parse the vert file & create paths
        parsePoints();
        makeLines();

        g2d.setTransform(t1);
        g2d.setStroke(new BasicStroke(2/scalingfactor));

        // draw main shape
        g2d.setColor(Color.BLACK);
        g2d.draw(lines);
        // drwa tangents
        g2d.setColor(Color.RED);
        g2d.draw(tangents);
        // draw normals
        g2d.setColor(Color.BLUE);
        g2d.draw(normals);
    }

    private Point2D.Double getUnitTangent(Point2D.Double p1, Point2D.Double p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double mag = Math.sqrt(dx*dx + dy*dy);
        return new Point2D.Double(dx/mag, dy/mag);
    }

    private Point2D.Double getUnitNormal(Point2D.Double tangent) {
        return new Point2D.Double(-tangent.y, tangent.x); // from the definition in the lecture slide
    }

    // used for drawing tangent and normal vectors
    // Claude helped me to implement this part
    private void getVectorPath(Path2D.Double path, Point2D.Double start, Point2D.Double vector) {
        path.moveTo(start.x, start.y);
        path.lineTo(start.x + vector.x, start.y + vector.y);
    }

    private void parsePoints() {
        points = new ArrayList<Point2D.Double>();

        for (int c = 0; c < nComponent; ++c) {
            int vcnt = input.nextInt();
            nVertex.add(vcnt);

            for (int v = 0; v < vcnt; ++v) {
                double x = input.nextDouble();
                double y = input.nextDouble();
                points.add(new Point2D.Double(x, y));
            }
        }
        input.close();
    }

    private void makeLines() {
        lines = new Path2D.Double();
        tangents = new Path2D.Double();
        normals = new Path2D.Double();

        for (int c = 0, offset = 0; c < nComponent; ++c) {
            int nvert = nVertex.get(c);
            Point2D.Double start = points.get(offset);
            lines.moveTo(start.x, start.y);

            for (int v = 1; v < nvert; ++v) {
                Point2D.Double cur = points.get(v+offset);
                Point2D.Double prev = points.get(v+offset-1);

                Point2D.Double vtan = getUnitTangent(prev, cur); // get tangents
                Point2D.Double vnorm = getUnitNormal(vtan); // get normals

                // contrlo points
                double magT = 0.05;
                // double magN = 0.05;
                // Point2D.Double ctl = new Point2D.Double(
                //     (cur.x+prev.x)/2 - vtan.x * magT + vnorm.x * magN,
                //     (cur.y+prev.y)/2 - vtan.y * magT + vnorm.y * magN
                // );
                Point2D.Double ctl = new Point2D.Double(
                    prev.x + vtan.x * magT,
                    prev.y + vtan.y * magT
                );

                lines.quadTo(ctl.x, ctl.y, cur.x, cur.y);

                // get tangent & normal vectors for every 20 points
                if ((v-1)%20 == 0) { // idea of this was suggested by ChatGPT
                    getVectorPath(tangents, prev, vtan); // create unit tangents
                    getVectorPath(normals, prev, vnorm); // create unit normals
                }

                if (v+1 == nvert) break;
                Point2D.Double next = points.get(v+offset+1);
                double angle1 = Math.atan2(prev.y-cur.y, prev.x-cur.x);
                double angle2 = Math.atan2(cur.y - next.y, cur.x - next.x);
                double diff = angle2 - angle1;
                System.out.printf("Curvature at point %d = %f\n", v, diff);
            }
            lines.closePath();
            offset += nvert;
        }
    }
}

class Curvature {
    Point2D.Double position;
    double curvature;

    Curvature(Point2D.Double pos, double curv) {
        this.position = pos;
        this.curvature = curv;
    }
}