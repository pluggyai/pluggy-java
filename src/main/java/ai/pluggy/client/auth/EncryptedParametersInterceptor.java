package ai.pluggy.client.auth;

import static ai.pluggy.utils.Asserts.assertNotNull;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import okio.Buffer;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EncryptedParametersInterceptor implements Interceptor {
    private String rsaPublicKey;
    private String path = "/items";
    private String[] methods = { "PATCH", "POST" };

    public EncryptedParametersInterceptor(String rsaPublicKey) {
        assertNotNull(rsaPublicKey, rsaPublicKey);
        this.rsaPublicKey = rsaPublicKey;
    }

    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String method = originalRequest.method();
        RequestBody originalBody = originalRequest.body();

        if (originalBody == null) {
            return chain.proceed(originalRequest);
        }

        JsonObject jsonBody = this.transformBodyToJsonObject(originalBody);

        if (!Arrays.asList(methods).contains(method)
                || !originalRequest.url().encodedPath().contains(path) && !jsonBody.has("parameters")) {
            return chain.proceed(originalRequest);
        }

        String parameters = jsonBody.get("parameters").toString();

        String encryptedParameters = encryptParameters(parameters);

        jsonBody.addProperty("parameters", encryptedParameters);

        // create new request with new body and same headers/params
        Request newRequest = originalRequest.newBuilder()
                .method(method, RequestBody.create(jsonBody.toString(), originalBody.contentType()))
                .build();

        return chain.proceed(newRequest);
    }

    private String encryptParameters(String parameters) throws IOException {
        String publicKeyPEM = this.rsaPublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedPayloadBytes = cipher.doFinal(parameters.getBytes());
            String encryptedPayload = Base64.getEncoder().encodeToString(encryptedPayloadBytes);
            return encryptedPayload;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException error) {
            throw new IOException("Error encrypting parameters", error);
        }
    }

    private JsonObject transformBodyToJsonObject(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        try {
            body.writeTo(buffer);
        } catch (IOException error) {
            throw error;
        }
        String bodyString = buffer.readUtf8();

        JsonObject jsonBody = new Gson().fromJson(bodyString, JsonObject.class);

        return jsonBody;
    }
}
