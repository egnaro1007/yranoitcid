package com.yranoitcid.Backend.Database;

import java.sql.SQLException;
import org.sqlite.Function;

public class LevenshteinFunction extends Function {

    @Override
    protected void xFunc() throws SQLException {
        if (args() != 2) {
            throw new SQLException("Levenshtein function requires two arguments");
        }

        String s1 = value_text(0);
        String s2 = value_text(1);

        int distance = levenshteinDistance(s1, s2);

        result(distance);
    }

    public int levenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[m][n];
    }
}
