package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.entity.Groups;
import splitter.entity.Members;
import splitter.repository.GroupsRepository;
import splitter.repository.MembersRepository;

import java.util.*;

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
    public void createGroup(String groupName, List<String> members) {
        if (!groupsRepository.existsByGroupName(groupName)) {
            Groups group = new Groups(groupName);

            for (String name : members) {
                Members member = membersRepository.findByMemberName(name)
                        .orElseGet(() -> {
                            Members newMember = new Members(name);
                            membersRepository.save(newMember);
                            return newMember;
                        });

                member.getGroups().add(group);
                group.getMembers().add(member);
            }

            groupsRepository.save(group);
        } else {
            System.out.println("Group already exist");
        }
    }
//        if (groupsRepository.findByGroupName(groupName).isEmpty()) {
//            Groups group = groupsRepository.save(new Groups(groupName));
//            for (String name : members) {
//                Optional<Members> existsMember = membersRepository.findByMemberName(name);
//                if (existsMember.isPresent()) {
//                    Members member = existsMember.get();
//                    member.getGroups().add(group);
//                    group.getMembers().add(member);
//                } else {
//                    Members newMember = membersRepository.save(new Members(name));
//                    newMember.getGroups().add(group);
//                    group.getMembers().add(newMember);
//                }
//            }
//            groupsRepository.save(group);
//        }
//    }
//        else {
//            System.out.println("Group already exist");
//        }


    /**
     * Adds members to an existing group if a group with the specified name exists.
     */

    @Transactional
    public void updateGroup(String groupName, List<String> members) {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
        if (existingGroup.isPresent()) {
            Groups group = existingGroup.get();
            members.forEach(name -> {
                Members member = membersRepository.save(new Members(name));
                group.getMembers().add(member);
            });
        } else {
            System.out.println("Group doesn't exist");
        }
    }


    /**
     * Removes a group and/or members from the group if they no longer belong to any group.
     */

    @Transactional
    public void removeFromGroup(String groupName, List<String> members) {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
        if (existingGroup.isPresent()) {
            Groups group = existingGroup.get();
            if (members.isEmpty()) {
                Iterator<Members> iterator = group.getMembers().iterator();
                while (iterator.hasNext()) {
                    Members member = iterator.next();
                    if (member.getGroups().contains(group) && member.getGroups().size() == 1) {
                        iterator.remove();
                        membersRepository.delete(member);
                    }
                }
                groupsRepository.delete(group);
            } else {
                members.forEach(membersRepository::deleteByMemberName);
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
            List<Members> members = group.getMembers();
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
    public List<String> ungroupNames(List<String> groupList) {
        List<String> namesFromGroup = new ArrayList<>();
        for (String groupName : groupList) {
            Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
            if (existingGroup.isPresent()) {
                Groups group = existingGroup.get();
                List<Members> members = group.getMembers();
                for (Members member : members) {
                    namesFromGroup.add(member.getMemberName());
                }
            }
        }
        return namesFromGroup;
    }
}
