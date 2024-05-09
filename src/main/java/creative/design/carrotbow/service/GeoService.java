package creative.design.carrotbow.service;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeoService {

    private final GeometryFactory geometryFactory;

    public Point makeGeoData(org.springframework.data.geo.Point carePoint){
        final Coordinate coordinate = new Coordinate(carePoint.getX(), carePoint.getY());
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326); // SRID 설정

        return point;
    }

    public org.springframework.data.geo.Point makePoint(Point point){
        return new org.springframework.data.geo.Point(point.getX(), point.getY());
    }


}
