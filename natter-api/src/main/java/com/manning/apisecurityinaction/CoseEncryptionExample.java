package com.manning.apisecurityinaction;

import COSE.*;
import com.manning.apisecurityinaction.token.Base64url;
import com.upokecenter.cbor.CBORObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class CoseEncryptionExample {
    private static final SecureRandom random = new SecureRandom();

    public static void main(String... args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        var keyMaterial = PskServer.loadPsk("changeit".toCharArray());

        var recipient = new Recipient();
        var keyData = CBORObject.NewMap()
                .Add(KeyKeys.KeyType.AsCBOR(), KeyKeys.KeyType_Octet)
                .Add(KeyKeys.Octet_K.AsCBOR(), keyMaterial);
        recipient.SetKey(new OneKey(keyData));
        recipient.addAttribute(HeaderKeys.Algorithm,
                AlgorithmID.HKDF_HMAC_SHA_256.AsCBOR(),
                Attribute.PROTECTED);
        var nonce = new byte[16];
        random.nextBytes(nonce);
        recipient.addAttribute(HeaderKeys.HKDF_Context_PartyU_nonce,
                CBORObject.FromObject(nonce), Attribute.PROTECTED);

        var message = new EncryptMessage();
        message.SetContent("Hello, World!");
        message.addAttribute(HeaderKeys.Algorithm,
                AlgorithmID.AES_CCM_16_128_128.AsCBOR(),
                Attribute.PROTECTED);
        message.addRecipient(recipient);

        message.encrypt();
        System.out.println(Base64url.encode(message.EncodeToBytes()));
        System.out.println(message.EncodeToCBORObject());
    }
}