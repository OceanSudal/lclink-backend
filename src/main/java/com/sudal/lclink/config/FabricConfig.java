package com.sudal.lclink.config;

import org.hyperledger.fabric.gateway.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
public class FabricConfig {

    @Value("${fabric.connection.profile}")
    private Resource connectionProfile;

    @Value("${fabric.crypto.path}")
    private String cryptoPath;

    @Value("${fabric.identity.label}")
    private String identityLabel;

    @Value("${fabric.msp.id}")
    private String mspId;

    @Bean
    public Gateway gateway() throws Exception {
        Wallet wallet = createWallet();

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, identityLabel)
                .networkConfig(connectionProfile.getInputStream())
                .discovery(false);

        return builder.connect();
    }

    private Wallet createWallet() throws IOException, CertificateException, InvalidKeyException {
        Wallet wallet = Wallets.newInMemoryWallet();

        // 인증서와 개인키 경로
        Path certPath = Paths.get(cryptoPath, "signcerts/User1@shipper.lclink.com-cert.pem");
        Path keyDirPath = Paths.get(cryptoPath, "keystore");

        // 인증서 읽기
        X509Certificate certificate = readX509Certificate(certPath);

        // 개인키 읽기
        PrivateKey privateKey = getPrivateKey(keyDirPath);

        // Identity 생성
        Identity identity = Identities.newX509Identity(mspId, certificate, privateKey);

        // Wallet에 추가
        wallet.put(identityLabel, identity);

        return wallet;
    }

    private X509Certificate readX509Certificate(Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private PrivateKey getPrivateKey(Path keyDirPath) throws IOException, InvalidKeyException {
        // keystore 디렉토리의 첫 번째 파일 읽기 (priv_sk)
        Path keyPath = Files.list(keyDirPath).findFirst()
                .orElseThrow(() -> new IOException("No private key found in: " + keyDirPath));

        try (Reader keyReader = Files.newBufferedReader(keyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(keyReader);
        }
    }
}