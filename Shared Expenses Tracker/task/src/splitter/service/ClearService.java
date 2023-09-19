package splitter.service;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.repository.BalanceRepository;
import splitter.repository.GroupsRepository;
import splitter.repository.MembersRepository;
import splitter.repository.TransactionsRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ClearService implements CommandProcessor {
    private final GroupsRepository groupsRepository;
    private final MembersRepository membersRepository;

    @Autowired
    public ClearService(GroupsRepository groupsRepository, MembersRepository membersRepository) {
        this.groupsRepository = groupsRepository;
        this.membersRepository = membersRepository;
    }
    @Override
    public void process(List<String> input) {
        groupsRepository.deleteAll();
        membersRepository.deleteAll();
    }
//    private final String databasePath;
//
//    public ClearService() {
//        databasePath = "../testDB" + ".mv.db";
//    }
//
//
//
//
//    private void replaceDatabase() {
//
//        String dbFilePath = System.getProperty("user.dir")
//                + File.separator + databasePath;
//
//        String dbTempFilePath = dbFilePath + "-real";
//
//        Path dbFile = Paths.get(dbFilePath);
//        Path dbTempFile = Paths.get(dbTempFilePath);
//
//        try {
//            if (dbTempFile.toFile().exists()) {
//                Files.deleteIfExists(dbFile);
//            } else if (dbFile.toFile().exists() && !dbTempFile.toFile().exists()) {
//                Files.move(dbFile, dbTempFile);
//            }
//        } catch (IOException ignored) {
//        }
//    }
//
//    private void revertDatabase() {
//
//        String dbFilePath = System.getProperty("user.dir")
//                + File.separator + databasePath;
//
//        String dbTempFilePath = dbFilePath + "-real";
//
//        Path dbFile = Paths.get(dbFilePath);
//        Path dbTempFile = Paths.get(dbTempFilePath);
//
//        try {
//            Files.deleteIfExists(dbFile);
//            if (dbTempFile.toFile().isFile()) {
//                Files.move(dbTempFile, dbFile);
//            }
//        } catch (IOException ignored) {
//        }

//    @Override
//    public List<Command> getCommand() {
//        return List.of(Command.clear);
//    }
}
