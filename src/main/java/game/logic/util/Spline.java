package game.logic.util;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Spline {
    public ArrayList<Vector2f> points;

    public Spline(List<Vector2f> points) {
        this.points = new ArrayList<>(points);
    }

    public float calculateBezier(float t) {
        Vector2f result = new Vector2f();
        int n = points.size() - 1;
        for (int i = 0; i <= n ; i++) {
            result.add(points.get(i).mul((float) (binomialCoefficient(n, i) * Math.pow(1 - t, n - i) * Math.pow(t, i)), new Vector2f()));
        }
        return result.y;
    }

    public float calculateLinear(float t) {
        Vector2f start = new Vector2f();
        Vector2f end = new Vector2f();
        for (int i = 0; i < this.points.size(); i++) {
            Vector2f point = this.points.get(i);
            if(point.x == t) return point.y;
            if(point.x > t) {
                if(i > 0) {
                    end = point;
                    start = this.points.get(i - 1);
                } else {
                    return point.y;
                }
                break;
            }
        }
        float pointDifference = end.x - start.x;
        float percentageFromStartToEndSegmentPoints = (t - start.x) / pointDifference;
        float yDifference = end.y - start.y;
        return start.y + yDifference * percentageFromStartToEndSegmentPoints;
    }

    public static int binomialCoefficient(int n, int k) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    public static int factorial(int a) {
        int result = 1;
        for (int i = 1; i <= a; i++) {
            result = result * i;
        }
        return result;
    }
}
