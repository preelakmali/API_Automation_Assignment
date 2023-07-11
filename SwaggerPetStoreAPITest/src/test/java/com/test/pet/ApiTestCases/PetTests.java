package com.test.pet.ApiTestCases;

import controller.PetsController;
import com.test.pet.reports.PetDataProvider;
import dataModel.Category;
import dataModel.Pet;
import dataModel.Status;
import dataModel.Tag;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import responseDataModel.PetPojoResponse;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Listeners(com.test.pet.listeners.ListenerClass.class)
public class PetTests extends  BaseTest{

    private static final String PHOTO_URL = "https://example.com/image.jpg";
    PetsController petsController;
    Pet pet = new Pet.Builder()
            .withId(RandomStringUtils.randomNumeric(5))
            .withName("My pet")
            .withPhotoUrls(Collections.singletonList(PHOTO_URL))
            .withStatus(Status.available)
            .withTags(Collections.singletonList(new Tag(1, "golden-retriever")))
            .inCategory(new Category(1, "dogs")).build();

    @BeforeClass
    public void beforeClass() {
        petsController = new PetsController();
    }

    @Test(description = "Add a new pet to the store" ,priority = 1)
    public void addNewPet() {
        Response response = petsController.addNewPet(pet, captor);
        PetPojoResponse petResponse = response.as(PetPojoResponse.class);
        Assert.assertEquals(petResponse.getName(), pet.getName(),"Pet name shoud be equal to provided Pet name");
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code to be 200");
        Assert.assertTrue(response.getTime() < 5000, "Response time is less than 5000 milliseconds");
        Assert.assertNotNull(petResponse.getId(), "Pet ID should not be null");
        PetPojoResponse findPetResponse = petsController.findPet(pet);
        Assert.assertEquals(findPetResponse.getName(), pet.getName());
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint());
    }


    @Test(dataProvider = "petData",dataProviderClass = PetDataProvider.class,description = "Create at least 4 pets with different tags & status" ,priority = 2)
    public void addNewFourPet(String name, List<Tag> tags, Status status) {
        pet.setName(name);
        pet.setTags(tags);
        pet.setStatus(status);
        Response response = petsController.addNewPet(pet, captor);
        PetPojoResponse petResponse = response.as(PetPojoResponse.class);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code to be 200");
    }

    @Test(description = "Store the id of the new pet in a json file",priority = 3)
    public void testStoreTheIDInAJSON() {
        pet.setName("Max");
        pet.setStatus(Status.sold);
        Response response = petsController.addNewPet(pet, captor);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code to be 200");
        int petId = response.jsonPath().getInt("id");
        petsController.storePetIdInJsonFile(petId);
        File jsonFile = new File("petId.json");
        Assert.assertTrue(jsonFile.exists(), "JSON file should exist");
        Assert.assertTrue(jsonFile.length() > 0, "JSON file should not be empty");
    }


    @Test(description = "Update an existing pet" ,priority = 4)
    public void updatePet() {
        pet.setName("My Updated pet");
        pet.setStatus(Status.pending);
        Response response = petsController.updatePet(pet, captor);
        PetPojoResponse petResponse = response.as(PetPojoResponse.class);
        Assert.assertEquals(petResponse.getName(), pet.getName(),"Pet name should be equal to updated Pet name");
        Assert.assertEquals(petResponse.getStatus(), pet.getStatus().toString() ,"Pet status should be equal to updated Pet status");
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code to be 200");
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint());
    }

    @Test(description ="Find Pets by status ",priority = 5)
    public void findPetsByStatus() {
        List<Pet> pets = petsController.getPetsByStatus(Status.available,captor);
        Response response = petsController.findPetsByStatus(Status.available,captor);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code to be 200");
        String responseBody = response.getBody().asString();
        Assert.assertNotNull(pets, "Pets should not be null");
        Assert.assertFalse(pets.isEmpty(), "No pets found with the specified status");
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint());
    }

    @Test(description ="Find Pets by tag",priority = 6)
    public void findPetsByTag() {

        String tags = "tag1,tag2,tag3";
        Response response = petsController.findPetsByTags(tags,captor);
        List<Pet> pets = petsController.getPetsByTags(response);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code to be 200");
        String responseBody = response.getBody().asString();
        Assert.assertNotNull(pets, "Pets should not be null");
        Assert.assertFalse(pets.isEmpty(), "No pets found with the specified tags");
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint());
    }


}
