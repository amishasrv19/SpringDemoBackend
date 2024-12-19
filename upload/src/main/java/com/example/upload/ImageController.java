package com.example.upload;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ImageController {

    private static final String UPLOAD_DIR = "uploads";

    
    @PostMapping("/upload")
    @CrossOrigin
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
           
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

           
            Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }

 
    @GetMapping("/{fileName}")
    @CrossOrigin
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
          
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

           
            byte[] fileBytes = Files.readAllBytes(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(filePath));

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
    @DeleteMapping("/delete/{fileName}")
    @CrossOrigin
    public ResponseEntity<String> deleteImage(@PathVariable String fileName) {
        try {
           
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }

           
            Files.delete(filePath);

            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }
   

   @GetMapping("/images")
    @CrossOrigin
    public ResponseEntity<List<String>> listImages() {
        try {
           
            List<String> imageFiles = Files.list(Paths.get(UPLOAD_DIR))
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString()) 
                    .collect(Collectors.toList());
            System.out.println("Images found: " + imageFiles); 
            return ResponseEntity.ok(imageFiles);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}