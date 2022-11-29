package hu.webuni.hr.minta.web;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.hr.minta.dto.CompanyDto;
import hu.webuni.hr.minta.dto.EmployeeDto;
import hu.webuni.hr.minta.mapper.CompanyMapper;
import hu.webuni.hr.minta.mapper.EmployeeMapper;
import hu.webuni.hr.minta.model.AverageSalaryByPosition;
import hu.webuni.hr.minta.model.Company;
import hu.webuni.hr.minta.repository.CompanyRepository;
import hu.webuni.hr.minta.service.CompanyService;




@RestController
@RequestMapping("/api/companies")
public class CompanyController {

	@Autowired
	private CompanyService companyService;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private CompanyRepository companyRepository;
	
	
	@GetMapping
	public List<CompanyDto> getCompanies(@RequestParam Optional<Boolean> full){
		List<Company> employees = 
			full.orElse(false)				
			 ? companyRepository.findAllWithEmployees()
			:companyService.findAll();
		return mapCompanies(employees, full);
	}


	private List<CompanyDto> mapCompanies(List<Company> employees, Optional<Boolean> full) {
		if(full.orElse(false))
			return companyMapper.companiesToDtos(employees);
		else
			return companyMapper.companiesToSummaryDtos(employees);
	}
	
	
	//2. alternatív megoldás a full param kezelésére	
//	@GetMapping(params = "full=true")
//	public List<CompanyDto> getCompaniesFull(){			
//		return new ArrayList<>(companies.values());		
//	}
//	
//	@JsonView(Views.BaseData.class)
//	@GetMapping
//	public List<CompanyDto> getCompaniesWithoutEmployees(@RequestParam(required = false) Boolean full){			
//		return new ArrayList<>(companies.values());
//	}
	

	@GetMapping("/{id}")
	public CompanyDto getById(@PathVariable long id, @RequestParam Optional<Boolean> full) {
		Company company = findByIdOrThrow(full, id);
		if (full.orElse(false))
			return companyMapper.companyToDto(company);
		else
			return companyMapper.companyToSummaryDto(company);
	}


	private Company findByIdOrThrow(Optional<Boolean> full, long id) {
		
		Optional<Company> companyOpt = 
				full.orElse(false)
				? companyRepository.findByIdWithEmployees(id)
				:companyService.findById(id);
		
		
		return companyOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping
	public CompanyDto createCompany(@RequestBody CompanyDto companyDto) {
		return companyMapper.companyToDto(companyService.save(companyMapper.dtoToCompany(companyDto)));		
	}
	
	@PutMapping("/{id}")
	public CompanyDto modifyCompany(@PathVariable long id, @RequestBody CompanyDto companyDto) {
      companyDto.setId(id);
      Company updatedCompany = companyService.update(companyMapper.dtoToCompany(companyDto));
      if (updatedCompany == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }

      return companyMapper.companyToDto(updatedCompany);
	}
	
	@DeleteMapping("/{id}")
	public void deleteCompany(@PathVariable long id) {
		companyService.delete(id);
	}
	
	@PostMapping("/{id}/employees")
	public CompanyDto addNewEmployee(@PathVariable long id, @RequestBody EmployeeDto employeeDto){
		return companyMapper.companyToDto(companyService.addEmployee(id, employeeMapper.dtoToEmployee(employeeDto)));
	}
	
	@DeleteMapping("/{id}/employees/{employeeId}")
	public CompanyDto deleteEmployee(@PathVariable long id, @PathVariable long employeeId){
		return companyMapper.companyToDto(companyService.deleteEmployee(id, employeeId));
	}
	
	@PutMapping("/{id}/employees")
	public CompanyDto replaceAllEmployees(@PathVariable long id, @RequestBody List<EmployeeDto> newEmployees){
		return companyMapper.companyToDto(companyService.replaceEmployees(id, employeeMapper.dtosToEmployees(newEmployees)));
	}

	
    @GetMapping(params = "aboveSalary")
    public List<CompanyDto> getCompaniesAboveSalary(@RequestParam int aboveSalary, @RequestParam Optional<Boolean> full) {
        List<Company> filteredCompanies = companyRepository.findByEmployeeWithSalaryHigherThan(aboveSalary);
        return mapCompanies(filteredCompanies, full);
    }
    
    @GetMapping(params = "aboveEmployeeCount")
    public List<CompanyDto> getCompaniesAboveEmployeeCount(@RequestParam int aboveEmployeeCount, @RequestParam Optional<Boolean> full) {
        List<Company> filteredCompanies = companyRepository.findByEmployeeCountHigherThan(aboveEmployeeCount);
        return mapCompanies(filteredCompanies, full);
    }
	
	
	@GetMapping("/{id}/salaryStats")
	public List<AverageSalaryByPosition> getSalaryStatsById(@PathVariable long id) {
		return companyRepository.findAverageSalariesByPosition(id);
	}
}
