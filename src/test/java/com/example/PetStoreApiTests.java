package com.example;

import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreApiTests {

    private enum PetStatus {
        AVAILABLE, PENDING, SOLD
    }

    private long petId;
    private long categoryId;
    private String categoryName;
    private String petName;
    private long tagId;
    private String tagName;
    private String petStatus;

    private final Map<String, List<String>> petCategories = Map.of(
            "Cat", List.of("Mirmur", "Whiskers", "Snowball", "Shadow", "Luna"),
            "Dog", List.of("Buddy", "Charlie", "Max", "Bailey", "Rocky")
    );

    private final Map<String, List<String>> petTags = Map.of(
            "No owner", List.of(String.valueOf(PetStatus.AVAILABLE).toLowerCase(),String.valueOf(PetStatus.PENDING).toLowerCase()),
            "Has owner", List.of(String.valueOf(PetStatus.SOLD).toLowerCase())
    );

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.filters(new AllureRestAssured());

        Random random = new Random();
        petId = random.nextInt(1000000);
        categoryId = random.nextInt(1000000);
        tagId = random.nextInt(1000000);

        List<String> categoryKeys = List.copyOf(petCategories.keySet());
        categoryName = categoryKeys.get(random.nextInt(categoryKeys.size()));

        List<String> petNamesForCategory = petCategories.get(categoryName);
        petName = petNamesForCategory.get(random.nextInt(petNamesForCategory.size()));

        List<String> petTagKeys = List.copyOf(petTags.keySet());
        tagName = petTagKeys.get(random.nextInt(petTagKeys.size()));

        List<String> petStatusFromTag = petTags.get(tagName);
        petStatus = petStatusFromTag.get(random.nextInt(petStatusFromTag.size()));
    }

    @Test
    @Order(1)
    @Description("Create a new pet")
    public void testCreatePet() {
        String petJson = """
                {
                  "id": %d,
                  "category": {
                    "id": %d,
                    "name": "%s"
                  },
                  "name": "%s",
                  "photoUrls": [
                    "/img/%s.jpg"
                  ],
                   "tags": [
                      {
                        "id": %d,
                        "name": "%s"
                      }
                   ],
                  "status": "%s"
                }
                """.formatted(petId, categoryId, categoryName, petName, petName.toLowerCase(), tagId, tagName, petStatus.toLowerCase());

        given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when()
                .post("/pet")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("id", equalTo((int) petId))
                .assertThat().body("category.id", equalTo((int) categoryId))
                .assertThat().body("category.name", equalTo(categoryName))
                .assertThat().body("name", equalTo(petName))
                .assertThat().body("photoUrls", notNullValue())
                .assertThat().body("photoUrls", contains("/img/"+ petName.toLowerCase() +".jpg"))
                .assertThat().body("tags[0].id", equalTo((int) tagId))
                .assertThat().body("tags[0].name", equalTo(tagName))
                .assertThat().body("status", equalTo(petStatus.toLowerCase()));
    }

    @Test
    @Order(2)
    @Description("Get the created pet")
    public void testGetPet() {
        given()
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("id", equalTo((int) petId))
                .assertThat().body("category.id", equalTo((int) categoryId))
                .assertThat().body("category.name", equalTo(categoryName))
                .assertThat().body("name", equalTo(petName))
                .assertThat().body("photoUrls", notNullValue())
                .assertThat().body("photoUrls", contains("/img/"+ petName.toLowerCase() +".jpg"))
                .assertThat().body("tags[0].id", equalTo((int) tagId))
                .assertThat().body("tags[0].name", equalTo(tagName))
                .assertThat().body("status", equalTo(petStatus.toLowerCase()));
    }

    @Test
    @Order(3)
    @Description("Update the pet information")
    public void testUpdatePet() {
        String newName = petName + " Updated";
        String newTagName;
        PetStatus newStatus;

        if (tagName.equals("Has owner") && petStatus.equals("sold"))
        {
            newTagName = "No owner";
            newStatus = PetStatus.AVAILABLE;
        }
        else {
            newTagName = "Has owner";
            newStatus = PetStatus.SOLD;
        }

        String updatedPetJson = """
                {
                  "id": %d,
                  "category": {
                    "id": %d,
                    "name": "%s"
                  },
                  "name": "%s",
                  "photoUrls": [
                    "/img/%s.jpg"
                  ],
                   "tags": [
                      {
                        "id": %d,
                        "name": "%s"
                      }
                   ],
                  "status": "%s"
                }
                """.formatted(petId, categoryId, categoryName, newName, petName.toLowerCase(), tagId, newTagName, newStatus.name().toLowerCase());

        given()
                .contentType(ContentType.JSON)
                .body(updatedPetJson)
                .when()
                .put("/pet")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("id", equalTo((int) petId))
                .assertThat().body("category.id", equalTo((int) categoryId))
                .assertThat().body("category.name", equalTo(categoryName))
                .assertThat().body("name", equalTo(newName))
                .assertThat().body("photoUrls", notNullValue())
                .assertThat().body("photoUrls", contains("/img/"+ petName.toLowerCase() +".jpg"))
                .assertThat().body("tags[0].id", equalTo((int) tagId))
                .assertThat().body("tags[0].name", equalTo(newTagName))
                .assertThat().body("status", equalTo(newStatus.name().toLowerCase()));

        petName = newName;
        tagName = newTagName;
        petStatus = newStatus.name().toLowerCase();
    }

    @Test
    @Order(4)
    @Description("Get the updated pet")
    public void testGetUpdatedPet() {
        given()
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("name", equalTo(petName))
                .assertThat().body("tags[0].name", equalTo(tagName))
                .assertThat().body("status", equalTo(petStatus));
    }

    @Test
    @Order(5)
    @Description("Delete the pet")
    public void testDeletePet() {
        given()
                .pathParam("petId", petId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .assertThat().statusCode(200);

        given()
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .assertThat().statusCode(404)
                .assertThat().body("message", equalTo("Pet not found"));
    }
}
