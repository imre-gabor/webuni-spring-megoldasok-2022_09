package hu.webuni.hr.minta.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import hu.webuni.hr.minta.model.Employee;
import hu.webuni.hr.minta.repository.EmployeeRepository;
import hu.webuni.hr.minta.repository.PositionDetailsByCompanyRepository;
import hu.webuni.hr.minta.repository.PositionRepository;

@Service
public class SalaryService {

	private EmployeeService employeeService;
	private PositionRepository positionRepository;
	private PositionDetailsByCompanyRepository positionDetailsByCompanyRepository;
	private EmployeeRepository employeeRepository;
	
	public SalaryService(EmployeeService employeeService, PositionRepository positionRepository,
			PositionDetailsByCompanyRepository positionDetailsByCompanyRepository,
			EmployeeRepository employeeRepository) {
		this.employeeService = employeeService;
		this.positionRepository = positionRepository;
		this.positionDetailsByCompanyRepository = positionDetailsByCompanyRepository;
		this.employeeRepository = employeeRepository;
	}

	public void setNewSalary(Employee employee) {
		int newSalary = employee.getSalary() * (100 + employeeService.getPayRaisePercent(employee)) / 100;
		employee.setSalary(newSalary);
	}
	
	@Transactional
	public void raiseMinSalary(String positionName, int minSalary) {
		//1. megoldás: kevésbé hatékony, mert egyesével fut majd UPDATE a releváns employee-kra
//		positionRepository.findByName(positionName)
//		.forEach(p -> {
//			p.setMinSalary(minSalary);
//			p.getEmployees().forEach(e ->{
//				if(e.getSalary() < minSalary)
//					e.setSalary(minSalary);
//			});
//		});
	}
	
	@Transactional
	public void raiseMinSalary(long companyId, String position, int minSalary) {
		
		positionDetailsByCompanyRepository.findByPositionNameAndCompanyId(position, companyId)
		.forEach(pd -> pd.setMinSalary(minSalary));
		
		employeeRepository.updateSalaries(companyId, position, minSalary);		
	}

}
