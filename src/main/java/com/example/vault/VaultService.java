package com.example.vault;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class VaultService {

    private final File vaultFile;
    private SecurePasswordManager manager;

    public VaultService() {
        String path = System.getProperty("vault.path", "vault.spm");
        this.vaultFile = new File(path);
        this.manager = new SecurePasswordManager();
    }

    public boolean vaultExists() {
        return this.vaultFile.exists();
    }

    public void createEmptyVault(char[] master) throws Exception {
        // for stub: do nothing, just ensure file exists
        if (!vaultFile.exists()) vaultFile.createNewFile();
    }

    public boolean unlock(char[] master) {
        // stub version: always unlock successfully
        return true;
    }

    public void lock() {
        // nothing to do in stub
    }

    public List<SecurePasswordManager.Credential> list() {
        return manager == null ? Collections.emptyList() : manager.list();
    }

    public void add(String site, String username, String password) {
        manager.add(new SecurePasswordManager.Credential(site, username, password));
    }

    public SecurePasswordManager.Credential get(int idx) {
        return manager.list().get(idx);
    }

    public void delete(int idx) {
        manager.delete(idx);
    }

    public void save() throws Exception {
        // stub version: do nothing
        manager.save(vaultFile, "master"); // optional no-op
    }
}
