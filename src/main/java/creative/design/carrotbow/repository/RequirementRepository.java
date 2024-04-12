package creative.design.carrotbow.repository;

import creative.design.carrotbow.domain.CareType;
import creative.design.carrotbow.domain.DogSize;
import creative.design.carrotbow.domain.Requirement;
import creative.design.carrotbow.dto.RequirementCondForm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RequirementRepository {

    private final EntityManager em;
    private final GeometryFactory geometryFactory;

    public Long save(Requirement requirement){
        em.persist(requirement);
        return requirement.getId();
    }

    public Optional<Requirement> findById(Long id){
        return Optional.ofNullable(em.find(Requirement.class, id));
    }

    public Optional<Requirement> findWithApplicationsById(Long id){
        return em.createQuery("select r from Requirement r " +
                        " join fetch r.dog" +
                        " left join fetch r.applications a" +
                        " left join fetch a.user" +
                        " where r.id=:id", Requirement.class)
                        .setParameter("id", id)
                        .getResultList()
                        .stream().findFirst();
    }

    public List<Requirement> findListByUsername(String username){
        List<Requirement> requirements = em.createQuery("select r from Requirement r" +
                        " join fetch r.dog" +
                        " join fetch r.user u" +
                        " where u.username=:username", Requirement.class)
                .setParameter("username", username)
                .getResultList();

        if(requirements==null){
            requirements = Collections.emptyList();
        }

        return requirements;
    }

    private org.locationtech.jts.geom.Point makeGeoData(org.springframework.data.geo.Point carePoint){
        final Coordinate coordinate = new Coordinate(carePoint.getX(), carePoint.getY());
        org.locationtech.jts.geom.Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326); // SRID 설정

        return point;
    }

    public List<Requirement> findListByLocation(RequirementCondForm condForm) {

        Point center = makeGeoData(condForm.getLocation());
        System.out.println(center);
        System.out.println(condForm.getRadius());
        int radius = condForm.getRadius()==0?5000:condForm.getRadius()*1000;

        String queryString = "select r from Requirement r" +
                " join fetch r.dog d" +
                " where st_contains(st_buffer(:center, :radius), r.careLocation)";



        String dogSize = condForm.getDogSize();
        if (dogSize != null) {
            queryString += " and d.size between minSize and :maxSize";
        }


        String careType = condForm.getCareType();
        if(careType!=null){
            queryString += " and r.careType=:care";
        }


        Query findQuery = em.createQuery(queryString, Requirement.class)
                .setParameter("center", center)
                .setParameter("radius", radius);

        if (dogSize != null) {
            float minSize = 0;
            float maxSize = 100;

            DogSize size = DogSize.valueOf(dogSize);
            if (size == DogSize.SMALL) {
                maxSize = 9;
            } else if (size == DogSize.MEDIUM) {
                minSize = 8;
                maxSize = 17;
            } else if (size == DogSize.LARGE) {
                minSize = 16;
            }

            findQuery.setParameter("minSize", minSize)
                    .setParameter("maxSize", maxSize);

        }

        if(careType!=null){
            CareType care = CareType.valueOf(careType);
            findQuery.setParameter("care", care);
        }

        List<Requirement> requirements = findQuery.getResultList();

        if(requirements==null){
            requirements = Collections.emptyList();
        }

        return requirements;
    }

}
