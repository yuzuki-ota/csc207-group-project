package data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.junit.jupiter.api.Test;

import entity.Flashcard;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

class DictionaryDataAccessObjectTest {
    @Test
    void readsEachDefinitionUnderItsWordAndPartOfSpeech() throws IOException {
        final String entries = "[{\"word\":\"set\",\"meanings\":["
                + "{\"partOfSpeech\":\"noun\",\"definitions\":[{\"definition\":\"A collection.\"},"
                + "{\"definition\":\"A group of games.\"}]},"
                + "{\"partOfSpeech\":\"verb\",\"definitions\":[{\"definition\":\"To place.\"}]}]},"
                + "{\"word\":\"Set\",\"meanings\":[{\"partOfSpeech\":\"proper noun\","
                + "\"definitions\":[{\"definition\":\"An Egyptian god.\"}]}]}]";
        final List<Flashcard> cards = dictionary(HttpURLConnection.HTTP_OK, entries).lookup("set");
        assertEquals(4, cards.size());
        assertEquals("noun", cards.get(0).getPartOfSpeech());
        assertEquals("A collection.", cards.get(0).getDefinition());
        assertEquals("A group of games.", cards.get(1).getDefinition());
        assertEquals("verb", cards.get(2).getPartOfSpeech());
        assertEquals("Set", cards.get(3).getWord());
    }

    @Test
    void missingWordHasNoDefinitions() throws IOException {
        assertTrue(dictionary(HttpURLConnection.HTTP_NOT_FOUND, "{}").lookup("set").isEmpty());
    }

    @Test
    void serverErrorAndMalformedResponseReportLookupFailure() {
        assertThrows(IOException.class, () -> dictionary(HttpURLConnection.HTTP_UNAVAILABLE, "").lookup("set"));
        assertThrows(IOException.class, () -> dictionary(HttpURLConnection.HTTP_OK, "[{}]").lookup("set"));
    }

    private DictionaryDataAccessObject dictionary(int status, String json) {
        final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            return new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                    .code(status).message("Test response")
                    .body(ResponseBody.create(json, MediaType.get("application/json"))).build();
        }).build();
        return new DictionaryDataAccessObject(client);
    }
}
