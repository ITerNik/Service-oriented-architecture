package ru.ifmo.collectionmanagingservice.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import ru.ifmo.collectionmanagingservice.config.MongoDBProducer;
import ru.ifmo.collectionmanagingservice.model.City;
import ru.ifmo.collectionmanagingservice.model.Climate;
import ru.ifmo.collectionmanagingservice.model.Coordinates;
import ru.ifmo.collectionmanagingservice.model.Human;

import java.time.ZoneId;
import java.util.*;

@ApplicationScoped
public class CityRepository {

    @Inject
    private MongoDBProducer mongoDBProducer;

    private MongoCollection<Document> getCollection() {
        MongoClient mongoClient = mongoDBProducer.createMongoClient();
        MongoDatabase database = mongoClient.getDatabase("citydb");
        return database.getCollection("cities");
    }

    public City save(City city) {
        MongoCollection<Document> collection = getCollection();

        if (city.getId() == null) {
            city.setId(new ObjectId().toString());
        }

        Document doc = cityToDocument(city);

        Document existing = collection.find(Filters.eq("_id", city.getId())).first();
        if (existing != null) {
            collection.replaceOne(Filters.eq("_id", city.getId()), doc);
        } else {
            collection.insertOne(doc);
        }

        return city;
    }

    public Optional<City> findById(String id) {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public List<City> findAll() {
        MongoCollection<Document> collection = getCollection();
        List<City> cities = new ArrayList<>();
        collection.find().forEach(doc -> cities.add(documentToCity(doc)));
        return cities;
    }

    public List<City> findWithFilters(int page, int size, String sort,
            Map<String, String> filters) {
        MongoCollection<Document> collection = getCollection();

        List<Bson> filterList = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty())
                continue;
            if (key.equals("page") || key.equals("size") || key.equals("sort"))
                continue;

            switch (key) {
                case "name":
                    filterList.add(Filters.regex("name", ".*" + value + ".*", "i"));
                    break;
                case "population":
                    filterList.add(Filters.eq("population", Integer.valueOf(value)));
                    break;
                case "area":
                    filterList.add(Filters.eq("area", Integer.valueOf(value)));
                    break;
                case "climate":
                    filterList.add(Filters.eq("climate", value));
                    break;
                case "capital":
                    filterList.add(Filters.eq("capital", Boolean.valueOf(value)));
                    break;
                case "metersAboveSeaLevel":
                    filterList.add(Filters.eq("metersAboveSeaLevel", Integer.valueOf(value)));
                    break;
            }
        }

        Bson filter = filterList.isEmpty() ? new Document() : Filters.and(filterList);

        Bson sortBson = buildSort(sort);

        List<City> cities = new ArrayList<>();
        collection.find(filter)
                .sort(sortBson)
                .skip(page * size)
                .limit(size)
                .forEach(doc -> cities.add(documentToCity(doc)));

        return cities;
    }

    private Bson buildSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sorts.ascending("_id");
        }

        String[] sortFields = sort.split(",");
        List<Bson> sorts = new ArrayList<>();

        for (String field : sortFields) {
            boolean desc = field.startsWith("-");
            String fieldName = desc ? field.substring(1) : field;

            String mongoField = mapFieldName(fieldName);

            if (desc) {
                sorts.add(Sorts.descending(mongoField));
            } else {
                sorts.add(Sorts.ascending(mongoField));
            }
        }

        return sorts.size() == 1 ? sorts.get(0) : Sorts.orderBy(sorts);
    }

    private String mapFieldName(String fieldName) {
        switch (fieldName) {
            case "id":
                return "_id";
            default:
                return fieldName;
        }
    }

    public void delete(City city) {
        MongoCollection<Document> collection = getCollection();
        collection.deleteOne(Filters.eq("_id", city.getId()));
    }

    public boolean deleteById(String id) {
        MongoCollection<Document> collection = getCollection();
        return collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
    }

    public Optional<City> findCityWithMinName() {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find()
                .sort(Sorts.ascending("name"))
                .limit(1)
                .first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public Optional<City> findCityWithMaxClimate() {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find()
                .sort(Sorts.descending("climate"))
                .limit(1)
                .first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public Optional<City> findFirstByMetersLessThan(Integer meters) {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find(Filters.lt("metersAboveSeaLevel", meters))
                .limit(1)
                .first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public long count() {
        MongoCollection<Document> collection = getCollection();
        return collection.countDocuments();
    }

    private Document cityToDocument(City city) {
        Document doc = new Document();
        doc.put("_id", city.getId());
        doc.put("name", city.getName());

        if (city.getCoordinates() != null) {
            Document coords = new Document();
            coords.put("x", city.getCoordinates().getX());
            coords.put("y", city.getCoordinates().getY());
            doc.put("coordinates", coords);
        }

        if (city.getCreationDate() != null) {
            doc.put("creationDate", Date.from(city.getCreationDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        doc.put("area", city.getArea());
        doc.put("population", city.getPopulation());
        doc.put("metersAboveSeaLevel", city.getMetersAboveSeaLevel());
        doc.put("agglomeration", city.getAgglomeration());
        doc.put("capital", city.getCapital());

        if (city.getClimate() != null) {
            doc.put("climate", city.getClimate().name());
        }

        if (city.getGovernor() != null) {
            Document governor = new Document();
            governor.put("height", city.getGovernor().getHeight());
            doc.put("governor", governor);
        }

        return doc;
    }

    private City documentToCity(Document doc) {
        City city = new City();
        city.setId(doc.getString("_id"));
        city.setName(doc.getString("name"));

        Document coordsDoc = doc.get("coordinates", Document.class);
        if (coordsDoc != null) {
            Coordinates coords = new Coordinates();
            coords.setX(coordsDoc.getDouble("x"));
            coords.setY(coordsDoc.getDouble("y"));
            city.setCoordinates(coords);
        }

        Date creationDate = doc.getDate("creationDate");
        if (creationDate != null) {
            city.setCreationDate(creationDate.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }

        city.setArea(doc.getInteger("area"));
        city.setPopulation(doc.getInteger("population"));
        city.setMetersAboveSeaLevel(doc.getInteger("metersAboveSeaLevel"));

        Object agglomeration = doc.get("agglomeration");
        if (agglomeration != null) {
            if (agglomeration instanceof Double) {
                city.setAgglomeration(((Double) agglomeration).floatValue());
            } else if (agglomeration instanceof Float) {
                city.setAgglomeration((Float) agglomeration);
            } else if (agglomeration instanceof Integer) {
                city.setAgglomeration(((Integer) agglomeration).floatValue());
            }
        }

        city.setCapital(doc.getBoolean("capital"));

        String climateStr = doc.getString("climate");
        if (climateStr != null) {
            city.setClimate(Climate.valueOf(climateStr));
        }

        Document governorDoc = doc.get("governor", Document.class);
        if (governorDoc != null) {
            Human governor = new Human();
            Object height = governorDoc.get("height");
            if (height != null) {
                if (height instanceof Double) {
                    governor.setHeight((Double) height);
                } else if (height instanceof Integer) {
                    governor.setHeight(((Integer) height).doubleValue());
                }
            }
            city.setGovernor(governor);
        }

        return city;
    }
}