package arquivo.crawler;

import arquivo.repository.RateLimiterRepository;
import arquivo.services.RateLimiterService;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
public class FaceDetection {

    private static final Logger LOG = LoggerFactory.getLogger(FaceDetection.class);

    private final RateLimiterService rateLimiterService;

    public FaceDetection(RateLimiterRepository rateLimiterRepository) {
        this.rateLimiterService = new RateLimiterService(rateLimiterRepository);
    }

    public double detectFace(String imagePath) {
        // Read the image into memory
        InputStream imageFile;
        try {
            imageFile = getFileFromUrl(imagePath);
        } catch (IOException e) {
            LOG.error("There was an error fetching image on url {}", imagePath);
            return 0.0;
        }

        ByteString imgBytes;
        try {
            imgBytes = ByteString.readFrom(imageFile);
        } catch (IOException e) {
            LOG.error("There was an reading the file from url {}", imagePath);
            return 0.0;
        }

        // Create an Image object from the image bytes
        final Image img = Image.newBuilder().setContent(imgBytes).build();

        // Set the feature for face detection
        final Feature feature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

        // Create the request
        final AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .setImage(img)
                .addFeatures(feature)
                .build();

        // Use the ImageAnnotatorClient to send the request
        rateLimiterService.increment("google-api");

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            List<AnnotateImageResponse> responses;
            try {
                responses = client.batchAnnotateImages(List.of(request)).getResponsesList();
            } catch (InvalidArgumentException ex) {
                LOG.error("There was an error calling the google api for url {}", imagePath);
                return 0.0;
            }

            // Check the responses for face annotations
            final AnnotateImageResponse response = responses.get(0);
            if (response != null) {
                // Check if face annotations are present in the response
                final List<FaceAnnotation> faceAnnotations = response.getFaceAnnotationsList();
                if (faceAnnotations.isEmpty()) {
                    return 0.0;
                } else {
                    if (faceAnnotations.size() == 1) { // found a single face
                        return faceAnnotations.get(0).getDetectionConfidence();
                    } else {
                        if (faceAnnotations.size() == 2) { // two people just send half
                            return faceAnnotations.get(0).getDetectionConfidence() / 2;
                        } // more than 2, just zero to skip it
                        return 0.0;
                    }
                }
            }
        } catch (IOException e) {
            return 0.0;
        }
        return 0.0;
    }

    private InputStream getFileFromUrl(String imageUrl) throws IOException {
        final URL url = new URL(imageUrl);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setConnectTimeout(5000); //set timeout to 5 seconds
        connection.setReadTimeout(5000); //set timeout to 5 seconds
        connection.connect();

        // Get the InputStream from the connection
        return connection.getInputStream();
    }
}
