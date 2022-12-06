package hu.webuni.hr.minta.security;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import hu.webuni.hr.minta.config.HrConfigProperties;
import hu.webuni.hr.minta.config.HrConfigProperties.JwtData;
import hu.webuni.hr.minta.model.Employee;
import hu.webuni.hr.minta.model.HrUser;

@Service
public class JwtService {

	private static final String USERNAME = "username";
	private static final String MANAGED_EMPLOYEES = "managedEmployees";
	private static final String MANAGER = "manager";
	private static final String ID = "id";
	private static final String FULLNAME = "fullname";
	private static final String AUTH = "auth";

	private Algorithm alg;
	private String issuer;
	
	@Autowired
	private HrConfigProperties hrConfig;
	
	
	@PostConstruct
	public void init() {
		JwtData jwtData = hrConfig.getJwtData();
		this.issuer =  jwtData.getIssuer();
		
		try {
			this.alg  = (Algorithm) Algorithm.class.getMethod(jwtData.getAlg(), String.class)
			.invoke(Algorithm.class, jwtData.getSecret());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public String creatJwtToken(UserDetails principal) {
		
		Employee employee = ((HrUser)principal).getEmployee();
		Employee manager = employee.getManager();
		List<Employee> managedEmployees = employee.getManagedEmployees();
		
		
		Builder jwtBuilder = JWT.create()
			.withSubject(principal.getUsername())
			.withArrayClaim(AUTH, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
			.withClaim(FULLNAME, employee.getName())
			.withClaim(ID, employee.getEmployeeId());
		
		if(manager != null)
			jwtBuilder
				.withClaim(MANAGER, createMapFromEmployee(manager));
		
		if(managedEmployees != null && !managedEmployees.isEmpty())
			jwtBuilder
			.withClaim(MANAGED_EMPLOYEES, 
				managedEmployees.stream()
				.map(this::createMapFromEmployee)
				.collect(Collectors.toList())				
			);
			
				
		return jwtBuilder
			.withExpiresAt(new Date(System.currentTimeMillis() + hrConfig.getJwtData().getDuration().toMillis()))
			.withIssuer(issuer)
			.sign(alg);
		
	}

	private Map<String, Object> createMapFromEmployee(Employee employee) {
		
		return Map.of(
				ID, employee.getEmployeeId(),
				USERNAME, employee.getUsername()
				);
	}

	public UserDetails parseJwt(String jwtToken) {
		
		DecodedJWT decodedJwt = JWT.require(alg)
			.withIssuer(issuer)
			.build()
			.verify(jwtToken);
						
		Employee employee = new Employee();
		employee.setEmployeeId(decodedJwt.getClaim(ID).asLong());
		employee.setUsername(decodedJwt.getSubject());
		employee.setName(decodedJwt.getClaim(FULLNAME).asString());
		
		Claim managerClaim = decodedJwt.getClaim(MANAGER);
		if(managerClaim != null) {
			Map<String, Object> managerData = managerClaim.asMap();
			employee.setManager(parseEmployeeFromMap(managerData));
		}
		
		Claim managedEmployeesClaim = decodedJwt.getClaim(MANAGED_EMPLOYEES);
		if(managedEmployeesClaim != null) {
			employee.setManagedEmployees(new ArrayList<>());
			List<HashMap> managedEmployees = managedEmployeesClaim.asList(HashMap.class);
			if(managedEmployees != null) {
				for (HashMap employeeMap : managedEmployees) {
					Employee managedEmployee = parseEmployeeFromMap(employeeMap);
					if(managedEmployee != null)
						employee.getManagedEmployees().add(managedEmployee);
				}
			}
		}
		
		
		return new HrUser(decodedJwt.getSubject(), "dummy",
				decodedJwt.getClaim(AUTH).asList(String.class)
				.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()), employee); 
		
		
	}

	private Employee parseEmployeeFromMap(Map<String, Object> employeeData) {
		if(employeeData == null)
			return null;
		
		Employee employee = new Employee();
		employee.setEmployeeId(((Integer)employeeData.get(ID)).longValue());
		employee.setUsername((String) employeeData.get(USERNAME));
		
		return employee;
	}

}
