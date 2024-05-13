package creative.design.carrotbow.repository;

import creative.design.carrotbow.domain.CareType;
import creative.design.carrotbow.domain.DogSize;
import creative.design.carrotbow.domain.MatchStatus;
import creative.design.carrotbow.domain.Requirement;
import creative.design.carrotbow.dto.requestForm.RequirementCondForm;
import creative.design.carrotbow.service.GeoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RequirementRepository {

    private final EntityManager em;
    private final GeoService geoService;
    @Value("${spring.jpa.page-size}")
    private int pageSize;

    public Long save(Requirement requirement){
        em.persist(requirement);
        return requirement.getId();
    }

    public Optional<Requirement> findById(Long id){
        return em.createQuery("select r from Requirement r " +
                        " join fetch r.dog" +
                        " where r.id=:id", Requirement.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public Optional<Requirement> findWithApplicationsById(Long id){
        return em.createQuery("select r from Requirement r " +
                        " join fetch r.dog" +
                        " left join fetch r.applications a" +
                        " where r.id=:id", Requirement.class)
                        .setParameter("id", id)
                        .getResultList()
                        .stream().findFirst();
    }

    public List<Requirement> findListByUserId(Long userId, int offset){
        List<Requirement> requirements = em.createQuery("select r from Requirement r" +
                        " join fetch r.dog" +
                        " where r.user.id=:userId" +
                        " order by r.startTime asc", Requirement.class)
                .setParameter("userId", userId)
                .setFirstResult(pageSize * (offset-1))
                .setMaxResults(pageSize)
                .getResultList();

        if(requirements==null){
            requirements = Collections.emptyList();
        }

        return requirements;
    }


    public List<Requirement> findListByLocation(RequirementCondForm condForm, int offset) {

        Point center = geoService.makeGeoData(condForm.getLocation());
        System.out.println(center);
        System.out.println(condForm.getRadius());
        int radius = condForm.getRadius()==0?5000:condForm.getRadius()*1000;

        String queryString = "select r from Requirement r" +
                " join fetch r.dog d" +
                " where r.startTime>:now" +
                " and r.status =:currentStatus" +
                " and st_contains(st_buffer(:center, :radius), r.careLocation)";



        String dogSize = condForm.getDogSize();
        if (dogSize != null) {
            queryString += " and d.weight between :minWeight and :maxWeight";
        }


        String careType = condForm.getCareType();
        if(careType!=null){
            queryString += " and r.careType=:care";
        }

        queryString +=  " order by r.startTime asc";

        Query findQuery = em.createQuery(queryString, Requirement.class)
                .setParameter("now", LocalDateTime.now())
                .setParameter("currentStatus", MatchStatus.NOT_MATCHED)
                .setParameter("center", center)
                .setParameter("radius", radius)
                .setFirstResult(pageSize * (offset-1))
                .setMaxResults(pageSize);

        if (dogSize != null) {
            float minWeight = 0;
            float maxWeight = 100;

            DogSize size = DogSize.valueOf(dogSize);
            if (size == DogSize.SMALL) {
                maxWeight = 9;
            } else if (size == DogSize.MEDIUM) {
                minWeight = 8;
                maxWeight = 17;
            } else if (size == DogSize.LARGE) {
                minWeight = 16;
            }

            findQuery.setParameter("minWeight", minWeight)
                    .setParameter("maxWeight", maxWeight);

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
