package sv.project.e_commerce.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) {
         if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), root.resolve(fileName));

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_INPUT_ERR);
        }
    }
}