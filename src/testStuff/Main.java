package testStuff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class Main {
	
	private static PublicKey _publicKey;
	private static PrivateKey _privateKey;
	
	private static String certPath = "C:\\Users\\Abreu\\Documents\\GitHub\\CandorSigner\\selfSigned.cer";
	
	private String pub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw7y/wBEixipwNlmTxXuA2AkQdlKNd8RW51jajJ+lHggAXQRuw8tKGfk71xVd7BOrVz6v0m4YK+9Kedd6vhUcmtEcFIqNbCx4mallPQtD3xk/KktXVjR4SkmG81bv7mlhg1uPkhxrcoK9a2xwTM2cLhgDv8yP8ft76W980TSlBLBdm5yw9YzJIKCrb2xI8tfFWD87cdkDeeIIAhI3DL8ORC/1Zt+cuii8kj3wMvXWJtOhKrwC7PR0IsKIjd4dVIEW730GI1lWO+NJgxu/9ogw9pPbF3aUnyYfE9Ls5Q+6Zz2Qfnf/dZUbwiIN971DxFFtAw8O7wedmhK8fP9fCcvW+wIDAQAB";
	private static String sig = "JnR8ATDov2BMwgD+XclTaqOwtKMaY2SFNSkeNcCnmHf/yHKI+Ma0ywkRCdkGgPn/Ybs+zCdW7xQWV8rzABRidaE4Vae6zQ0bUKPMOEKO6vvwcOn3DNXCDQEvARL5BGubIaDEYNc1UKjzQug9/p8fWUoyCCifUb89TJyvE70OAZF4mv5BOjNt/3I9gTY+9PDbjDiri/F+fIf4DoDpipheu4xPkQv6YKhDM2IUSirKKWma4ezv12kN0L2MzT3r131aIAwVGM1nVAuALtPck+7OTpa3edoeQGyI4+FSmYOoHIt/BhW2TKMqkVgvdNmXrGSVrt5w/1wyDWknlex4G0UYCw==";
	
	public static void main(String[] args) throws Exception {
		Main app = new Main();
		_publicKey = app.getPubKeyFromFile();
		_privateKey = app.getPrivKeyFromFile();
		
		PublicKey k = app.getPubKeyFromCert(certPath);
		System.out.println(Base64.getEncoder().encodeToString(k.getEncoded()));
		
		String data = "welcome";
		byte[] signature = app.toDecodedBase64ByteArray(sig.getBytes());
		
		//String pubKeyStr = Base64.getEncoder().encodeToString(_publicKey.getEncoded());
		
		//byte[] signature = app.makeSignature(data.getBytes());
		//String signatureStr = Base64.getEncoder().encodeToString(signature);
		
		if(app.checkSignature(data.getBytes(), signature, _publicKey)) {
			System.out.println("valid");
		}
		else {
			System.out.println("invalid");
		}
	}
	
	private PublicKey getPubKeyFromCert(String pathToCert) throws CertificateException, FileNotFoundException {
		FileInputStream fin = new FileInputStream(pathToCert);
		CertificateFactory f = CertificateFactory.getInstance("X.509");
		Certificate certificate = f.generateCertificate(fin);
		PublicKey pk = certificate.getPublicKey();
		return pk;
	}

	private java.security.PrivateKey getPrivKeyFromFile() {
		PrivateKey Key = null;
		try {
			String KeyStr = readFromFile("priv.txt");
			byte[] KeyBytes = toDecodedBase64ByteArray(KeyStr.getBytes());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			KeySpec KeySpec = new PKCS8EncodedKeySpec(KeyBytes);
			Key = keyFactory.generatePrivate(KeySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return Key;
	}

	private java.security.PublicKey getPubKeyFromFile() {

		PublicKey Key = null;
		try {
			String KeyStr = readFromFile("pub.txt");
			byte[] KeyBytes = toDecodedBase64ByteArray(KeyStr.getBytes());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			KeySpec KeySpec = new X509EncodedKeySpec(KeyBytes);
			Key = keyFactory.generatePublic(KeySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return Key;

	}

	public void generateKeyPair() {
		KeyPairGenerator keygen;
		try {
			keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(2048);
			KeyPair keypair = keygen.generateKeyPair();
			_publicKey = keypair.getPublic();
			_privateKey = keypair.getPrivate();

			storeKeys(_privateKey, _publicKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private void storeKeys(PrivateKey priv, PublicKey pub) {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream("priv.txt"), "utf-8");
			writer = new BufferedWriter(writer);
			writer.write(Base64.getEncoder().encodeToString(priv.getEncoded()));
			writer.close();

			writer = new OutputStreamWriter(new FileOutputStream("pub.txt"), "utf-8");
			writer = new BufferedWriter(writer);
			writer.write(Base64.getEncoder().encodeToString(pub.getEncoded()));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private byte[] toDecodedBase64ByteArray(byte[] base64EncodedByteArray) {
		return DatatypeConverter.parseBase64Binary(new String(base64EncodedByteArray, Charset.forName("UTF-8")));
	}
	
	public String readFromFile(String path) {
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return everything;
	}
	
	public byte[] makeSignature(byte[] data) {
		return encrypt(getHash(data), _privateKey);
	}

	private byte[] encrypt(byte[] buffer, java.security.Key key) {
		Cipher rsa;
		try {
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, key);
			return rsa.doFinal(buffer);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] getHash(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		byte[] digest = md.digest();
		return digest;
	}

	private byte[] getBytesFromKey(java.security.Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded()).getBytes();
	}

	public boolean checkHash(byte[] content, byte[] id) {
		return Arrays.equals(getHash(content), id);
	}
	
	private boolean checkSignature(byte[] data, byte[] signature, java.security.PublicKey public_key) {
		byte[] digest = getHash(data);
		
		byte[] decrypted = decrypt(signature, public_key);
		
		if(Arrays.equals(digest,decrypted)){
			return true;
		}
		return false;
	}
	
	private byte[] decrypt(byte[] buffer, java.security.PublicKey key) {
		try {
			Cipher rsa;
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.DECRYPT_MODE, key);
			return rsa.doFinal(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
