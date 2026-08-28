package com.example.ni.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SafeSQLValidator {
    
    private static final Set<String> DANGEROUS_KEYWORDS = new HashSet<>(Arrays.asList(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "TRUNCATE", 
            "GRANT", "REVOKE", "CREATE", "EXEC", "EXECUTE"
    ));
    
    private static final Set<String> ALLOWED_OPERATIONS = new HashSet<>(Arrays.asList(
            "SELECT"
    ));
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "('(''|[^'])*')|(;)|(\b(OR|AND)\b.*=.*--)|(/\\*.*\\*/)",
            Pattern.CASE_INSENSITIVE
    );
    
    private static final int MAX_QUERY_LENGTH = 1000;
    
    public ValidationResult validateSQL(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new ValidationResult(false, "SQL query is empty");
        }
        
        String upperSQL = sql.toUpperCase().trim();
        
        if (upperSQL.length() > MAX_QUERY_LENGTH) {
            return new ValidationResult(false, "SQL query too long (max " + MAX_QUERY_LENGTH + " characters)");
        }
        
        if (!upperSQL.startsWith("SELECT")) {
            return new ValidationResult(false, "Only SELECT queries are allowed");
        }
        
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperSQL.contains(keyword)) {
                return new ValidationResult(false, "Dangerous keyword detected: " + keyword);
            }
        }
        
        if (SQL_INJECTION_PATTERN.matcher(sql).find()) {
            return new ValidationResult(false, "Potential SQL injection pattern detected");
        }
        
        if (!upperSQL.contains("LIMIT") && !upperSQL.contains("limit")) {
            log.warn("SQL query without LIMIT detected, adding default LIMIT 100");
            return new ValidationResult(true, addLimitClause(sql, 100));
        }
        
        return new ValidationResult(true, sql);
    }
    
    private String addLimitClause(String sql, int limit) {
        String trimmed = sql.trim();
        if (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + " LIMIT " + limit + ";";
    }
    
    public record ValidationResult(boolean isValid, String resultOrMessage) {}
}
