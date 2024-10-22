package chapter6.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * 暗号化ユーティリティー
 */
public class CipherUtil {

	/**
	 *
	 * SHA-256で暗号化。暗号化した文字列はバイト配列(byte[])に変換されます。
	 * バイト配列よりは文字列の方が扱いやすいので、 Base64でエンコード。
	 *
	 * @param target
	 *            暗号化対象の文字列
	 *
	 * @return 暗号化された文字列
	 */
	public static String encrypt(String target) {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(target.getBytes());
			return Base64.encodeBase64URLSafeString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
