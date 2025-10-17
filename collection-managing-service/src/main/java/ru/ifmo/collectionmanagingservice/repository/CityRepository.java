package ru.ifmo.collectionmanagingservice.repository;

import ru.ifmo.collectionmanagingservice.model.City;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import ru.ifmo.collectionmanagingservice.model.Climate;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CityRepository {

    @PersistenceContext(unitName = "cityPU")
    private EntityManager em;

    @Transactional
    public City save(City city) {
        if (city.getId() == null) {
            em.persist(city);
            return city;
        } else {
            return em.merge(city);
        }
    }

    public Optional<City> findById(Long id) {
        City city = em.find(City.class, id);
        return Optional.ofNullable(city);
    }

    public List<City> findAll() {
        return em.createQuery("SELECT c FROM City c", City.class).getResultList();
    }

    public List<City> findWithPaginationAndSorting(int page, int size, String sort) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<City> cq = cb.createQuery(City.class);
        Root<City> city = cq.from(City.class);

        if (sort != null && !sort.isEmpty()) {
            String[] sortFields = sort.split(",");
            for (String field : sortFields) {
                boolean desc = field.startsWith("-");
                String fieldName = desc ? field.substring(1) : field;

                Path<?> path = getPath(city, fieldName);
                if (desc) {
                    cq.orderBy(cb.desc(path));
                } else {
                    cq.orderBy(cb.asc(path));
                }
            }
        }

        TypedQuery<City> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public List<City> findWithFilters(int page, int size, String sort,
                                      java.util.Map<String, String> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<City> cq = cb.createQuery(City.class);
        Root<City> city = cq.from(City.class);

        Predicate predicate = cb.conjunction();

        for (java.util.Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) continue;
            if (key.equals("page") || key.equals("size") || key.equals("sort")) continue;

            switch (key) {
                case "name":
                    predicate = cb.and(predicate,
                            cb.like(cb.lower(city.get("name")),
                                    "%" + value.toLowerCase() + "%"));
                    break;
                case "population":
                    predicate = cb.and(predicate,
                            cb.equal(city.get("population"), Integer.valueOf(value)));
                    break;
                case "area":
                    predicate = cb.and(predicate,
                            cb.equal(city.get("area"), Integer.valueOf(value)));
                    break;
                case "climate":
                    predicate = cb.and(predicate,
                            cb.equal(city.get("climate"),
                                    Climate.valueOf(value)));
                    break;
                case "capital":
                    predicate = cb.and(predicate,
                            cb.equal(city.get("capital"), Boolean.valueOf(value)));
                    break;
            }
        }

        cq.where(predicate);

        if (sort != null && !sort.isEmpty()) {
            String[] sortFields = sort.split(",");
            java.util.List<Order> orders = new java.util.ArrayList<>();

            for (String field : sortFields) {
                boolean desc = field.startsWith("-");
                String fieldName = desc ? field.substring(1) : field;

                Path<?> path = getPath(city, fieldName);
                if (desc) {
                    orders.add(cb.desc(path));
                } else {
                    orders.add(cb.asc(path));
                }
            }
            cq.orderBy(orders);
        }

        TypedQuery<City> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private Path<?> getPath(Root<City> root, String fieldName) {
        switch (fieldName) {
            case "id":
            case "name":
            case "area":
            case "population":
            case "creationDate":
            case "climate":
            case "capital":
            case "agglomeration":
            case "metersAboveSeaLevel":
                return root.get(fieldName);
            default:
                return root.get("id");
        }
    }

    @Transactional
    public void delete(City city) {
        em.remove(em.contains(city) ? city : em.merge(city));
    }

    @Transactional
    public boolean deleteById(Long id) {
        return findById(id).map(city -> {
            delete(city);
            return true;
        }).orElse(false);
    }

    public Optional<City> findCityWithMinName() {
        return em.createQuery(
                        "SELECT c FROM City c ORDER BY c.name ASC", City.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<City> findCityWithMaxClimate() {
        return em.createQuery(
                        "SELECT c FROM City c ORDER BY c.climate DESC", City.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<City> findFirstByMetersLessThan(Integer meters) {
        return em.createQuery(
                        "SELECT c FROM City c WHERE c.metersAboveSeaLevel < :meters", City.class)
                .setParameter("meters", meters)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(c) FROM City c", Long.class)
                .getSingleResult();
    }
}