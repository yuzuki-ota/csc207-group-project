package data_access;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Flashcard;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import use_case.lookup.LookupDataAccessInterface;

public class DictionaryDataAccessObject implements LookupDataAccessInterface {
    private static final HttpUrl ENTRIES = HttpUrl.get("https://api.dictionaryapi.dev/api/v2/entries/en/");
    private final OkHttpClient client;

    public DictionaryDataAccessObject() {
        this(new OkHttpClient());
    }

    DictionaryDataAccessObject(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public List<Flashcard> lookup(String word) throws IOException {
        final HttpUrl url = ENTRIES.newBuilder().addPathSegment(word).build();
        final Request request = new Request.Builder().url(url).build();
        final List<Flashcard> meanings;
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                meanings = Collections.emptyList();
            }
            else if (response.isSuccessful()) {
                meanings = readEntries(new JSONArray(response.body().string()));
            }
            else {
                throw new IOException("Dictionary returned HTTP " + response.code());
            }
        }
        catch (JSONException exception) {
            throw new IOException("Could not read the dictionary response.", exception);
        }
        return meanings;
    }

    private List<Flashcard> readEntries(JSONArray entries) {
        final List<Flashcard> cards = new ArrayList<>();
        for (int i = 0; i < entries.length(); i++) {
            final JSONObject entry = entries.getJSONObject(i);
            final String word = entry.getString("word");
            final JSONArray meanings = entry.getJSONArray("meanings");
            for (int j = 0; j < meanings.length(); j++) {
                addDefinitions(cards, word, meanings.getJSONObject(j));
            }
        }
        return cards;
    }

    private void addDefinitions(List<Flashcard> cards, String word, JSONObject meaning) {
        final String partOfSpeech = meaning.getString("partOfSpeech");
        final JSONArray definitions = meaning.getJSONArray("definitions");
        for (int i = 0; i < definitions.length(); i++) {
            final String definition = definitions.getJSONObject(i).getString("definition");
            cards.add(new Flashcard(word, partOfSpeech, definition));
        }
    }
}
