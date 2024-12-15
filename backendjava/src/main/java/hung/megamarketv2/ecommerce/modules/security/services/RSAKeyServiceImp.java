package hung.megamarketv2.ecommerce.modules.security.services;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jose.jwk.RSAKey;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.generic.models.PersistentKeyPair;
import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.KeyPairRepositoryErrorCodes;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.RSAKeyServiceErrorCodes;
import hung.megamarketv2.ecommerce.modules.security.repositories.KeyPairRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RSAKeyServiceImp implements RSAKeyService {

    private final KeyPairRepository keyPairRepository;

    private final BytesEncryptor bytesEncryptor;

    @Override
    public Result<RSAKey, RSAKeyServiceErrorCodes> getLatestKey() {
        Result<PersistentKeyPair, KeyPairRepositoryErrorCodes> keyPairOutcome = keyPairRepository.findLatest();

        if (!keyPairOutcome.isSuccessful) {
            return Result.ofError(RSAKeyServiceErrorCodes.KEY_NOT_FOUND);
        }

        try {
            PersistentKeyPair keyPair = keyPairOutcome.value;

            byte[] publicKeyBytes = bytesEncryptor.decrypt(keyPair.getPublicKeyBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory;

            keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

            byte[] privateKeyBytes = bytesEncryptor.decrypt(keyPair.getPrivateKeyBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            RSAPrivateKey privateKey;
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(keyPair.getTextId())
                    .build();

            return Result.ofValue(rsaKey);
        } catch (NoSuchAlgorithmException e) {
            return Result.ofError(RSAKeyServiceErrorCodes.WRONG_KEY_ALGORITHM_NAME);
        } catch (InvalidKeySpecException e) {
            return Result.ofError(RSAKeyServiceErrorCodes.INVALID_KEY_SPECIFICATION);

        }

    }

    @Override
    public Result<RSAKey, RSAKeyServiceErrorCodes> createNewKey() {
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            return Result.ofError(RSAKeyServiceErrorCodes.UNABLE_TO_CREATE_KEY);
        }

        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        byte[] encryptedPublicKeyBytes = bytesEncryptor.encrypt(publicKey.getEncoded());
        byte[] encryptedPrivateKeyBytes = bytesEncryptor.encrypt(privateKey.getEncoded());

        PersistentKeyPair persistentKeyPair = new PersistentKeyPair(encryptedPublicKeyBytes, encryptedPrivateKeyBytes);

        Outcome<KeyPairRepositoryErrorCodes> keySavingOutcome = keyPairRepository.create(persistentKeyPair);

        if (!keySavingOutcome.isSuccessful) {
            return Result.ofError(RSAKeyServiceErrorCodes.UNABLE_TO_CREATE_KEY);
        }

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        return Result.ofValue(rsaKey);
    }

}
