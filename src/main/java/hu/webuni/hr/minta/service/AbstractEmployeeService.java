package hu.webuni.hr.minta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import hu.webuni.hr.minta.model.Employee;

@Service
public abstract class AbstractEmployeeService implements EmployeeService {

	@Override
	public Employee save(Employee employee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Employee update(Employee employee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Employee> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Employee> findById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub
	}
	
}
