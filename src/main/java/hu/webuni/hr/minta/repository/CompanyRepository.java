package hu.webuni.hr.minta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hu.webuni.hr.minta.model.AverageSalaryByPosition;
import hu.webuni.hr.minta.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{
	
	@Query("SELECT c FROM Company c")
	@EntityGraph(attributePaths = {"employees", "employees.position"})
	public List<Company> findAllWithEmployees();
	
	@Query("SELECT DISTINCT c FROM Company c JOIN c.employees e WHERE e.salary > :minSalary")
	public List<Company> findByEmployeeWithSalaryHigherThan(int minSalary);
	
	
	@Query("SELECT c FROM Company c WHERE SIZE(c.employees) > :minEmployeeCount")
	public List<Company> findByEmployeeCountHigherThan(int minEmployeeCount);

	
	@Query(
		"SELECT e.position.name AS position, avg(e.salary) AS averageSalary "
		+ "FROM Company c "
		+ "INNER JOIN c.employees e "
		+ "WHERE c.id=:companyId "
		+ "GROUP BY e.position.name "
		+ "ORDER BY avg(e.salary) DESC"
	)
	public List<AverageSalaryByPosition> findAverageSalariesByPosition(long companyId);

	@Query("SELECT c FROM Company c WHERE c.id = :id")
	@EntityGraph(attributePaths = {"employees", "employees.position"})
	public Optional<Company> findByIdWithEmployees(long id);
	
}
