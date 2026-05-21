package sv.project.e_commerce.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AIService {

    UserRepository userRepository;

    public String analyzeFaceForUser(User user, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        String faceShape = "OVAL"; // Default fallback

        try {
            // Attempt to call the Python FastAPI AI Service
            RestTemplate restTemplate = new RestTemplate();
            String aiServiceUrl = "http://localhost:8000/analyze-face";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", contentsAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(aiServiceUrl, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("face_shape")) {
                    faceShape = responseBody.get("face_shape").toString().toUpperCase();
                    log.info("AI Service successfully identified face shape: {}", faceShape);
                }
            }

        } catch (Exception e) {
            log.warn("Failed to connect to Python FastAPI AI service: {}. Falling back to simulated/mock face shape.", e.getMessage());
            
            // Smart simulated detection based on file name hash to be consistent
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
            int hash = Math.abs(fileName.hashCode());
            String[] shapes = {"ROUND", "OVAL", "SQUARE", "HEART"};
            faceShape = shapes[hash % shapes.length];
            log.info("Simulated/mock face shape determined for demo: {}", faceShape);
        }

        // Save the face shape to the User
        User dbUser = userRepository.findByIdAndEnabledTrue(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        dbUser.setFaceShape(faceShape);
        userRepository.save(dbUser);

        return faceShape;
    }
}
