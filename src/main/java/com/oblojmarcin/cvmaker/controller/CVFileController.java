package com.oblojmarcin.cvmaker.controller;

import com.oblojmarcin.cvmaker.entity.CVFile;
import com.oblojmarcin.cvmaker.entity.User;
import com.oblojmarcin.cvmaker.service.CVFileService;
import com.oblojmarcin.cvmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cvfile")
public class CVFileController {

    private final CVFileService cvFileService;
    private final UserService userService;


    @Autowired
    public CVFileController(CVFileService cvFileService, UserService userService) {
        this.cvFileService = cvFileService;
        this.userService = userService;
    }


    @PostMapping("/uploadPdf")
    public ResponseEntity<Map<String, Object>> uploadCvHtml(
            @RequestParam("cvFile") MultipartFile cvFile,
            @RequestParam("userId") int userId) throws InterruptedException {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                response.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }


            String uniqueId = UUID.randomUUID().toString();
            String fileName = "CV_" + userId + "_" + uniqueId + ".pdf";
            String filePath = System.getProperty("user.dir") + "/cv-files/" + fileName;

            // Zapis pliku na serwerze
            File pdfFile = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(cvFile.getBytes());
            }


            CVFile cvFileEntity = cvFileService.saveCvFile(user, filePath, fileName);


            response.put("message", "CV zostało poprawnie zapisane w formacie PDF");
            response.put("cvFileId", cvFileEntity.getId());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "Error occurred while saving CV: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/list/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getCvFilesByUser(@PathVariable int userId) {
        List<CVFile> cvFiles = cvFileService.getCvFilesByUserId(userId);
        if (cvFiles == null || cvFiles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Map<String, Object>> responseList = cvFiles.stream().map(cvFile -> {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("id", cvFile.getId());
            responseMap.put("fileName", cvFile.getFileName());
            responseMap.put("createdAt", cvFile.getCreatedAt());
            responseMap.put("downloadUrl", "http://localhost:8080/api/cvfile/download/" + cvFile.getId());
            return responseMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCvFile(@PathVariable int id) {
        try {
            CVFile cvFile = cvFileService.getCvFileById(id);
            if (cvFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CV not found");
            }

            File file = new File(cvFile.getFilePath());
            if (file.exists()) {
                file.delete();
            }

            cvFileService.deleteCvFileById(id);
            return ResponseEntity.ok("CV deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting CV: " + e.getMessage());
        }
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadCvFile(@PathVariable int id) {
        CVFile cvFile = cvFileService.getCvFileById(id);
        if (cvFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        File file = new File(cvFile.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cvFile.getFileName() + "\"")
                .body(resource);
    }
}