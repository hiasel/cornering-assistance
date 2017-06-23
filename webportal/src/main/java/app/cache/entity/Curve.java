package app.cache.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity class for Parameters that influence assistance in the app
 */
@Document(collection = "curves")
public class Curve {
    @Id
    public String id;

    private double radius;
    private double length;
    private String type;
    private Object centerPoint;
    private Object startPoint;
    private Object endPoint;

    public Curve() {
    }

    public Curve(String id, double radius, double length, String type, Object centerPoint, Object startPoint, Object endPoint) {
        this.id = id;
        this.radius = radius;
        this.length = length;
        this.type = type;
        this.centerPoint = centerPoint;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Object centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Object getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Object startPoint) {
        this.startPoint = startPoint;
    }

    public Object getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Object endPoint) {
        this.endPoint = endPoint;
    }
}
