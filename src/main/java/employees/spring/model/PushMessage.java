package employees.spring.model;

import lombok.Data;

@Data
public class PushMessage {
	String status;
	Employee employee;
	
	public PushMessage(String status, Employee employee) {
		super();
		this.status = status;
		this.employee = employee;
	}
}
