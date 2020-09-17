package com.sfmap.api.mapcore.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public final class FileUtils
{
  public static final Charset US_ASCII = Charset.forName("US-ASCII");
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  
  static void deleteContents(File paramFile) // a
    throws IOException
  {
    File[] arrayOfFile1 = paramFile.listFiles();
    if (arrayOfFile1 == null) {
      throw new IOException("not a readable directory: " + paramFile);
    }
    for (File localFile : arrayOfFile1)
    {
      if (localFile.isDirectory()) {
        deleteContents(localFile);
      }
      if (!localFile.delete()) {
        throw new IOException("failed to delete file: " + localFile);
      }
    }
  }

  static void closeQuietly(Closeable closeable) // a
  {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (RuntimeException localRuntimeException) {
        throw localRuntimeException;
      } catch (Exception localException) {
      }
    }
  }
}
