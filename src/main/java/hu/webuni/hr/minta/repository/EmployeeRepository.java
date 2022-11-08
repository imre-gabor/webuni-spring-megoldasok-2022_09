package hu.webuni.hr.minta.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import hu.webuni.hr.minta.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

	Page<Employee> findBySalaryGreaterThan(Integer minSalary, Pageable pageable);
	
	List<Employee> findByPositionName(String jobTitle);
	
	List<Employee> findByNameStartingWithIgnoreCase(String name);
	
	List<Employee> findByDateOfStartWorkBetween(LocalDateTime start, LocalDateTime end);

	@Transactional
	@Modifying
	// 1. megoldás: nem működik, UPDATE + JOIN miatt
//	@Query("UPDATE Employee e "
//			+ "SET e.salary = :minSalary "
//			+ "WHERE e.position.name = :position "
//			+ "AND e.company.id = :companyId "
//			+ "AND e.salary < :minSalary")
	@Query("UPDATE Employee e "
			+ "SET e.salary = :minSalary "
			+ "WHERE e.id IN "
			+ "(SELECT e2.id "
			+ "FROM Employee e2 "			
			+ "WHERE e2.position.name = :position "
			+ "AND e2.company.id = :companyId "
			+ "AND e2.salary < :minSalary)")

	void updateSalaries(long companyId, String position, int minSalary);
	

}
