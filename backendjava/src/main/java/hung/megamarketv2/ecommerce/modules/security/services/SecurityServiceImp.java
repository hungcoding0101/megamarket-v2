package hung.megamarketv2.ecommerce.modules.security.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hung.megamarketv2.common.generic.constants.SecurityConstants;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.SecurityServiceErrorCodes;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class SecurityServiceImp implements SecurityService {

    @Value("${token-uri}")
    private String tokenUri;

    @Value("${resource-server.client-id}")
    private String resourceServerClientId;

    @Value("${resource-server.client-secret}")
    private String resourceServerClientSecret;

    private final Logger logger = LoggerFactory.getLogger(SecurityServiceImp.class);

    @Override
    public Result<String, SecurityServiceErrorCodes> getAccessTokenForUser(Long userId, List<String> roles) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(tokenUri).newBuilder();
        urlBuilder.addQueryParameter(SecurityConstants.SUB, userId.toString());
        for (String role : roles)
            urlBuilder.addQueryParameter(SecurityConstants.AUTHORITIES, SecurityConstants.ROLE_PREFIX + role);
        String url = urlBuilder.build().toString();

        String credentials = Credentials.basic(resourceServerClientId, resourceServerClientSecret);
        RequestBody formBody = new FormBody.Builder()
                .add(SecurityConstants.GRANT_TYPE, SecurityConstants.CLIENT_CREDENTIALS)
                .build();

        Request request = new Request.Builder().url(url).header(SecurityConstants.AUTHORIZATION, credentials)
                .post(formBody).build();
        Call call = client.newCall(request);

        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Result.ofError(SecurityServiceErrorCodes.UNABLE_TO_GET_ACCESS_TOKEN);
        }

        if (!response.isSuccessful()) {
            logger.error(SecurityServiceErrorCodes.UNABLE_TO_GET_ACCESS_TOKEN + String.valueOf(response.code()));
            return Result.ofError(SecurityServiceErrorCodes.UNABLE_TO_GET_ACCESS_TOKEN);
        }

        String jsonData;
        try {
            jsonData = response.body().string();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Result.ofError(SecurityServiceErrorCodes.UNABLE_TO_GET_ACCESS_TOKEN);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> entity = objectMapper.readValue(jsonData, Map.class);
            return Result.ofValue(entity.get(SecurityConstants.ACCESS_TOKEN));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.ofError(SecurityServiceErrorCodes.UNABLE_TO_GET_ACCESS_TOKEN);
        }

    }

}
