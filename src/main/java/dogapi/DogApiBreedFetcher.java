package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    public final String allBreed = "https://dog.ceo/api/breeds/list/all";


    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        Request request = new Request.Builder()
                .url(allBreed)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException("Failed to fetch breeds from API.");
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            JSONObject breedsObject = json.getJSONObject("message");

            // Check if the breed exists
            if (!breedsObject.has(breed)) {
                throw new BreedNotFoundException("Breed not found: " + breed);
            }

            // Extract sub-breed list
            JSONArray subArray = breedsObject.getJSONArray(breed);
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < subArray.length(); i++) {
                subBreeds.add(subArray.getString(i));
            }

            return subBreeds;

        } catch (IOException e) {
            throw new BreedNotFoundException("Error communicating with the Dog API.");
        }
    }
}