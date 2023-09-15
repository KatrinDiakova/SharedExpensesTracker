package splitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.entity.Groups;
import splitter.entity.Members;
import splitter.repository.GroupsRepository;

import java.util.*;

@Service
public class SecretSanta implements CommandProcessor {

    private final GroupsRepository groupsRepository;

    @Autowired
    public SecretSanta(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }


    @Override
    @Transactional
    public void process(List<String> input) {
        String groupName = input.get(1);
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(groupName);
        if (existingGroup.isPresent()) {
            Groups group = existingGroup.get();
            List<Members> members = group.getMembers();
            List<Members> shuffledMembers = shuffleMembers(members);
            for (int i = 0; i < members.size(); i++) {
                System.out.println(members.get(i).getMemberName() + " gift to " + shuffledMembers.get(i).getMemberName());
            }
        }
    }

    private List<Members> shuffleMembers(List<Members> members) {
        List<Members> shuffleMembers = new ArrayList<>(members);
        Random random = new Random(System.currentTimeMillis());
        do {
            Collections.shuffle(shuffleMembers, random);
        } while (!isShuffleValid(members, shuffleMembers));

        return shuffleMembers;
    }

    private boolean isShuffleValid(List<Members> members, List<Members> shuffled) {
        Map<Members, Members> giftPairs = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equals(shuffled.get(i))) {
                return false;
            }
            giftPairs.put(members.get(i), shuffled.get(i));
        }
        if (members.size() > 2) {
            for (Map.Entry<Members, Members> entry : giftPairs.entrySet()) {
                Members giver = entry.getKey();
                Members receiver = entry.getValue();

                if (giftPairs.get(receiver) != null && giftPairs.get(receiver).equals(giver)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Command> getCommand() {
        return Collections.singletonList(Command.secretSanta);
    }
}
