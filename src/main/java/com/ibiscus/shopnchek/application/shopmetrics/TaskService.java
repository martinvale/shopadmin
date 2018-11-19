package com.ibiscus.shopnchek.application.shopmetrics;

import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatus;
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private BatchTaskStatusRepository batchTaskStatusRepository;

    @Transactional
    public String createTask(String fileName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        String processName = fileName + dateFormat.format(new Date());
        BatchTaskStatus batchTaskStatus = new BatchTaskStatus(processName);
        batchTaskStatusRepository.save(batchTaskStatus);
        return processName;
    }

    @Transactional
    public void updateTaskErrorMessage(final String name,
            final String errorMessage) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
        batchTaskStatus.error(errorMessage);
        batchTaskStatusRepository.update(batchTaskStatus);
    }

    @Transactional
    public void updatePorcentage(final String name, final int porcentage) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
        batchTaskStatus.setProcentage(porcentage);
        batchTaskStatusRepository.update(batchTaskStatus);
    }

    @Transactional
    public void start(final String name) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
        batchTaskStatus.start();
        batchTaskStatusRepository.update(batchTaskStatus);
    }

    @Transactional
    public void finish(final String name, final List<ShopmetricsUserDto> users) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
        batchTaskStatus.finish();
        batchTaskStatus.setAdditionalInfo(getImportErrorInfo(users));
        batchTaskStatusRepository.update(batchTaskStatus);
    }

    private String getImportErrorInfo(final List<ShopmetricsUserDto> users) {
        String additionalInfo = null;
        if (!users.isEmpty()) {
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            for (ShopmetricsUserDto userDto : users) {
                if (!first) {
                    builder.append(",");
                }
                first = false;
                builder.append("\"");
                builder.append(userDto.getLogin());
                builder.append("\"");
            }
            builder.append("]");
            additionalInfo = builder.toString();
        }
        return additionalInfo;
    }

    public void setBatchTaskStatusRepository(BatchTaskStatusRepository batchTaskStatusRepository) {
        this.batchTaskStatusRepository = batchTaskStatusRepository;
    }
}
