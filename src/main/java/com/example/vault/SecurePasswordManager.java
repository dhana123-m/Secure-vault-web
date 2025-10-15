package com.example.vault;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SecurePasswordManager {
    public static class Credential {
        public String site;
        public String username;
        public String password;
        public Credential(String site, String username, String password) {
            this.site = site; this.username = username; this.password = password;
        }
    }

    public static final String DEFAULT_ITERATIONS = null;

    private final List<Credential> creds = new ArrayList<>();

    public List<Credential> list() {
        return creds;
    }

    public void add(Credential c) {
        creds.add(c);
    }

    public void delete(int idx) {
        if (idx >= 0 && idx < creds.size()) creds.remove(idx);
    }

    public void load(File file, String masterPw) throws Exception { /* TODO */ }
    public void save(File file, String masterPw) throws Exception { /* TODO */ }
    public class VaultData {
    }
    public class VaultIO {

        public VaultIO(File vaultFile) {
            //TODO Auto-generated constructor stub
        }

        public void save(VaultData vd, char[] master, String defaultIterations) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'save'");
        }
    }
}
