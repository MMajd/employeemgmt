package com.rawafed.employeemgmt.domain.event.listener;

import com.rawafed.employeemgmt.domain.EmailValidationOnErrorResponse;
import com.rawafed.employeemgmt.domain.EmailValidationResponse;
import com.rawafed.employeemgmt.domain.event.EmployeeEmailValidationEvent;
import com.rawafed.employeemgmt.domain.event.EmployeeRegistrationEvent;
import com.rawafed.employeemgmt.domain.event.EmployeeVerifiedEvent;
import com.rawafed.employeemgmt.domain.presistent.model.EmployeeModel;
import com.rawafed.employeemgmt.domain.presistent.repo.EventRepository;
import com.rawafed.employeemgmt.service.IEmailValidationService;
import com.rawafed.employeemgmt.service.IEmployeeService;
import com.rawafed.employeemgmt.utils.mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static com.rawafed.employeemgmt.domain.event.listener.OperationTestUtil.EMPLOYEE_MODEL;
import static com.rawafed.employeemgmt.domain.event.listener.OperationTestUtil.TEST_EMPLOYEE_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmployeeEmailValidationListenerTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Mock
    private IEmailValidationService validationService;

    @Mock
    private IEmployeeService employeeService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EmployeeEmailValidationListener listener;

    @BeforeEach
    void setup() {
        when(employeeService.find(TEST_EMPLOYEE_EMAIL))
                .thenReturn(employeeMapper.modelToDto(EMPLOYEE_MODEL));
    }

    @Test
    public void testRegistrationListenerSuccessfulValidation() {
        EmployeeRegistrationEvent event = EmployeeRegistrationEvent.builder().email(TEST_EMPLOYEE_EMAIL).build();
        EmailValidationResponse response = EmailValidationResponse.builder()
                .email(TEST_EMPLOYEE_EMAIL)
                .valid(true)
                .build();

        // Mocking the validate method with explicit Consumer types
        doAnswer(invocation -> {
            Consumer<EmailValidationResponse> onSuccess = invocation.getArgument(1);
            Consumer<EmailValidationOnErrorResponse> onError = invocation.getArgument(2);
            onSuccess.accept(response);
            return null;
        }).when(validationService).validate(eq(TEST_EMPLOYEE_EMAIL), any(Consumer.class), any(Consumer.class));

        // Act
        listener.registrationListener(event);

        // Assert
        verify(validationService, times(1)).validate(eq(TEST_EMPLOYEE_EMAIL), any(), any());
        verify(employeeService, times(1)).internalUpdate(any());
        verify(eventRepository, times(1)).deleteByEmployeeEmailAndEventType(eq(TEST_EMPLOYEE_EMAIL), eq(EmployeeEmailValidationEvent.class.getSimpleName()));
        verify(eventPublisher, times(1)).publishEvent(any(EmployeeVerifiedEvent.class));
    }


    @Test
    public void testRegistrationListenerFailedValidation() {
        EmployeeRegistrationEvent event = EmployeeRegistrationEvent.builder().email(TEST_EMPLOYEE_EMAIL).build();
        EmailValidationOnErrorResponse response = EmailValidationOnErrorResponse.builder()
                .email(TEST_EMPLOYEE_EMAIL)
                .build();

        doAnswer(invocation -> {
            Consumer<EmailValidationResponse> onSuccess = invocation.getArgument(1);
            Consumer<EmailValidationOnErrorResponse> onError = invocation.getArgument(2);
            onError.accept(response);
            return null;
        }).when(validationService).validate(eq(TEST_EMPLOYEE_EMAIL), any(Consumer.class), any(Consumer.class));

        listener.registrationListener(event);

        verify(validationService, times(1)).validate(eq(TEST_EMPLOYEE_EMAIL), any(Consumer.class), any(Consumer.class));
        verify(employeeService, times(0)).internalUpdate(any());
        verify(eventRepository, times(0)).deleteByEmployeeEmailAndEventType(eq(TEST_EMPLOYEE_EMAIL), eq(EmployeeEmailValidationEvent.class.getSimpleName()));
        verify(eventPublisher, times(0)).publishEvent(any(EmployeeVerifiedEvent.class));
    }
}


final class OperationTestUtil {
    public static final EmployeeModel EMPLOYEE_MODEL = new EmployeeModel();
    public static final String TEST_EMPLOYEE_EMAIL = "test@mail.com";

    static {
        EMPLOYEE_MODEL.setEmail("test@mail.com");
        EMPLOYEE_MODEL.setFirstName("firstName");
        EMPLOYEE_MODEL.setLastName("lastName");
        EMPLOYEE_MODEL.setDepartment("d2");
        EMPLOYEE_MODEL.setSalary(BigDecimal.ONE);
        EMPLOYEE_MODEL.setValidDepartment(Boolean.TRUE);
        EMPLOYEE_MODEL.setValidEmail(Boolean.TRUE);

    }
}