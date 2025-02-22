### PetStore API Test Summary

This project includes a set of automated tests for the PetStore API using JUnit 5 and RestAssured. The tests cover the full lifecycle of a pet entity in the system, including creation, retrieval, update, and deletion. All test cases are executed in a specific order to ensure consistency and reliability.

<u>**Test Cases:**</u>

1. Create a new pet: A new pet is added with randomly generated attributes.

2. Retrieve the created pet: The pet is fetched using its ID to verify the creation.

3. Update pet information: The pet's name and status are updated.

4. Retrieve the updated pet: The updated pet details are validated.

5. Delete the pet: The pet is removed, and a subsequent retrieval confirms its deletion.

The test framework integrates with Allure for test reporting and uses random values to generate test data dynamically. The base URI for API requests is https://petstore.swagger.io/v2.

<u>**Verification Steps**</u>:

1. Clone the repository.

2. Import dependencies.

3. Run `mvn test` to execute the tests.

4. Run `allure serve target/allure-results` to view the Allure report.