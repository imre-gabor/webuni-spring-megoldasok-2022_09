package hu.webuni.hr.minta.web;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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




@RestController
@RequestMapping("/api/companies")
public class CompanyController {

	
	
	@GetMapping
	public List<CompanyDto> getCompanies(@RequestParam Optional<Boolean> full){
		return null;
		
//		if(full.orElse(false))		
//			return new ArrayList<>(companies.values());
//		else
//			return companies.values().stream()
//					.map(c -> createCompanyWithoutEmployees(c))
//					.collect(Collectors.toList());
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
		return null;
//		CompanyDto company = findByIdOrThrow(id);
//		if(!full.orElse(false))
//			company = createCompanyWithoutEmployees(company);
//		return company;		
	}
	
	@PostMapping
	public CompanyDto createCompany(@RequestBody CompanyDto companyDto) {
		return null;
//		return companyMapper.companyToDto(companyService.save(companyMapper.dtoToCompany(companyDto)));		
	}
	
	@PutMapping("/{id}")
	public CompanyDto modifyCompany(@PathVariable long id, @RequestBody CompanyDto companyDto) {
		return null;
//      companyDto.setId(id);
//      Company updatedCompany = companyService.update(companyMapper.dtoToCompany(companyDto));
//      if (updatedCompany == null) {
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//      }
//
//      return companyMapper.companyToDto(updatedCompany);
	}
	
	@DeleteMapping("/{id}")
	public void deleteCompany(@PathVariable long id) {
//      companyService.delete(id);
	}
	
	@PostMapping("/{id}/employees")
	public CompanyDto addNewEmployee(@PathVariable long id, @RequestBody EmployeeDto employeeDto){
		return null;
//		CompanyDto company = findByIdOrThrow(id);
//		company.getEmployees().add(employeeDto);
//		return company;
	}
	
	@DeleteMapping("/{id}/employees/{employeeId}")
	public CompanyDto deleteEmployee(@PathVariable long id, @PathVariable long employeeId){
		return null;
//		CompanyDto company = findByIdOrThrow(id);
//		company.getEmployees().removeIf(emp -> emp.getId() == employeeId);
//		return company;
	}
	
	@PutMapping("/{id}/employees")
	public CompanyDto replaceAllEmployees(@PathVariable long id, @RequestBody List<EmployeeDto> newEmployees){
		return null;
//		CompanyDto company = findByIdOrThrow(id);
//		company.setEmployees(newEmployees);
//		return company;
	}
	
	private CompanyDto findByIdOrThrow(long id) {
		return null;
//		CompanyDto company = companies.get(id);
//		if(company == null)
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//		return company;
	}
	
//	private CompanyDto createCompanyWithoutEmployees(CompanyDto c) {
//		return new CompanyDto(c.getId(), c.getRegistrationNumber(), c.getName(), c.getAddress(), null);
//	}
	
}
