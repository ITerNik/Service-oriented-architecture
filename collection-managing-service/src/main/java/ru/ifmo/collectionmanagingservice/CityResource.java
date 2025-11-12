package ru.ifmo.collectionmanagingservice;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import ru.ifmo.collectionmanagingservice.dto.ErrorResponse;
import ru.ifmo.collectionmanagingservice.service.CityService;
import ru.ifmo.collectionmanagingservice.model.City;

import java.util.List;

@Path("/cities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CityResource {
    @Inject
    private CityService cityService;

    @GET
    public Response getCities(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") String sort,
            @Context UriInfo uriInfo) {

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        List<City> cities = cityService.getCities(page, size, sort, queryParams);
        return Response.ok(cities).build();
    }

    @POST
    public Response createCity(@Valid City city, @Context UriInfo uriInfo) {
        try {
            City created = cityService.createCity(city);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(created.getId());
            return Response.created(builder.build())
                    .entity(created)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(422)
                    .entity(new ErrorResponse(422, e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCityById(@PathParam("id") String id) {
        City city = cityService.getCityById(id);
        if (city == null) {
            return Response.status(404)
                    .entity(new ErrorResponse(404, "City not found"))
                    .build();
        }
        return Response.ok(city).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCity(@PathParam("id") String id, @Valid City city) {
        try {
            City updated = cityService.updateCity(id, city);
            if (updated == null) {
                return Response.status(404)
                        .entity(new ErrorResponse(404, "City not found"))
                        .build();
            }
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(422)
                    .entity(new ErrorResponse(422, e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCity(@PathParam("id") String id) {
        boolean deleted = cityService.deleteCity(id);
        if (!deleted) {
            return Response.status(404)
                    .entity(new ErrorResponse(404, "City not found"))
                    .build();
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("/special/less-than-meters/")
    public Response deleteCityByMeters(
            @QueryParam("less-than-meters") Integer meters) {

        if (meters == null) {
            return Response.status(400)
                    .entity(new ErrorResponse(400, "Parameter required"))
                    .build();
        }

        boolean deleted = cityService.deleteCityByMeters(meters);
        if (!deleted) {
            return Response.status(404)
                    .entity(new ErrorResponse(404, "No city found"))
                    .build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/special/min-name")
    public Response getCityWithMinName() {
        City city = cityService.getCityWithMinName();
        if (city == null) {
            return Response.status(404)
                    .entity(new ErrorResponse(404, "No cities found"))
                    .build();
        }
        return Response.ok(city).build();
    }

    @GET
    @Path("/special/max-climate")
    public Response getCityWithMaxClimate() {
        City city = cityService.getCityWithMaxClimate();
        if (city == null) {
            return Response.status(404)
                    .entity(new ErrorResponse(404, "No cities found"))
                    .build();
        }
        return Response.ok(city).build();
    }
}
