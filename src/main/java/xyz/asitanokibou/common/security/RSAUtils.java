package xyz.asitanokibou.common.security;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//来源:
//[1](https://www.cnblogs.com/leeego-123/p/10450339.html)
//    https://www.cnblogs.com/coloz/p/10915179.html
//[2](https://segmentfault.com/a/1190000011263680 , https://github.com/liamylian/x-rsa)

//还可以看下 netty-handler : Pem开头的类 PemReader,PemPrivateKey
public class RSAUtils {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    public static final String PUBLIC_KEY_NAME = "publicKey";
    public static final String PRIVATE_KEY_NAME = "privateKey";


    private static final Pattern PUBLIC_KEY_PATTERN = Pattern.compile(
            "-+BEGIN\\s+.*PUBLIC\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+" + // Header
                    "([a-z0-9+/=\\r\\n]+)" +                       // Base64 text
                    "-+END\\s+.*PUBLIC\\s+KEY[^-]*-+",            // Footer
            Pattern.CASE_INSENSITIVE);


    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile(
            "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+" + // Header
                    "([a-z0-9+/=\\r\\n]+)" +                       // Base64 text
                    "-+END\\s+.*PRIVATE\\s+KEY[^-]*-+",            // Footer
            Pattern.CASE_INSENSITIVE);

    public static Map<String, String> createKeys(int keySize) {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        // map装载公钥和私钥
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put(PUBLIC_KEY_NAME, publicKeyStr);
        keyPairMap.put(PRIVATE_KEY_NAME, privateKeyStr);
        // 返回map
        return keyPairMap;
    }

    public static RSAPublicKey generatePublicKeyFromPrivateKey(PrivateKey privateKey) throws InvalidKeySpecException {
        RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)privateKey;

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateCrtKey.getModulus(),
                rsaPrivateCrtKey.getPublicExponent());

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }
        PublicKey myPublicKey = keyFactory.generatePublic(publicKeySpec);
        return (RSAPublicKey) myPublicKey;
    }

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过X509编码的Key指令获得公钥对象
        Matcher matcher = PUBLIC_KEY_PATTERN.matcher(publicKey);

        String key = publicKey;
        if (matcher.find()) { //pem
            key = publicKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "");
        }

        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 得到私钥 der
     *
     * @param privateKey 密钥字符串（经过base64编码）
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        String key = privateKey;

        Matcher matcher = PRIVATE_KEY_PATTERN.matcher(privateKey);
        if (matcher.find()) {
            key = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");

        }

        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 公钥加密
     *
     * @param data      明文
     * @param publicKey 公钥
     * @return 密文
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        return doEncrypt(data, publicKey);
    }

    /**
     * 私钥加密
     *
     * @param data       明文
     * @param privateKey 私钥
     * @return -
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        return doEncrypt(data, privateKey);
    }

    public static String privateEncrypt(String data, String pemPrivateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return privateEncrypt(data, getPrivateKey(pemPrivateKey));
    }


    public static <T extends RSAKey & Key> String doEncrypt(String data, T key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            //每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] codec = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), key.getModulus().bitLength());

            return Base64.encodeBase64URLSafeString(codec);
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    public static <T extends RSAKey & Key> String doDecrypt(String data, T key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] codec = rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), key.getModulus().bitLength());

            return new String(codec, CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param data       密文
     * @param privateKey 私钥
     * @return 明文
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        return doDecrypt(data, privateKey);
    }

    /**
     * 公钥解密
     *
     * @param data      明文
     * @param publicKey 公钥
     * @return 密文
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        return doDecrypt(data, publicKey);
    }

    public static String publicDecrypt(String data, String pemPublicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return doDecrypt(data, getPublicKey(pemPublicKey));
    }



    //rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据

    /**
     * 分段加密/解密
     *
     * @param cipher   cipher
     * @param opmode   Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param dataByte opmode:ENCRYPT_MODE 为加密数据 , opmode:DECRYPT_MODE为解密数据
     * @param keySize  key的大小
     * @return 密文/明文的字节数组
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] dataByte, int keySize) {
        int maxBlock;  //最大块
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }

        int offSet = 0;
        byte[] buff;
        int i = 0;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try {
                while (dataByte.length > offSet) {
                    if (dataByte.length - offSet > maxBlock) {
                        //可以调用以下的doFinal（）方法完成加密或解密数据：
                        buff = cipher.doFinal(dataByte, offSet, maxBlock);
                    } else {
                        buff = cipher.doFinal(dataByte, offSet, dataByte.length - offSet);
                    }
                    out.write(buff, 0, buff.length);
                    i++;
                    offSet = i * maxBlock;
                }
            } catch (Exception e) {
                throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> keyMap = createKeys(1024);
        String publicKey = keyMap.get("publicKey");
        String privateKey = keyMap.get("privateKey");
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("公钥加密——私钥解密");
        String str = "站在大明门前守卫的禁卫军，事先没有接到\n" + "有关的命令，但看到大批盛装的官员来临，也就\n" + "以为确系举行大典，因而未加询问。进大明门即\n" + "为皇城。文武百官看到端门午门之前气氛平静，\n" + "城楼上下也无朝会的迹象，既无几案，站队点名\n" + "的御史和御前侍卫“大汉将军”也不见踪影，不免\n"
                + "心中揣测，互相询问：所谓午朝是否讹传？";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);

        publicEncryptPrivateDecrypt(publicKey, privateKey, str);

        privateEncryptPublicDecrypt(publicKey, privateKey, str);

    }

    private static void publicEncryptPrivateDecrypt(String publicKey, String privateKey, String str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("===publicEncryptPrivateDecrypt===");
        String encodedData = publicEncrypt(str, getPublicKey(publicKey));  //传入明文和公钥加密,得到密文
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = privateDecrypt(encodedData, getPrivateKey(privateKey)); //传入密文和私钥,得到明文
        System.out.println("解密后文字: \r\n" + decodedData);
    }

    private static void privateEncryptPublicDecrypt(String publicKey, String privateKey, String str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("===privateEncryptPublicDecrypt===");
        String encodedData = privateEncrypt(str, getPrivateKey(privateKey));  //传入明文和公钥加密,得到密文
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = publicDecrypt(encodedData, getPublicKey(publicKey)); //传入密文和私钥,得到明文
        System.out.println("解密后文字: \r\n" + decodedData);
    }

}
