package com.op.iplay.service;

interface IKeyMappingService {
    /**
     * <p>Query the download URL of key-mapping file for specified package, 
     * All parameters are required and MUST match the value in AndroidManifest.xml.</p>
     *
     * <p>Will return <code>null</code> if key-mapping file is not found.</p>
     * @param packageName <code>package</code> from AndroidManifest.xml
     * @param versionCode <code>versionCode</code> from AndroidManifest.xml
     * @param versionName <code>versionName</code> from AndroidManifest.xml
     */
    String getMappingFileUrlForPackage(String packageName, int versionCode, String versionName);
}