package employees.spring.service;

import static employees.spring.api.EmployeesConfig.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import employees.spring.model.Employee;
import employees.spring.model.PushMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.spring.exceptions.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
	
	final SimpMessagingTemplate notifier;
	
	private Map<Long, Employee> employees= new HashMap<>();
	
	
	@Override
	public Employee addEmployee(Employee employee) {
		if (employee.getId() == null) {
			employee.setId(getRandomId());
		}
			Employee addedEmpl = employees.putIfAbsent(employee.getId(), employee);
			if (addedEmpl != null) {
				throw new RuntimeException("Employee with id " + employee.getId() + " already exists");
			}
			notifier.convertAndSend("/topic/employees", new PushMessage("added", employee));
			return employee;
	}
	
private Long getRandomId() {
		Long id = null;
		do {
			id = getRandomNum(MIN_ID, MAX_ID);
			} while (exists(id));
			return id;
			}

private boolean exists(Long id) {
	Employee res = employees.get(id);
	return res != null;
}

private Long getRandomNum(int minId, int maxId) {
	return (long) (minId + Math.random() * (maxId - minId));
}

	
	@Override
	public  List<Employee> getAllEmployees() {
			return new ArrayList<Employee>(employees.values());
	
	}
	
	
	@Override
	public Employee getEmployee(long id) {
		Employee empl = employees.get(id);
		if (empl == null) {
			throw new NotFoundException("Employee with id: " + id + " not found");
		}
		return empl;
	}
	

	
	@Override
	public void deleteEmployee(Long id) {
		Employee removedEmpl = employees.get(id);
		if (removedEmpl != null) {
			employees.remove(id);
			log.trace("Employee with id: {} was removed", id);
		} else {
			log.error("Employee with id: {} not found", id);
			throw new NotFoundException("Employee with id: " + id + " not found");
		}
		notifier.convertAndSend("/topic/employees", new PushMessage("removed", removedEmpl));
	}
	
	
	@Override
	public Employee updateEmployee(Employee empl) {
		if (!employees.containsKey(empl.getId())) {
				throw new NotFoundException("Not found " + empl.getId());
			}
			Employee emplFound = employees.put(empl.getId(), empl);		
			notifier.convertAndSend("/topic/employees", new PushMessage("updated", empl));
			return emplFound;
	}
	
	
	@Override
	public void save() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
			log.info("Saving data of employees to the file: {} ", FILE_NAME);
			out.writeObject(getAllEmployees());
			log.info("Saving to: {} was completed. Amount of employees are: {}", FILE_NAME, employees.size());
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void restore() {
		List<Employee> emplArr = new ArrayList<Employee>();
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
			log.info("Restoring from the file: {} ", FILE_NAME);
			emplArr = (List<Employee>) in.readObject();
			Iterator<Employee> iterator = emplArr.iterator();
			while (iterator.hasNext()) {
				addEmployee(iterator.next());
			}
			log.info("Restoring from: {} was completed. Amount of employees are: {}", FILE_NAME, emplArr.size()); 
		} catch (FileNotFoundException e) {
			log.error("File with name: {} not found", FILE_NAME);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	



}
