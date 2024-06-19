package com.researchspace.model.permissions;

import static org.apache.commons.lang.RandomStringUtils.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.junit.jupiter.api.Test;

public class SymmetricTextEncryptorTest {

	// this is to ensure backwards compatibility with existing encyrpted secrets, that we encode/decode
	// in the same way over time. 
	final String secretMessage_Shiro1_5_0 = "aaaaaaaaaaaaaaaabbbbb";
	final String expectedEncryptedSecretMessage_Shiro1_5_0="3WZjkGKDNz1Z6Bss7MSJ/2hCjuMLY2S0wUULmRq1y1o=";
	
	@Test
	public void encryptDecryptRoundTrip() {
		String keyString = createNewEncryptionKey();
			
		TextEncryptor encryptor = new  SymmetricTextEncryptor(keyString);

		String encrypted = encryptor.encrypt(secretMessage_Shiro1_5_0);
		assertEquals(expectedEncryptedSecretMessage_Shiro1_5_0, expectedEncryptedSecretMessage_Shiro1_5_0);
		assertEquals(secretMessage_Shiro1_5_0, encryptor.decrypt(encrypted));
		
		String encrypted2 =encryptor.encrypt(secretMessage_Shiro1_5_0);
		encrypted =encryptor.encrypt(secretMessage_Shiro1_5_0);
		assertEquals(secretMessage_Shiro1_5_0, encryptor.decrypt(encrypted2));
	}
	
	@Test
	public void createFromAnyString() {
		TextEncryptor encryptor =   SymmetricTextEncryptor.createFromAnyString("abcde");
		String encrypted =encryptor.encrypt(secretMessage_Shiro1_5_0);
		assertEquals(secretMessage_Shiro1_5_0, encryptor.decrypt(encrypted));
	}
	
	@Test
	public void createFromAnyStringConstraints() {
		
		assertThrows(IllegalArgumentException.class, ()->SymmetricTextEncryptor.createFromAnyString(""));
		assertThrows(IllegalArgumentException.class, ()->SymmetricTextEncryptor.createFromAnyString(null));
		assertThrows(IllegalArgumentException.class, ()->SymmetricTextEncryptor.createFromAnyString(random(256)));
		
	}

	private String createNewEncryptionKey() {
		return Base64.encodeToString(new AesCipherService().generateNewKey().getEncoded());
	}

}
