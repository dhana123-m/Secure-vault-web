package com.example.vault;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VaultService {

    private final File csvFile;
    private SecurePasswordManager manager;

    public VaultService() {
        String path = System.getProperty("vault.path", "vault.csv");
        this.csvFile = new File(path);
        this.manager = new SecurePasswordManager();
        loadFromCSV();
    }

    public boolean vaultExists() {
        return this.csvFile.exists();
    }

    public void createEmptyVault(char[] master) throws Exception {
        if (!csvFile.exists()) csvFile.createNewFile();
    }

    public boolean unlock(char[] master) {
        return true;
    }

    public void lock() {}

    public List<SecurePasswordManager.Credential> list() {
        return manager == null ? Collections.emptyList() : manager.list();
    }

    public void add(String site, String username, String password) {
        manager.add(new SecurePasswordManager.Credential(site, username, password));
        saveToCSV();
    }

    public SecurePasswordManager.Credential get(int idx) {
        List<SecurePasswordManager.Credential> all = manager.list();
        return (idx >= 0 && idx < all.size()) ? all.get(idx) : null;
    }

    public void delete(int idx) {
        if (manager != null) {
            manager.delete(idx);
            saveToCSV();
        }
    }

    public void update(int idx, String site, String username, String password) {
        if (manager == null) return;
        List<SecurePasswordManager.Credential> all = manager.list();
        if (idx >= 0 && idx < all.size()) {
            SecurePasswordManager.Credential c = all.get(idx);
            if (site != null) c.site = site;
            if (username != null) c.username = username;
            if (password != null) c.password = password;
            saveToCSV();
        }
    }

    public List<SecurePasswordManager.Credential> search(String query) {
        if (manager == null || query == null || query.trim().isEmpty()) {
            return list();
        }

        String lower = query.toLowerCase();
        return manager.list().stream()
                .filter(c ->
                        (c.site != null && c.site.toLowerCase().contains(lower)) ||
                        (c.username != null && c.username.toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("site,username,password");
            for (SecurePasswordManager.Credential c : manager.list()) {
                pw.printf("%s,%s,%s%n",
                        escape(c.site), escape(c.username), escape(c.password));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        if (!csvFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            manager.list().clear();
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = parseCSV(line);
                if (parts.length >= 3) {
                    manager.add(new SecurePasswordManager.Credential(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private String[] parseCSV(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char ch : line.toCharArray()) {
            if (ch == '\"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    public void save() {
        saveToCSV();
    }
}
