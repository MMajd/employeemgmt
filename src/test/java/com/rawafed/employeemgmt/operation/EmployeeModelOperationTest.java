package com.rawafed.employeemgmt.operation;

import com.rawafed.employeemgmt.api.model.EmployeeCreateReq;
import com.rawafed.employeemgmt.api.model.EmployeeUpdateReq;
import com.rawafed.employeemgmt.domain.exception.common.ErrorEnum;
import com.rawafed.employeemgmt.domain.presistent.model.EmployeeModel;
import com.rawafed.employeemgmt.domain.presistent.repo.EmployeeRepository;
import com.rawafed.employeemgmt.service.impl.EmployeeService;
import com.rawafed.employeemgmt.service.impl.EventPublishingService;
import com.rawafed.employeemgmt.service.impl.MailService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static com.rawafed.employeemgmt.operation.OperationTestUtil.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;


@SpringBootTest(webEnvironment = RANDOM_PORT)
public class EmployeeModelOperationTest {
    @Mock
    private EventPublishingService applicationEventPublisher;

    @Mock
    private MailService mailService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    @InjectMocks
    private EmployeeService employeeService;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
        employeeRepository.save(EMPLOYEE_MODEL);
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void successWhenV1CreateEmployeeGivenValidRequest() {
        given()
                .contentType(ContentType.JSON)
                .body(EMPLOYEE_CREATE_REQ)
                .when()
                .post("/v1/employees")
                .then()
                .statusCode(ACCEPTED.value());

        assertThat(employeeService.find(DUMMY_EMPLOYEE_EMAIL)).isNotNull();
    }

    @Test
    void failWhenV1CreateEmployeeGivenInvalidRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/v1/employees")
                .then()
                .statusCode(UNPROCESSABLE_ENTITY.value())
                .body("code", equalTo(ErrorEnum.INVALID_REQUEST_ERR.getCode()))
                .body("message", equalTo(ErrorEnum.INVALID_REQUEST_ERR.getMessage()))
        ;
    }

    @Test
    void failWhenV1CreateEmployeeGivenDuplicateEmail() {
        given()
                .contentType(ContentType.JSON)
                .body(EMPLOYEE_DUPLICATE_CREATE_REQ)
                .when()
                .post("/v1/employees")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("code", equalTo(ErrorEnum.DUPLICATE_KEY_ERR.getCode()))
                .body("message", equalTo(ErrorEnum.DUPLICATE_KEY_ERR.getMessage()))
        ;

    }

    @Test
    void successWhenV1GetEmployeeGiveValidRequest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/employees/{email}", TEST_EMPLOYEE_EMAIL)
                .then()
                .statusCode(OK.value())
                .body("email", notNullValue());
    }

    @Test
    void failWhenV1GetEmployeeGiveInvalidRequest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/employees/{email}", "e23@xmail.cm")
                .then()
                .statusCode(NOT_FOUND.value())
                .body("code", equalTo(ErrorEnum.RESOURCE_NOT_FOUND_ERR.getCode()))
                .body("message", equalTo(ErrorEnum.RESOURCE_NOT_FOUND_ERR.getMessage()));
    }

    @Test
    void successWhenV1ListEmployeesGivenValidRequest() {
    }

    @Test
    void successWhenV1UpdateEmployeeGivenValidRequest() {
        given()
                .contentType(ContentType.JSON)
                .body(EMPLOYEE_UPDATE_REQ)
                .when()
                .put("/v1/employees/{email}", TEST_EMPLOYEE_EMAIL)
                .then()
                .statusCode(ACCEPTED.value());
        EmployeeModel employee = employeeRepository.findByEmail(TEST_EMPLOYEE_EMAIL).get();
        assertThat(employee.getFirstName()).isEqualTo("f2");
        assertThat(employee.getSalary()).isEqualTo(EMPLOYEE_SALARY);
    }

    @Test
    void failWhenV1UpdateEmployeeGivenInvalidRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .put("/v1/employees/{email}", TEST_EMPLOYEE_EMAIL)
                .then()
                .statusCode(UNPROCESSABLE_ENTITY.value())
                .body("code", equalTo(ErrorEnum.INVALID_REQUEST_ERR.getCode()))
                .body("message", equalTo(ErrorEnum.INVALID_REQUEST_ERR.getMessage()));
    }
}


final class OperationTestUtil {
    public static final EmployeeModel EMPLOYEE_MODEL = new EmployeeModel();

    public static final String TEST_EMPLOYEE_EMAIL = "test@mail.com";
    public static final String DUMMY_EMPLOYEE_EMAIL = "dummy@mail.com";
    public static final String EMPLOYEE_SALARY = "100000.00";

    public static final EmployeeCreateReq EMPLOYEE_DUPLICATE_CREATE_REQ = new EmployeeCreateReq();
    public static final EmployeeCreateReq EMPLOYEE_CREATE_REQ = new EmployeeCreateReq();
    public static final EmployeeUpdateReq EMPLOYEE_UPDATE_REQ = new EmployeeUpdateReq();

    static {
        EMPLOYEE_MODEL.setEmail("test@mail.com");
        EMPLOYEE_MODEL.setFirstName("firstName");
        EMPLOYEE_MODEL.setLastName("lastName");
        EMPLOYEE_MODEL.setDepartment("d2");
        EMPLOYEE_MODEL.setSalary(BigDecimal.ONE);
        EMPLOYEE_MODEL.setValidDepartment(Boolean.TRUE);
        EMPLOYEE_MODEL.setValidEmail(Boolean.TRUE);

        EMPLOYEE_CREATE_REQ
                .firstName("f1")
                .lastName("l1")
                .department("d1")
                .salary("100000")
                .email(DUMMY_EMPLOYEE_EMAIL);

        EMPLOYEE_UPDATE_REQ
                .firstName("f2")
                .salary("100000");

        EMPLOYEE_DUPLICATE_CREATE_REQ
                .email(TEST_EMPLOYEE_EMAIL)
                .firstName("f1")
                .lastName("l1")
                .department("d1")
                .salary("100000");
    }
}