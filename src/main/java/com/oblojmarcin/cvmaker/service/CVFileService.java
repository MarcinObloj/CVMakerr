package com.oblojmarcin.cvmaker.service;

import com.oblojmarcin.cvmaker.entity.CVFile;
import com.oblojmarcin.cvmaker.entity.User;
import com.oblojmarcin.cvmaker.repository.CVFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CVFileService {

    private final CVFileRepository cvFileRepository;

    @Autowired
    public CVFileService(CVFileRepository cvFileRepository) {
        this.cvFileRepository = cvFileRepository;
    }


    public CVFile saveCvFile(User user, String filePath, String fileName) {
        CVFile cvFile = new CVFile();
        cvFile.setUser(user);
        cvFile.setFilePath(filePath);
        cvFile.setFileName(fileName);
        cvFile.setCreatedAt(LocalDateTime.now());
        return cvFileRepository.save(cvFile);
    }


    public CVFile getCvFileById(int cvFileId) {
        return cvFileRepository.findById(cvFileId).orElse(null);
    }


    public List<CVFile> getCvFilesByUserId(int userId) {
        return cvFileRepository.findByUserUserId(userId);
    }

    public void deleteCvFileById(int id) {
        cvFileRepository.deleteById(id);
    }


}
