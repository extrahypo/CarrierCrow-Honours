package matt.honours.cryptography.EncryptDecrypt;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import matt.honours.cryptography.keys.KeyConversion;
import org.spongycastle.jce.spec.IEKeySpec;
import org.spongycastle.jce.spec.IESParameterSpec;

import android.util.Base64;
import android.util.Log;

public class EncryptDecrypt {
	
	public static String encryptWithPublic(PublicKey keyPub, byte[] key){
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, keyPub);
			byte[] bytes = cipher.doFinal(key);
			return Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}		
	}
	
	public static String encryptWithPublic(PublicKey key, String stringUserKey) {
		byte[] bytes = KeyConversion.stringToSecretKey(stringUserKey).getEncoded();
		return encryptWithPublic(key, bytes);
	}
	
	public static String decryptWithPrivate(PrivateKey keyPriv, String key){
		try{
			byte[] bytes = Base64.decode(key, Base64.DEFAULT);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, keyPriv);
			bytes = cipher.doFinal(bytes);
			return Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	public static String encryptWithECC(IEKeySpec spec1, String text, IESParameterSpec spec){
		try{
			Cipher cipher = Cipher.getInstance("ECIES", "SC");

			cipher.init(Cipher.ENCRYPT_MODE, spec1, spec);

			return Base64.encodeToString(cipher.doFinal(text.getBytes(),  0, text.getBytes().length),
					Base64.DEFAULT);
		} catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

	public static String decryptWithECC(IEKeySpec spec2, String text, IESParameterSpec spec){
		try{
			Cipher cipher = Cipher.getInstance("ECIES", "SC");	
			byte[] bytes = Base64.decode(text, Base64.DEFAULT);			
			
			cipher.init(Cipher.DECRYPT_MODE, spec2, spec);
			
			return new String(cipher.doFinal(bytes, 0, bytes.length));
		} catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

	public static String encryptWithECC(IEKeySpec spec1, String text, byte[] d,
			byte[] e) {
		try{
			Cipher cipher = Cipher.getInstance("ECIES", "SC");
			IESParameterSpec spec = new IESParameterSpec(d, e, 128);
			cipher.init(Cipher.ENCRYPT_MODE, spec1, spec);

			return Base64.encodeToString(cipher.doFinal(text.getBytes(),  0, text.getBytes().length),
					Base64.DEFAULT);
		} catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
	}

	public static String decryptWithECC(IEKeySpec spec2, String text,
			byte[] d, byte[] e) {
		try{
			Cipher cipher = Cipher.getInstance("ECIES", "SC");	
			byte[] bytes = Base64.decode(text, Base64.DEFAULT);			
			IESParameterSpec spec = new IESParameterSpec(d, e, 128);
			cipher.init(Cipher.DECRYPT_MODE, spec2, spec);
			
			return new String(cipher.doFinal(bytes, 0, bytes.length));
		} catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
	}

	public static String encryptString(String text, SecretKey key,
			IvParameterSpec iv) {
		try{			
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        aesCipher.init(Cipher.ENCRYPT_MODE, key, iv);
	        
	        byte[] toEncrypt = text.getBytes();
	        byte[] cipherText = aesCipher.doFinal(toEncrypt);
	        return Base64.encodeToString(cipherText, Base64.DEFAULT);	        
			
		} catch(Exception e){
			Log.e("Encryption Error", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static String decryptString(String text, SecretKey key,
			IvParameterSpec iv) {
		try{
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] b = Base64.decode(text, Base64.DEFAULT);
			byte[] t = aesCipher.doFinal(b);
			return new String(t);
		} catch(Exception e){
			Log.e("Decryption Error", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static String encryptString(String aesKey, SecretKey conKey,
			String ivString) {
		byte[] bytes = Base64.decode(ivString, Base64.DEFAULT);
		return encryptString(aesKey, conKey, new IvParameterSpec(bytes));
	}
	
	public static String decryptString(String text, SecretKey key,
			String ivString) {
		byte[] bytes = Base64.decode(ivString, Base64.DEFAULT);
		return decryptString(text, key, new IvParameterSpec(bytes));
	}
}
