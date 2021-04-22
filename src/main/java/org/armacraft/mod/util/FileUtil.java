package org.armacraft.mod.util;

import java.io.File;
import java.io.IOException;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class FileUtil {
	public static String getHash(File file) {
		try {
			return Files.hash(file, Hashing.md5()).toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
