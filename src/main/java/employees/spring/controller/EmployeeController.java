package employees.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employees.spring.model.Employee;
import employees.spring.service.EmployeeService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin
public class EmployeeController {
	
@Autowired	
EmployeeService service;

	
	@PostMapping
	public Employee addEmployee(@RequestBody @Valid Employee employee) {
		Long id = employee.getId();
		if (id != null) {
			log.warn("received id {} from client", id);
			employee.setId(null);
		}
		Employee res = service.addEmployee(employee);
		log.debug("Employee with id {} was added", employee.getId());
		return res;
	}

	@GetMapping
	public List<Employee> getEmployees() {
		List<Employee> empls = service.getAllEmployees();
		log.trace("{} employees are received {} ", empls.size(), empls);
		return empls;
	}
	@GetMapping("/{id}")
	public Employee getEmployee(@PathVariable long id) {
		return service.getEmployee(id);
	}


	@DeleteMapping("{id}")
	public void deleteEmployee(@PathVariable Long id) {
			service.deleteEmployee(id);
			log.debug("Employee with id {} has been removed", id);		
	}

	@PutMapping("{id}")
	public Employee updateEmployee(@PathVariable long id, @RequestBody @Valid Employee empl) {
		if (empl.getId() != id) {
			throw new IllegalArgumentException("id doesn't exist");
		}
		Employee emplUpdated = service.updateEmployee(empl);
		log.debug("Employee with id {} was updated", id);
		return emplUpdated;
	}
	
	@PreDestroy
	public void save() {
		service.save();
	}
	
	
	@PostConstruct
	public void restore() {
		service.restore();;
	}
	

}
