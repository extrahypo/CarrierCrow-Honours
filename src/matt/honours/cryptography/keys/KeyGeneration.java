package matt.honours.cryptography.keys;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECParameterSpec;
import android.util.Log;


public class KeyGeneration {
	
	public static SecretKey getNewKeys(){
		return getKey();
	}

	private static SecretKeySpec getKey() {
		try{

	        KeyGenerator keygen = KeyGenerator.getInstance("AES");
	        keygen.init(128, new SecureRandom());  
	        byte[] key = keygen.generateKey().getEncoded();
	        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
	        
	        return skeySpec;
		} catch(Exception e){
			Log.e("Key Generation Error", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static KeyPair getPublicPrivateKeys(){
		try {
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			return keyGen.generateKeyPair();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static KeyPair getECCKeys(){
		try{			
			Security.addProvider(new BouncyCastleProvider());

			KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "SC");
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");
			g.initialize(ecSpec, new SecureRandom());

			return g.generateKeyPair();
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
}
