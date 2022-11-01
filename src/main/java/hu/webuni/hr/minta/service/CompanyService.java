package hu.webuni.hr.minta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.webuni.hr.minta.model.Company;
import hu.webuni.hr.minta.model.Employee;
import hu.webuni.hr.minta.repository.CompanyRepository;
import hu.webuni.hr.minta.repository.EmployeeRepository;

@Service
public class CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	

	public Company save(Company company) {		
		return companyRepository.save(company);
	}

	public Company update(Company company) {
		if(!companyRepository.existsById(company.getId()))
			return null;
		return companyRepository.save(company);
	}

	public List<Company> findAll() {
		return companyRepository.findAll();
	}

	public Optional<Company> findById(long id) {
		return companyRepository.findById(id);
	}

	public void delete(long id) {
		companyRepository.deleteById(id);
	}
	
	public Company addEmployee(long companyId, Employee employee) {
		Company company = companyRepository.findById(companyId).get();
		company.addEmployee(employee);
		employeeRepository.save(employee);
		return company;
	}
	
	public Company deleteEmployee(long companyId, long employeeId) {
		Company company = companyRepository.findById(companyId).get();
		Employee employee = employeeRepository.findById(employeeId).get();
		employee.setCompany(null);
		company.getEmployees().remove(employee);
		employeeRepository.save(employee);
		return company;
	}
	
	public Company replaceEmployees(long companyId, List<Employee> employees) {
		Company company = companyRepository.findById(companyId).get();
		
		company.getEmployees().forEach(emp -> emp.setCompany(null));
		company.getEmployees().clear();

		employees.forEach(emp -> {
			Employee savedEmployee = employeeRepository.save(emp);
			company.addEmployee(savedEmployee);			
		});
		return company;
	}
	
}
