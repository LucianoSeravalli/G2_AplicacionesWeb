package LukSportPrueba;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class StorageConfig {

    @Value("${firebase.json.path}")
    private String jsonPath;

    @Value("${firebase.json.file}")
    private String jsonFile;

    @Bean
    public Storage storage() throws IOException {

        String ruta = jsonPath + "/" + jsonFile;

        ClassPathResource resource = new ClassPathResource(ruta);
        
        System.out.println("jsonPath: " + jsonPath);
        System.out.println("jsonFile: " + jsonFile);
        System.out.println("Ruta Firebase JSON: " + ruta);
        System.out.println("Existe recurso: " + resource.exists());

        try (InputStream inputStream = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        }
    }
}
