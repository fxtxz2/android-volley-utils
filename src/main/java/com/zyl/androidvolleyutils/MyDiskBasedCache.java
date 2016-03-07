package com.zyl.androidvolleyutils;

import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

/**
 * DiskBasedCache provides a one-file-per-response cache with an in-memory index
 * @author zyl
 *
 */
public class MyDiskBasedCache extends DiskBasedCache {

	/**
	 * 缓存根目录
	 */
	private File rootDirectory;
	public MyDiskBasedCache(File rootDirectory, int maxCacheSizeInBytes) {
		super(rootDirectory, maxCacheSizeInBytes);
		this.rootDirectory = rootDirectory;
	}

	public MyDiskBasedCache(File rootDirectory) {
		super(rootDirectory);
		this.rootDirectory = rootDirectory;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}
}
