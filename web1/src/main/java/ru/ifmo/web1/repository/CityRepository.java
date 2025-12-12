package ru.ifmo.web1.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ifmo.web1.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findFirstByMetersAboveSeaLevelLessThan(Integer meters);

    @Query("SELECT c FROM City c ORDER BY c.name ASC LIMIT 1")
    Optional<City> findCityWithMinName();

    @Query("SELECT c FROM City c ORDER BY c.climate DESC LIMIT 1")
    Optional<City> findCityWithMaxClimate();

    List<City> findByNameContainingIgnoreCase(String name);

    List<City> findByPopulation(Integer population);

    List<City> findByArea(Integer area);

    List<City> findByClimate(ru.ifmo.web1.model.Climate climate);

    List<City> findByCapital(Boolean capital);
}
