package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ImageCacheDecode { // ba

	// Default memory cache size in kilobytes
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB

	// Default disk cache size in bytes
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

	// Compression settings when writing images to disk cache
	private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.PNG;
	private static final int DEFAULT_COMPRESS_QUALITY = 100/* 70 */;
	private static final int DISK_CACHE_INDEX = 0;

	// Constants to easily toggle various caches
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;

	private static final Bitmap.CompressFormat a = Bitmap.CompressFormat.PNG; // a
	private DiskLruCache mDiskLruCache; //b
	private LruCacheDecode<String, Bitmap> mMemoryCache; //c
	private ImageCacheParams imageCacheParams; // d
	private final Object mDiskCacheLock = new Object(); //e
	private boolean mDiskCacheInitializing = true; //f
	private HashMap<String, WeakReference<Bitmap>> mReusableBitmaps; //g

	private ImageCacheDecode(ImageCacheParams parama) {
		init(parama);
	}

	public static ImageCacheDecode getInstance(ImageCacheParams parama) { // a
		ImageCacheDecode localba = new ImageCacheDecode(parama);
		return localba;
	}

	private void init(ImageCacheParams parama) { //b
		this.imageCacheParams = parama;
		if (this.imageCacheParams.memoryCacheEnabled) {
			LogManager.writeLog("ImageCache",
					"Memory cache created (size = " + this.imageCacheParams.memCacheSize + ")", 111);
			if (Util.hasHoneycomb()) {
				this.mReusableBitmaps = new HashMap<>();
			}
			this.mMemoryCache = new LruCacheDecode<String, Bitmap> (this.imageCacheParams.memCacheSize) {
				protected void entryRemoved(boolean evicted,
						String key,
						Bitmap oldValue,
						Bitmap newValue) {
					// if ((bk.c()) && (ba.a(ba.this) != null)
					if ((Util.hasHoneycomb()) && (mReusableBitmaps != null)
							&& (oldValue != null)
							&& (!oldValue.isRecycled())) {
						// ba.a(ba.this).put(paramAnonymousString,
						mReusableBitmaps.put(key, new WeakReference(oldValue));
					}
				}

				protected int sizeOf(String key,
						Bitmap value) {
					int bitmapSize = ImageCacheDecode.getBitmapSize(value);
					return bitmapSize == 0 ? 1 : bitmapSize;
				}
			};
		}
		if (parama.initDiskCacheOnCreate) {
			initDiskCache();
		}
	}

	public void initDiskCache() { // a
		synchronized (this.mDiskCacheLock) {
			if ((this.mDiskLruCache == null) || (this.mDiskLruCache.isClosed())) {
				File localFile = this.imageCacheParams.diskCacheDir;
				if ((this.imageCacheParams.diskCacheEnabled) && (localFile != null)) {
					try {
						if (localFile.exists()) {
							cleanUp(localFile);
						}
						localFile.mkdir();
					} catch (Exception localException) {
					}
					if (getUsableSpace(localFile) > this.imageCacheParams.diskCacheSize) {
						try {
							this.mDiskLruCache = DiskLruCache.open(localFile, 1, 1,this.imageCacheParams.diskCacheSize);

							LogManager.writeLog("ImageCache", "Disk cache initialized", 111);
						} catch (IOException localIOException) {
							this.imageCacheParams.diskCacheDir = null;
							LogManager.writeLog("ImageCache", "initDiskCache - "
									+ localIOException, 112);
						}
					}
				}
			}
			this.mDiskCacheInitializing = false;
			this.mDiskCacheLock.notifyAll();
		}
	}

	private void cleanUp(File paramFile) throws IOException { //b
		File[] arrayOfFile1 = paramFile.listFiles();
		if (arrayOfFile1 == null) {
			throw new IOException("not a readable directory: " + paramFile);
		}
		for (File localFile : arrayOfFile1) {
			if (localFile.isDirectory()) {
				cleanUp(localFile);
			}
			if (!localFile.delete()) {
				throw new IOException("failed to delete file: " + localFile);
			}
		}
	}

	public void put(String key, Bitmap bitmap) { // a
		if ((key == null) || (bitmap == null)
				|| (bitmap.isRecycled())) {
			return;
		}
		if (this.mMemoryCache != null) {
			this.mMemoryCache.put(key, bitmap);
		}

		synchronized (this.mDiskCacheLock) {
			if (this.mDiskLruCache != null) {
				String str = hashKeyForDisk(key);
				OutputStream outputStream = null;
				try {
					DiskLruCache.Snapshot snapshot = this.mDiskLruCache.get(str);
					if (snapshot == null) {
						DiskLruCache.Editor editor = this.mDiskLruCache.edit(str);
						if (editor != null) {
							outputStream = editor.newOutputStream(0);
							bitmap.compress(this.imageCacheParams.compressFormat, this.imageCacheParams.compressQuality,
									outputStream);
//							writeDatasToFile(paramString,paramBitmap);
							editor.commit();
							outputStream.flush();
						}
					} else {
						snapshot.getInputStream(0).close();
					}
				} catch (IOException localIOException2) {
					LogManager.writeLog("ImageCache", "addBitmapToCache - "
							+ localIOException2, 112);
				} catch (Throwable localThrowable) {
					LogManager.writeLog("ImageCache", "addBitmapToCache - " + localThrowable,
							112);
				} finally {
					FileUtils.closeQuietly(outputStream);
				}
			}
		}
	}

	public Bitmap getBitmapFromMemCache(String cacheKey) { // a
		Bitmap localBitmap = null;
		if ((Util.hasHoneycomb()) && (this.mReusableBitmaps != null)) {
			WeakReference localWeakReference = this.mReusableBitmaps
					.get(cacheKey);
			if (localWeakReference != null) {
				localBitmap = (Bitmap) localWeakReference.get();
				if ((localBitmap == null) || (localBitmap.isRecycled())) {
					localBitmap = null;
				}
				this.mReusableBitmaps.remove(cacheKey);
			}
		}
		if ((localBitmap == null) && (this.mMemoryCache != null)) {
			localBitmap = this.mMemoryCache.get(cacheKey);
		}
		if ((localBitmap == null) || (localBitmap.isRecycled())) {
			return null;
		}
		LogManager.writeLog("ImageCache", "Memory cache hit", 111);

		return localBitmap;
	}

	public Bitmap getBitmapFromDiskCache(String paramString) { //b
		String key = hashKeyForDisk(paramString);
		Bitmap localBitmap = null;
		synchronized (this.mDiskCacheLock) {
			while (this.mDiskCacheInitializing) {
				try {
					this.mDiskCacheLock.wait();
				} catch (InterruptedException localInterruptedException) {
				}
			}
			if (this.mDiskLruCache != null) {
				InputStream inputStream = null;
				try {
					DiskLruCache.Snapshot snapshot = this.mDiskLruCache.get(key);
					if (snapshot != null) {
						LogManager.writeLog("ImageCache", "Disk cache hit", 111);

						inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
						if (inputStream != null) {
							FileDescriptor localFileDescriptor = ((FileInputStream) inputStream)
									.getFD();

							localBitmap = ImageResizerDecode.a(localFileDescriptor,
									Integer.MAX_VALUE, Integer.MAX_VALUE, this);
						}
					}
				} catch (IOException localIOException2) {
					LogManager.writeLog("ImageCache", "getBitmapFromDiskCache - "
							+ localIOException2, 112);
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (IOException localIOException4) {
					}
				}
			}
			return localBitmap;
		}
	}

	public void clearCache() { //b
		if ((Util.hasHoneycomb()) && (this.mReusableBitmaps != null)) {
			this.mReusableBitmaps.clear();
		}
		if (this.mMemoryCache != null) {
			this.mMemoryCache.evictAll();
			LogManager.writeLog("ImageCache", "Memory cache cleared", 111);
		}
		synchronized (this.mDiskCacheLock) {
			this.mDiskCacheInitializing = true;
			if ((this.mDiskLruCache != null) && (!this.mDiskLruCache.isClosed())) {
				try {
					this.mDiskLruCache.delete();
					LogManager.writeLog("ImageCache", "Disk cache cleared", 111);
				} catch (IOException localIOException) {
					LogManager.writeLog("ImageCache", "clearCache - " + localIOException, 112);
				}
				this.mDiskLruCache = null;
				initDiskCache();
			}
		}
	}

	public void flush() { //c
		synchronized (this.mDiskCacheLock) {
			if (this.mDiskLruCache != null) {
				try {
					this.mDiskLruCache.flush();
					LogManager.writeLog("ImageCache", "Disk cache flushed", 111);
				} catch (IOException localIOException) {
					LogManager.writeLog("ImageCache", "flush - " + localIOException, 112);
				}
			}
		}
	}

	public void close() { // d
		if ((Util.hasHoneycomb()) && (this.mReusableBitmaps != null)) {
			this.mReusableBitmaps.clear();
		}
		if (this.mMemoryCache != null) {
			this.mMemoryCache.evictAll();
			LogManager.writeLog("ImageCache", "Memory cache cleared", 111);
		}
		synchronized (this.mDiskCacheLock) {
			if (this.mDiskLruCache != null) {
				try {
					if (!this.mDiskLruCache.isClosed()) {
						this.mDiskLruCache.delete();
						this.mDiskLruCache = null;
						LogManager.writeLog("ImageCache", "Disk cache closed", 111);
					}
				} catch (IOException localIOException) {
					LogManager.writeLog("ImageCache", "close - " + localIOException, 112);
				}
			}
		}
	}

	public static class ImageCacheParams { // a
		public int memCacheSize = 5242880; // a
		public int diskCacheSize = 10485760; //b
		public File diskCacheDir; //c
		// public Bitmap.CompressFormat changeBearing = ba.f();
		public Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT; // d
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;// 100; //e
		public boolean memoryCacheEnabled = true; //f
		public boolean diskCacheEnabled = true; //g
		public boolean initDiskCacheOnCreate = false; //h

		public ImageCacheParams(Context paramContext, String paramString) { // a
			this.diskCacheDir = ImageCacheDecode.getDiskCacheDir(paramContext, paramString);
		}

		public void setMemCacheSize(int paramInt) { // a
			this.memCacheSize = paramInt;
		}

		public void setDiskCacheSize(int paramInt) { //b
			if (paramInt <= 0) {
				this.diskCacheEnabled = false;
			}
			this.diskCacheSize = paramInt;
		}

		public void setDiskCacheDir(String paramString) { // a
			this.diskCacheDir = new File(paramString);
		}

		public void enableMemoryCache(boolean paramBoolean) { // a
			this.memoryCacheEnabled = paramBoolean;
		}

		public void enableDiskCache(boolean paramBoolean) { //b
			this.diskCacheEnabled = paramBoolean;
		}
	}

	public static File getDiskCacheDir(Context paramContext, String paramString) { // a
		File localFile = getExternalCacheDir(paramContext);

		String cachePath = (("mounted".equals(Environment.getExternalStorageState())) || (!isExternalStorageRemovable()))	&& (localFile != null) ? localFile.getPath() : paramContext	.getCacheDir().getPath();
		LogManager.writeLog("ImageCache", "Disk cachePath: " + cachePath, 111);

		return new File(cachePath + File.separator + paramString);
	}

	public static String hashKeyForDisk(String paramString) { //c
		String str;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes("utf-8"));
			str = bytesToHexString(localMessageDigest.digest());
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			str = String.valueOf(paramString.hashCode());
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			str = String.valueOf(paramString.hashCode());
		}
		return str;
	}

	private static String bytesToHexString(byte[] paramArrayOfByte) { // a
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0; i < paramArrayOfByte.length; i++) {
			String str = Integer.toHexString(0xFF & paramArrayOfByte[i]);
			if (str.length() == 1) {
				localStringBuilder.append('0');
			}
			localStringBuilder.append(str);
		}
		return localStringBuilder.toString();
	}

	public static int getBitmapSize(Bitmap paramBitmap) { // a
		Bitmap localBitmap = paramBitmap;
		if (Util.hasHoneycombMR1()) {
			return localBitmap.getByteCount();
		}
		return localBitmap.getRowBytes() * localBitmap.getHeight();
	}

	public static boolean isExternalStorageRemovable() { //e
		if (Util.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	public static File getExternalCacheDir(Context paramContext) { // a
		if (Util.hasFroyo()) {//android 版本大于8
			return paramContext.getExternalCacheDir();
		}
		String str = "/Android/data/" + paramContext.getPackageName()
				+ "/cache/";

		return new File(Environment.getExternalStorageDirectory().getPath()
				+ str);
	}

	public static long getUsableSpace(File paramFile) { // a
		if (Util.hasGingerbread()) {
			return paramFile.getUsableSpace();
		}
		StatFs localStatFs = new StatFs(paramFile.getPath());
		return (long)localStatFs.getBlockSize() * (long)localStatFs.getAvailableBlocks();
	}
}
