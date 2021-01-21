package br.com.vinicius.core.global.group.constructor;

public class Group {

	private final int id;
	private final int priority;
	
	private final String name;
	private final String plural;
	
	private String prefix;
	private String color;
	
	private boolean isStaff;
	
	public Group(int id, int priority, String name, String plural) {
		this.id = id;
		this.priority = priority;
		this.name = name;
		this.plural = plural;
	}
	
	public final int getId() {
		return id;
	}
	
	public final int getPriority() {
		return priority;
	}
	
	public final String getName() {
		return name;
	}
	
	public final boolean isName(String name) {
		return this.name.equalsIgnoreCase(name);
	}
	
	public final String getPlural() {
		return plural;
	}
	
	public final String getPrefix() {
		return prefix;
	}
	
	public final void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public final String getColor() {
		return color;
	}
	
	public final boolean isStaff() {
		return isStaff;
	}
	
	public final void setStaff(boolean isStaff) {
		this.isStaff = isStaff;
	}
	
}
