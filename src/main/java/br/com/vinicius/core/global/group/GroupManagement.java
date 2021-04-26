package br.com.vinicius.core.global.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.vinicius.core.global.group.constructor.Group;

public class GroupManagement {

	private final HashMap<UUID, List<Group>> groups;

	public GroupManagement() {
		this.groups = new HashMap<>();
	}

	public final Set<Entry<UUID, List<Group>>> getGroups() {
		return groups.entrySet();
	}

	public final Group getGroupByName(List<Group> groupList, String name) {
		return groupList.stream().filter(s -> s.isName(name)).findFirst().orElse(null);
	}

	public final Group getGroupByName(Player p, String name) {
		return getGroups(p.getUniqueId()).stream().filter(s -> s.isName(name)).findFirst().orElse(null);
	}

	public final List<Group> getGroups(Player p) {
		return getGroups(p.getUniqueId());
	}

	public final List<Group> getGroups(UUID uniqueId) {
		return groups.get(uniqueId);
	}

	public final void setGroup(UUID uniqueId, Group group) {
		List<Group> groupList = getGroups(uniqueId);
		if (groupList == null)
			groupList = new ArrayList<Group>();
		if (!groupList.contains(group))
			groupList.add(group);
		this.groups.put(uniqueId, groupList);
	}

	public final boolean removeGroup(UUID uniqueId, String name) {
		List<Group> groupList = getGroups(uniqueId);
		if (groupList == null)
			return false;
		Group group = getGroupByName(groupList, name);
		if (group == null)
			return false;
		groupList.remove(group);
		this.groups.put(uniqueId, groupList);
		return true;
	}

	public final boolean asyncGetGroup(UUID uniqueId) {
		return true;
	}

}
