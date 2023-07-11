package controller;

import dataModel.Pet;
import dataModel.Status;
import dataModel.Tag;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import responseDataModel.PetPojoResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static utils.Properties.baseUri;



public class PetsController {
    public static String PET_ENDPOINT = baseUri + "/pet";
    private RequestSpecification requestSpecification;
    PrintStream captor;

    public PetsController() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(baseUri);
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecBuilder.log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();
    }
    public Response addNewPet(Pet pet, PrintStream captor) {
        return given(requestSpecification).filter(new RequestLoggingFilter(captor))
                .body(pet)
                .post(PET_ENDPOINT);

    }

    public List<Pet> getPetsByStatus(Status status,PrintStream captor) {
        return given(requestSpecification).filter(new RequestLoggingFilter(captor))
                .queryParam("status", Status.available.toString())
                .get(PET_ENDPOINT + "/findByStatus")
                .then().log().all()
                .extract().body()
                .jsonPath().getList("pets", Pet.class);

    }
    public Response findPetsByStatus(Status status,PrintStream captor) {
        return given(requestSpecification).filter(new RequestLoggingFilter(captor))
                .queryParam("status")
                .get(PET_ENDPOINT+"/findByStatus");
    }
    public List<Pet> getPetsByTags(Response response) {
        return response.jsonPath().getList("pets", Pet.class);

    }
    public Response findPetsByTags(String tags,PrintStream captor) {
        return given(requestSpecification).filter(new RequestLoggingFilter(captor))
                .queryParam("tags", Arrays.asList(tags.split(",")))
                .get(PET_ENDPOINT+"/findByTags");
    }

    public Response updatePet(Pet pet,PrintStream captor) {
        return given(requestSpecification).filter(new RequestLoggingFilter(captor))
                .body(pet)
                .put(PET_ENDPOINT);
    }
    public PetPojoResponse findPet(Pet pet) {
        return given(requestSpecification)
                .pathParam("petId", pet.getId())
                .get(PET_ENDPOINT + "/{petId}").as(PetPojoResponse.class);
    }

    public void storePetIdInJsonFile(int petId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("petId", petId);
            FileUtils.writeStringToFile(new File("petId.json"), jsonObject.toString(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
