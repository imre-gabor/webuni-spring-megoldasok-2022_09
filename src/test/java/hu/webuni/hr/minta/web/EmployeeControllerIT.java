package hu.webuni.hr.minta.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import hu.webuni.hr.minta.dto.EmployeeDto;
import hu.webuni.hr.minta.dto.LoginDto;
import hu.webuni.hr.minta.model.Employee;
import hu.webuni.hr.minta.repository.EmployeeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureWebTestClient(timeout = "1000000")
public class EmployeeControllerIT {

	private static final String PASS = "pass";

	private static final String TESTUSER = "testuser";

	private static final String BASE_URI = "/api/employees";

	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	String jwtToken;
	
	@BeforeEach
	public void init() {
		
		Optional<Employee> optionalTestUser = employeeRepository.findByUsername(TESTUSER);
		if(optionalTestUser.isEmpty()) {
			Employee testuser = new Employee(null, "ssdf", 200000, LocalDateTime.now());
			
			testuser.setUsername(TESTUSER);
			testuser.setPassword(passwordEncoder.encode(PASS));
			employeeRepository.save(testuser);
		}
		
		
		LoginDto body = new LoginDto();
		
		body.setUsername(TESTUSER);
		body.setPassword(PASS);
		
		this.jwtToken = webTestClient.post()
			.uri("/api/login")
			.bodyValue(body)
			.exchange()
			.expectBody(String.class)
			.returnResult().getResponseBody();
		
	}

	
	@Test
	void testThatNewValidEmployeeCanBeSaved() throws Exception {
		List<EmployeeDto> employeesBefore = getAllEmployees();

		EmployeeDto newEmployee = new EmployeeDto(0L, "ABC", "student", 200000, LocalDateTime.of(2019, 01, 01, 8, 0, 0));
		
		saveEmployee(newEmployee)
		.expectStatus()
		.isOk();

		List<EmployeeDto> employeesAfter = getAllEmployees();

		assertThat(employeesAfter.size()).isEqualTo(employeesBefore.size() + 1);
		assertThat(employeesAfter.get(employeesAfter.size()-1))
			.usingRecursiveComparison()
			.ignoringFields("id")
			.isEqualTo(newEmployee);
	}
	
	@Test
	void testThatNewInvalidEmployeeCannotBeSaved() throws Exception {
		List<EmployeeDto> employeesBefore = getAllEmployees();

		EmployeeDto newEmployee = newInvalidEmployee();
		saveEmployee(newEmployee)
		.expectStatus()
		.isBadRequest();

		List<EmployeeDto> employeesAfter = getAllEmployees();

		assertThat(employeesAfter).hasSameSizeAs(employeesBefore);
	}

	private EmployeeDto newInvalidEmployee() {
		return new EmployeeDto(0L, "", "student", 200000, LocalDateTime.of(2019, 01, 01, 8, 0, 0));
	}
	
	@Test
	void testThatEmployeeCanBeUpdatedWithValidFields() throws Exception {

		EmployeeDto newEmployee = new EmployeeDto(0L, "ABC", "student", 200000, LocalDateTime.of(2019, 01, 01, 8, 0, 0));
		EmployeeDto savedEmployee = saveEmployee(newEmployee)
				.expectStatus().isOk()
				.expectBody(EmployeeDto.class)
				.returnResult()
				.getResponseBody();
		
		List<EmployeeDto> employeesBefore = getAllEmployees();
		savedEmployee.setName("modified");
		modifyEmployee(savedEmployee)
		.expectStatus()
		.isOk();

		List<EmployeeDto> employeesAfter = getAllEmployees();

		assertThat(employeesAfter).hasSameSizeAs(employeesBefore);
		assertThat(employeesAfter.get(employeesAfter.size()-1))
			.usingRecursiveComparison()
			.isEqualTo(savedEmployee);
	}
	
	@Test
	void testThatEmployeeCannotBeUpdatedWithInvalidFields() throws Exception {
		EmployeeDto newEmployee = new EmployeeDto(0L, "ABC", "student", 200000, LocalDateTime.of(2019, 01, 01, 8, 0, 0));
		EmployeeDto savedEmployee = saveEmployee(newEmployee)
				.expectStatus().isOk()
				.expectBody(EmployeeDto.class)
				.returnResult()
				.getResponseBody();
		
		List<EmployeeDto> employeesBefore = getAllEmployees();
		EmployeeDto invalidEmployee = newInvalidEmployee();
		invalidEmployee.setId(savedEmployee.getId());
		modifyEmployee(invalidEmployee)
		.expectStatus()
		.isBadRequest();

		List<EmployeeDto> employeesAfter = getAllEmployees();

		assertThat(employeesAfter).hasSameSizeAs(employeesBefore);
		assertThat(employeesAfter.get(employeesAfter.size()-1))
			.usingRecursiveComparison()
			.isEqualTo(savedEmployee);
	}
	
	private ResponseSpec modifyEmployee(EmployeeDto newEmployee) {
		String path = BASE_URI + "/" + newEmployee.getId();
		return webTestClient
				.put()
				.uri(path)
				.headers(headers -> headers.setBearerAuth(jwtToken))
				.bodyValue(newEmployee)
				.exchange();
	}
	
	private ResponseSpec saveEmployee(EmployeeDto newEmployee) {
		return webTestClient
				.post()
				.uri(BASE_URI)
				.headers(headers -> headers.setBearerAuth(jwtToken))
				.bodyValue(newEmployee)
				.exchange();
	}

	private List<EmployeeDto> getAllEmployees() {
		List<EmployeeDto> responseList = webTestClient
				.get()
				.uri(BASE_URI)
				.headers(headers -> headers.setBearerAuth(jwtToken))
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(EmployeeDto.class)
				.returnResult()
				.getResponseBody();
		Collections.sort(responseList, Comparator.comparing(EmployeeDto::getId));
		return responseList;
	}

}
