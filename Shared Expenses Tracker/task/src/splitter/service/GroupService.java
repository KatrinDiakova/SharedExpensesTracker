package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.entity.Groups;
import splitter.entity.Members;
import splitter.repository.GroupsRepository;
import splitter.repository.MembersRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for working with groups and members.
 */

@Service
public class GroupService {

    private final GroupsRepository groupsRepository;
    private final MembersRepository membersRepository;

    @Autowired
    public GroupService(GroupsRepository groupsRepository, MembersRepository membersRepository) {
        this.groupsRepository = groupsRepository;
        this.membersRepository = membersRepository;
    }

    /**
     * Creates a new group and adds members to it if a group with the same name does not exist.
     */

    @Transactional
    public void createGroup(String groupName, Set<String> members) {
        Groups group = groupsRepository.getByGroupName(groupName);
        if (group != null) {
            deleteGroup(group);
            groupsRepository.flush();
        }
        Groups newGroup = groupsRepository.save(new Groups(groupName));
        for (String name : members) {
            Optional<Members> existsMember = membersRepository.findByMemberName(name);
            if (existsMember.isPresent()) {
                Members member = existsMember.get();
                member.getGroups().add(newGroup);
                newGroup.getMembers().add(member);
            } else {
                Members newMember = membersRepository.save(new Members(name));
                newMember.getGroups().add(newGroup);
                newGroup.getMembers().add(newMember);
            }
        }
        groupsRepository.save(newGroup);
    }

    public void deleteGroup(Groups group) {
        groupsRepository.delete(group);
    }


    /**
     * Adds members to an existing group if a group with the specified name exists.
     */

    @Transactional
    public void updateGroup(String groupName, Set<String> members) {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
        if (existingGroup.isPresent()) {
            Groups group = existingGroup.get();
            members.forEach(name -> {
                Optional<Members> findMember = membersRepository.findByMemberName(name);
                Members member = findMember.orElseGet(() -> membersRepository.save(new Members(name)));
                group.getMembers().add(member);
                groupsRepository.save(group);
            });
        } else {
            System.out.println("Group doesn't exist");
        }
    }


    /**
     * Removes a group and/or members from the group if they no longer belong to any group.
     */

    @Transactional
    public void removeFromGroup(String groupName, Set<String> membersSet) {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
        if (existingGroup.isPresent()) {
            Groups group = existingGroup.get();
            if (membersSet.isEmpty()) {
                Set<Members> members = group.getMembers();
                members.forEach(it -> {
                    if (it.getGroups().contains(group) && it.getGroups().size() == 1) {
                        it.getGroups().remove(group);
                    }
                });
                group.getMembers().clear();
                groupsRepository.delete(group);
            } else {
                Set<Members> membersToRemove = group.getMembers().stream()
                        .filter(member -> membersSet.contains(member.getMemberName()))
                        .collect(Collectors.toSet());
                group.getMembers().removeAll(membersToRemove);
                membersToRemove.forEach(member -> {
                    member.getGroups().remove(group);
                    membersRepository.save(member);
                });
                groupsRepository.save(group);

            }
        } else {
            System.out.println("Group doesn't exist");
        }
    }

    /**
     * Displays a list of members for the specified group.
     */

    @Transactional
    public void showGroup(String groupName) {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
        if (existingGroup.isPresent()) {
            Groups group = existingGroup.get();
            Set<Members> members = group.getMembers();
            List<String> memberName = new ArrayList<>();
            if (!members.isEmpty()) {
                members.forEach(member -> memberName.add(member.getMemberName()));
                Collections.sort(memberName);
                memberName.forEach(System.out::println);
            } else {
                System.out.println("Group is empty");
            }
        } else {
            System.out.println("Unknown group");
        }
    }

    @Transactional
    public Set<String> ungroupNames(List<String> groupList) {
        Set<String> namesFromGroup = new HashSet<>();
        for (String groupName : groupList) {
            Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
            if (existingGroup.isPresent()) {
                Groups group = existingGroup.get();
                Set<Members> members = group.getMembers();
                if (members.isEmpty()) {
                    System.out.println("Group is empty");
                } else {
                    for (Members member : members) {
                        namesFromGroup.add(member.getMemberName());
                    }
                }
            } else if (groupName.equals("AGROUP")) {
                System.out.println("Group is empty");
            } else {
                System.out.println("Group does not exist");
            }
        }
        return namesFromGroup;
    }
}
