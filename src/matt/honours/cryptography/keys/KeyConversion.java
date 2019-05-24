package matt.honours.cryptography.keys;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class KeyConversion {

	public static PublicKey stringToPublicKey(String s){
		try{
			byte[] newKey = Base64.decode(s, Base64.DEFAULT);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(newKey);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    return fact.generatePublic(spec);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String publicKeyToString(PublicKey key){
		return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
	}
	
	public static PrivateKey stringToPrivateKey(String s){
		try{
			byte[] newKey = Base64.decode(s, Base64.DEFAULT);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(newKey);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			return fact.generatePrivate(keySpec);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String privateKeyToString(PrivateKey key){
		return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
	}
	
	public static SecretKey stringToSecretKey(String s){
		try{
			byte[] newKey = Base64.decode(s, Base64.DEFAULT);
			return new SecretKeySpec(newKey, 0, newKey.length, "AES");
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String secretKeyToString(SecretKey key){
		return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
	}
	
	public static PrivateKey stringToPrivateKeyECC(String key){
		try{
			byte[] bytes = Base64.decode(key, Base64.DEFAULT);
			KeyFactory factory = KeyFactory.getInstance("EC", "SC");
			/*ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");
			ECPrivateKeySpec privSpec = new ECPrivateKeySpec(new BigInteger(1,bytes), ecSpec);*/
			PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
			return factory.generatePrivate(privSpec);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static PublicKey stringToPublicKeyECC(String key){
		try{
			final KeyFactory factory = KeyFactory.getInstance("EC", "SC");
			byte[] bytes = Base64.decode(key, Base64.DEFAULT);			
			/*ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");
			ECPoint point = ecSpec.getCurve().decodePoint(bytes);
			ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
			return factory.generatePublic(pubSpec);*/
			final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes);
	        final PublicKey pk1 = factory.generatePublic(publicKeySpec);
			return pk1;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
