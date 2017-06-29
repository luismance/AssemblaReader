package org.manaty.its;

public class AssemblaTicketChange {

	private int id;
	private String comment;
	private String ticket_id;
	private String created_on;
	private String updated_at;
	private String ticket_changes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTicket_id() {
		return ticket_id;
	}

	public void setTicket_id(String ticket_id) {
		this.ticket_id = ticket_id;
	}

	public String getCreated_on() {
		return created_on;
	}

	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getTicket_changes() {
		return ticket_changes;
	}

	public void setTicket_changes(String ticket_changes) {
		this.ticket_changes = ticket_changes;
	}

}
