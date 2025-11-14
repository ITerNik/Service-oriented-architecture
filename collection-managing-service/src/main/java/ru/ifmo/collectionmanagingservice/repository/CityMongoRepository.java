package ru.ifmo.collectionmanagingservice.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import ru.ifmo.collectionmanagingservice.model.City;
import ru.ifmo.collectionmanagingservice.model.Climate;
import ru.ifmo.collectionmanagingservice.model.Coordinates;
import ru.ifmo.collectionmanagingservice.model.Human;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;

@ApplicationScoped
public class CityMongoRepository {
    @Inject
    private MongoClient mongoClient;

    private static final String DATABASE_NAME = "serviceoriented";
    private static final String COLLECTION_NAME = "cities";

    private MongoCollection<Document> getCollection() {
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        return database.getCollection(COLLECTION_NAME);
    }

    public City save(City city) {
        MongoCollection<Document> collection = getCollection();
        Document doc = cityToDocument(city);

        if (city.getId() == null || city.getId().isEmpty()) {
            collection.insertOne(doc);
            city.setId(doc.getObjectId("_id").toString());
        } else {
            ObjectId objectId = new ObjectId(city.getId());
            doc.remove("_id");
            collection.replaceOne(eq("_id", objectId), doc);
        }
        return city;
    }

    public Optional<City> findById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            MongoCollection<Document> collection = getCollection();
            Document doc = collection.find(eq("_id", objectId)).first();
            return Optional.ofNullable(doc).map(this::documentToCity);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public List<City> findAll() {
        MongoCollection<Document> collection = getCollection();
        List<City> cities = new ArrayList<>();
        collection.find().forEach(doc -> cities.add(documentToCity(doc)));
        return cities;
    }

    public List<City> findWithFilters(int page, int size, String sort, Map<String, String> filters) {
        MongoCollection<Document> collection = getCollection();

        List<Bson> bsonFilters = new ArrayList<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) continue;
            if (key.equals("page") || key.equals("size") || key.equals("sort")) continue;

            switch (key) {
                case "name":
                    bsonFilters.add(regex("name", ".*" + value + ".*", "i"));
                    break;
                case "population":
                    bsonFilters.add(eq("population", Integer.valueOf(value)));
                    break;
                case "area":
                    bsonFilters.add(eq("area", Integer.valueOf(value)));
                    break;
                case "climate":
                    bsonFilters.add(eq("climate", value));
                    break;
                case "capital":
                    bsonFilters.add(eq("capital", Boolean.valueOf(value)));
                    break;
            }
        }

        Bson filter = bsonFilters.isEmpty() ? new Document() : and(bsonFilters);

        // Build sort
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
            return ascending("_id");
        }

        String[] sortFields = sort.split(",");
        List<Bson> sorts = new ArrayList<>();

        for (String field : sortFields) {
            boolean desc = field.startsWith("-");
            String fieldName = desc ? field.substring(1) : field;

            String mongoField = mapFieldName(fieldName);

            if (desc) {
                sorts.add(descending(mongoField));
            } else {
                sorts.add(ascending(mongoField));
            }
        }

        return sorts.isEmpty() ? ascending("_id") : orderBy(sorts);
    }

    private String mapFieldName(String fieldName) {
        if (fieldName.equals("id")) {
            return "_id";
        }
        return fieldName;
    }

    public boolean deleteById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            MongoCollection<Document> collection = getCollection();
            return collection.deleteOne(eq("_id", objectId)).getDeletedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<City> findCityWithMinName() {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find()
                .sort(ascending("name"))
                .limit(1)
                .first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public Optional<City> findCityWithMaxClimate() {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find()
                .sort(descending("climate"))
                .limit(1)
                .first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public Optional<City> findFirstByMetersLessThan(Integer meters) {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find(lt("metersAboveSeaLevel", meters))
                .limit(1)
                .first();
        return Optional.ofNullable(doc).map(this::documentToCity);
    }

    public long count() {
        return getCollection().countDocuments();
    }

    private Document cityToDocument(City city) {
        Document doc = new Document();

        if (city.getId() != null && !city.getId().isEmpty()) {
            doc.append("_id", new ObjectId(city.getId()));
        }

        doc.append("name", city.getName())
                .append("area", city.getArea())
                .append("population", city.getPopulation())
                .append("creationDate", city.getCreationDate())
                .append("climate", city.getClimate() != null ? city.getClimate().name() : null)
                .append("capital", city.getCapital())
                .append("agglomeration", city.getAgglomeration())
                .append("metersAboveSeaLevel", city.getMetersAboveSeaLevel());

        if (city.getCoordinates() != null) {
            Document coordDoc = new Document()
                    .append("x", city.getCoordinates().getX())
                    .append("y", city.getCoordinates().getY());
            doc.append("coordinates", coordDoc);
        }

        if (city.getGovernor() != null) {
            Document govDoc = new Document()
                    .append("height", city.getGovernor().getHeight());
            if (city.getGovernor().getBirthday() != null) {
                govDoc.append("birthday", city.getGovernor().getBirthday());
            }
            doc.append("governor", govDoc);
        }

        return doc;
    }

    private City documentToCity(Document doc) {
        City city = new City();

        city.setId(doc.getObjectId("_id").toString());
        city.setName(doc.getString("name"));
        city.setArea(doc.getInteger("area"));
        city.setPopulation(doc.getInteger("population"));
        city.setCreationDate(doc.get("creationDate", java.time.LocalDate.class));

        String climateStr = doc.getString("climate");
        if (climateStr != null) {
            city.setClimate(Climate.valueOf(climateStr));
        }

        city.setCapital(doc.getBoolean("capital"));
        city.setAgglomeration(Float.valueOf(doc.getLong("agglomeration")));
        city.setMetersAboveSeaLevel(doc.getInteger("metersAboveSeaLevel"));

        Document coordDoc = doc.get("coordinates", Document.class);
        if (coordDoc != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setX(coordDoc.getDouble("x"));
            coordinates.setY(coordDoc.getDouble("y"));
            city.setCoordinates(coordinates);
        }

        Document govDoc = doc.get("governor", Document.class);
        if (govDoc != null) {
            Human governor = new Human();
            governor.setHeight(govDoc.getDouble("height"));
            if (govDoc.containsKey("birthday")) {
                governor.setBirthday(govDoc.get("birthday", java.time.LocalDate.class));
            }
            city.setGovernor(governor);
        }

        return city;
    }
}