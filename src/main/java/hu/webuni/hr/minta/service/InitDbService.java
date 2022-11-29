package hu.webuni.hr.minta.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.webuni.hr.minta.model.Company;
import hu.webuni.hr.minta.model.Employee;
import hu.webuni.hr.minta.model.Position;
import hu.webuni.hr.minta.model.PositionDetailsByCompany;
import hu.webuni.hr.minta.model.Qualification;
import hu.webuni.hr.minta.repository.CompanyRepository;
import hu.webuni.hr.minta.repository.EmployeeRepository;
import hu.webuni.hr.minta.repository.PositionDetailsByCompanyRepository;
import hu.webuni.hr.minta.repository.PositionRepository;

@Service
public class InitDbService {

	@Autowired
	PositionRepository positionRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	PositionDetailsByCompanyRepository positionDetailsByCompanyRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Transactional
	public void initDb() {
		
		Position developer = positionRepository.save(new Position("fejlesztő", Qualification.UNIVERSITY));
		Position tester = positionRepository.save(new Position("tesztelő", Qualification.HIGH_SCHOOL));
		
		Employee newEmployee1 = employeeRepository.save(new Employee(null, "ssdf", 200000, LocalDateTime.now()));
		newEmployee1.setPosition(developer);
		newEmployee1.setUsername("user1");
		newEmployee1.setPassword(passwordEncoder.encode("pass"));
		
		Employee newEmployee2 = employeeRepository.save(new Employee(null, "t35", 200000, LocalDateTime.now()));
		newEmployee2.setPosition(tester);
		newEmployee2.setUsername("user2");
		newEmployee2.setPassword(passwordEncoder.encode("pass"));
		
		Employee newEmployee3 = employeeRepository.save(new Employee(null, "tester2", /*"tester",*/ 200000, LocalDateTime.now()));
		newEmployee3.setPosition(tester);
		newEmployee3.setUsername("user3");
		newEmployee3.setPassword(passwordEncoder.encode("pass"));
		newEmployee3.setManager(newEmployee2);
		
		Company newCompany = companyRepository.save(new Company(null, 10, "sdfsd", "", null));
		newCompany.addEmployee(newEmployee2);
		newCompany.addEmployee(newEmployee1);
		newCompany.addEmployee(newEmployee3);
		
		PositionDetailsByCompany pd = new PositionDetailsByCompany();
		pd.setCompany(newCompany);
		pd.setMinSalary(250000);
		pd.setPosition(developer);
		positionDetailsByCompanyRepository.save(pd);
		
		PositionDetailsByCompany pd2 = new PositionDetailsByCompany();
		pd2.setCompany(newCompany);
		pd2.setMinSalary(200000);
		pd2.setPosition(tester);
		positionDetailsByCompanyRepository.save(pd2);
	}
}
