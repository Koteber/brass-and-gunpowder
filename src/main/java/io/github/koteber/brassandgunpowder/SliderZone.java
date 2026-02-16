package io.github.koteber.brassandgunpowder;

import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class SliderZone {
    public SliderZone(ArrayList<Vector2f> points, float thickness) {
        this.points = points;
        this.thickness = thickness;
    }
    public ArrayList<Vector2f> points = new ArrayList<>();
    public float thickness;

    public Vector2f getStartPoint() {
        return points.get(0);
    }
    public Vector2f getEndPoint() {
        return points.get(points.size() - 1);
    }
}
