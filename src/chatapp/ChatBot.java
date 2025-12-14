package chatapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatBot {

    private static final String BOT_NAME = "ChatBot";

    // Knowledge & memory
    private static Map<String, String> knowledgeBase = new HashMap<>();
    private static Map<String, List<String>> userHistory = new HashMap<>();
    private static Map<String, String> lastBotResponse = new HashMap<>();

    // Analytics
    private static List<String> chatHistory = new ArrayList<>();
    private static Map<String, Integer> userMessageCount = new HashMap<>();
    private static Map<String, Integer> keywordFrequency = new HashMap<>();

    // Time formatter 
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm:ss a");

    // Simple intent patterns
    private static Map<String, String> patterns = Map.of(
            "hi", "Hello! How can I help you today?",
            "hello", "Hi there! Ask me anything ",
            "bye", "Goodbye! Have a nice day ",
            "thanks", "You're welcome! "
    );

    public static Message reply(Message msg) {

        String user = msg.sender;
        String text = preprocess(msg.text);

        logMessage(user, text);

        /* ---------- PATTERN MATCH ---------- */
        for (String key : patterns.keySet()) {
            if (text.contains(key)) {
                remember(user, patterns.get(key));
                return new Message(BOT_NAME, patterns.get(key));
            }
        }

        /* ---------- STATS ---------- */
        if (text.equals("stats")) {
            String stats = generateStats();
            remember(user, stats);
            return new Message(BOT_NAME, stats);
        }

        /* ---------- LEARNING ---------- */
        if (text.startsWith("learn:")) {
            Message learned = learn(msg.text);
            remember(user, learned.text);
            return learned;
        }

        /* ---------- CONTEXT FOLLOW-UP ---------- */
        String contextAnswer = handleFollowUp(user, text);
        if (contextAnswer != null) {
            remember(user, contextAnswer);
            return new Message(BOT_NAME, contextAnswer);
        }

        /* ---------- FUZZY KNOWLEDGE ---------- */
        String ranked = findBestMatchFuzzy(text);
        if (ranked != null) {
            remember(user, ranked);
            return new Message(BOT_NAME, ranked);
        }

        /* ---------- TIME ---------- */
        if (text.contains("time")) {
            String timeReply = " Current time: " +
                    LocalDateTime.now().format(TIME_FORMAT);
            remember(user, timeReply);
            return new Message(BOT_NAME, timeReply);
        }

        /* ---------- HELP ---------- */
        if (text.contains("help")) {
            String helpText = """
                      Commands:
                    help   - show help
                    stats  - chat analysis
                    learn: question = answer

                    Example:
                    learn: what is tcp = TCP is connection-oriented
                    """;
            remember(user, helpText);
            return new Message(BOT_NAME, helpText);
        }

        /* ---------- DEFAULT ---------- */
        String defaultReply = " I don't understand yet. Try 'help' or teach me.";
        remember(user, defaultReply);
        return new Message(BOT_NAME, defaultReply);
    }

    /* ================= CONTEXT HANDLING ================= */
    private static String handleFollowUp(String user, String text) {
        String lastResponse = lastBotResponse.get(user);
        if (lastResponse == null) return null;

        if (text.contains("more") || text.contains("again") || text.contains("what about")) {
            return "You asked earlier: " + lastResponse;
        }
        return null;
    }

    /* ================= MEMORY ================= */
    private static void remember(String user, String response) {
        lastBotResponse.put(user, response);
    }

    /* ================= LOGGING ================= */
    private static void logMessage(String user, String text) {

        chatHistory.add(user + ": " + text);
        userMessageCount.put(user,
                userMessageCount.getOrDefault(user, 0) + 1);

        userHistory.putIfAbsent(user, new LinkedList<>());
        List<String> history = userHistory.get(user);
        history.add(text);
        if (history.size() > 5) history.remove(0);

        for (String word : text.split("\\s+")) {
            if (word.length() > 2)
                keywordFrequency.put(word,
                        keywordFrequency.getOrDefault(word, 0) + 1);
        }
    }

    /* ================= STATS ================= */
    private static String generateStats() {
        StringBuilder sb = new StringBuilder();

        sb.append("📊 CHAT STATISTICS\n");
        sb.append("------------------\n");
        sb.append("Total messages: ").append(chatHistory.size()).append("\n\n");

        sb.append("Messages per user:\n");
        userMessageCount.forEach((k, v) ->
                sb.append("- ").append(k).append(": ").append(v).append("\n"));

        sb.append("\nTop keywords:\n");
        keywordFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e ->
                        sb.append("- ").append(e.getKey())
                          .append(" (").append(e.getValue()).append(")\n"));

        return sb.toString();
    }

    /* ================= LEARNING ================= */
    private static Message learn(String raw) {
        String content = raw.substring(6).trim();
        String[] parts = content.split("=", 2);

        if (parts.length != 2)
            return new Message(BOT_NAME,
                    "❌ Use format: learn: question = answer");

        knowledgeBase.put(preprocess(parts[0]), parts[1].trim());
        return new Message(BOT_NAME, " Learned successfully!");
    }

    /* ================= FUZZY MATCH ================= */
    private static String findBestMatchFuzzy(String input) {
        String bestAnswer = null;
        int bestScore = Integer.MAX_VALUE;

        for (String question : knowledgeBase.keySet()) {
            int distance = levenshteinDistance(input, question);
            if (distance < bestScore) {
                bestScore = distance;
                bestAnswer = knowledgeBase.get(question);
            }
        }
        return bestScore <= Math.max(3, input.length() / 3)
                ? bestAnswer : null;
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                dp[i][j] = (a.charAt(i - 1) == b.charAt(j - 1))
                        ? dp[i - 1][j - 1]
                        : 1 + Math.min(dp[i - 1][j - 1],
                        Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[a.length()][b.length()];
    }

    private static String preprocess(String input) {
        return input.toLowerCase().trim();
    }
}
