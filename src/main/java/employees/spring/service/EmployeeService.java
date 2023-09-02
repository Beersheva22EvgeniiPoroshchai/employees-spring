package employees.spring.service;

import java.util.List;

import employees.spring.model.Employee;

public interface EmployeeService {
	
	Employee addEmployee(Employee employee);
    List<Employee> getAllEmployees();
    void deleteEmployee(Long id);
    Employee updateEmployee(Employee empl);
	Employee getEmployee(long id);
	
//	void save(List<Employee> listEmployees);
//	List<Employee> restore();
	
	
	void save();
	void restore();
	
	
	
//    Long addEmployee(Employee employee);
//    List<Employee> getAllEmployees();
//    void deleteEmployee(Long id);
//    Employee updateEmployee(long id, Employee empl);
//	Employee getEmployee(long id);
}
