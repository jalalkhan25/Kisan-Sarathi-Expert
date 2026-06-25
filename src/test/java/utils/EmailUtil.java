package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EmailUtil - Sends colourful HTML test report via email after suite execution.
 */
public class EmailUtil {

    private static final List<TestResult> results = Collections.synchronizedList(new ArrayList<>());

    public static void addResult(String testName, String suiteName, String status, String reason, String screenshotPath) {
        results.add(new TestResult(testName, suiteName, status, reason, screenshotPath));
    }

    public static void sendReport(int totalTests, int passed, int failed, int skipped) {
        String enabled = ConfigReader.getProperty("email.enabled");
        if (!"true".equalsIgnoreCase(enabled)) return;

        String host = ConfigReader.getProperty("email.smtp.host");
        String port = ConfigReader.getProperty("email.smtp.port");
        String sender = ConfigReader.getProperty("email.sender");
        String password = ConfigReader.getProperty("email.password");
        String recipient = ConfigReader.getProperty("email.recipient");

        String overallStatus = (failed > 0) ? "FAILED" : (skipped == totalTests) ? "SKIPPED" : "PASSED";
        String statusColor = (failed > 0) ? "#e74c3c" : (skipped == totalTests) ? "#f39c12" : "#27ae60";
        String timestamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(new Date());

        String subject = "Automation Script Result - Kisan Sarathi 2.0 (KVK Expert) | " + overallStatus
                + " | Pass:" + passed + " Fail:" + failed + " Skip:" + skipped;

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family:Segoe UI,Arial;padding:20px;background:#f5f6fa;'>");
        body.append("<div style='max-width:900px;margin:0 auto;background:#fff;border-radius:12px;box-shadow:0 4px 20px rgba(0,0,0,0.1);overflow:hidden;'>");

        // Header
        body.append("<div style='background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;'>");
        body.append("<h1 style='margin:0;'>🧪 Automation Script Result</h1>");
        body.append("<p style='margin:5px 0 0;opacity:0.9;'>Kisan Sarathi 2.0 - KVK Expert Module</p>");
        body.append("</div>");

        // Info bar
        body.append("<table style='width:100%;background:#f8f9fa;border-bottom:1px solid #e9ecef;'><tr>");
        body.append("<td style='padding:15px 30px;'><small style='color:#6c757d;'>PROJECT</small><br><b>Kisan Sarathi 2.0</b></td>");
        body.append("<td style='padding:15px 30px;'><small style='color:#6c757d;'>QA ENGINEER</small><br><b>Jalal Khan</b></td>");
        body.append("<td style='padding:15px 30px;'><small style='color:#6c757d;'>DATE</small><br><b>").append(timestamp).append("</b></td>");
        body.append("<td style='padding:15px 30px;'><small style='color:#6c757d;'>ENVIRONMENT</small><br><b>Dev</b></td>");
        body.append("</tr></table>");

        // Stats
        body.append("<table style='width:100%;padding:20px 30px;'><tr>");
        body.append("<td style='text-align:center;padding:15px;background:#e8f4fd;border-radius:8px;'><div style='font-size:28px;font-weight:700;color:#0c5460;'>").append(totalTests).append("</div><div style='font-size:11px;color:#0c5460;'>TOTAL</div></td>");
        body.append("<td style='width:15px;'></td>");
        body.append("<td style='text-align:center;padding:15px;background:#d4edda;border-radius:8px;'><div style='font-size:28px;font-weight:700;color:#155724;'>").append(passed).append("</div><div style='font-size:11px;color:#155724;'>PASSED</div></td>");
        body.append("<td style='width:15px;'></td>");
        body.append("<td style='text-align:center;padding:15px;background:#f8d7da;border-radius:8px;'><div style='font-size:28px;font-weight:700;color:#721c24;'>").append(failed).append("</div><div style='font-size:11px;color:#721c24;'>FAILED</div></td>");
        body.append("<td style='width:15px;'></td>");
        body.append("<td style='text-align:center;padding:15px;background:#fff3cd;border-radius:8px;'><div style='font-size:28px;font-weight:700;color:#856404;'>").append(skipped).append("</div><div style='font-size:11px;color:#856404;'>SKIPPED</div></td>");
        body.append("</tr></table>");

        // Test case table
        body.append("<div style='padding:20px 30px 10px;font-size:16px;font-weight:600;border-top:3px solid #667eea;'>📋 Test Case Results</div>");
        body.append("<table style='width:100%;border-collapse:collapse;'><tr><th style='background:#343a40;color:white;padding:10px;text-align:left;font-size:12px;'>#</th><th style='background:#343a40;color:white;padding:10px;text-align:left;font-size:12px;'>Suite</th><th style='background:#343a40;color:white;padding:10px;text-align:left;font-size:12px;'>Test Case</th><th style='background:#343a40;color:white;padding:10px;text-align:left;font-size:12px;'>Status</th><th style='background:#343a40;color:white;padding:10px;text-align:left;font-size:12px;'>Reason</th></tr>");

        int idx = 1;
        for (TestResult r : results) {
            String statusClass = r.status.equals("PASS") ? "color:#27ae60;" : r.status.equals("FAIL") ? "color:#e74c3c;" : "color:#f39c12;";
            String icon = r.status.equals("PASS") ? "✅" : r.status.equals("FAIL") ? "❌" : "⚠️";
            String reason = (r.reason != null && !r.reason.isEmpty()) ? r.reason : "-";
            if (reason.length() > 80) reason = reason.substring(0, 80) + "...";
            String bg = (idx % 2 == 0) ? "background:#f8f9fa;" : "";

            body.append("<tr style='").append(bg).append("'><td style='padding:8px 10px;border-bottom:1px solid #e9ecef;'>").append(idx++).append("</td>");
            body.append("<td style='padding:8px 10px;border-bottom:1px solid #e9ecef;'>").append(r.suiteName).append("</td>");
            body.append("<td style='padding:8px 10px;border-bottom:1px solid #e9ecef;'>").append(r.testName).append("</td>");
            body.append("<td style='padding:8px 10px;border-bottom:1px solid #e9ecef;font-weight:600;").append(statusClass).append("'>").append(icon).append(" ").append(r.status).append("</td>");
            body.append("<td style='padding:8px 10px;border-bottom:1px solid #e9ecef;font-size:11px;color:#6c757d;'>").append(reason).append("</td></tr>");
        }
        body.append("</table>");

        body.append("<div style='background:#f8f9fa;padding:15px 30px;text-align:center;font-size:11px;color:#6c757d;border-top:1px solid #e9ecef;'>📧 Automated Report | Kisan Sarathi 2.0 KVK Expert | QA: Jalal Khan</div>");
        body.append("</div></body></html>");

        // Send
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body.toString(), "text/html; charset=utf-8");
            multipart.addBodyPart(bodyPart);

            File reportFile = new File("reports/TestReport.html");
            if (reportFile.exists()) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(reportFile);
                attachPart.setFileName("TestReport.html");
                multipart.addBodyPart(attachPart);
            }

            File screenshotDir = new File("screenshots");
            if (screenshotDir.exists() && screenshotDir.isDirectory()) {
                File[] screenshots = screenshotDir.listFiles((dir, name) -> name.contains("FAIL"));
                if (screenshots != null) {
                    int count = 0;
                    for (File ss : screenshots) {
                        if (count >= 5) break;
                        MimeBodyPart ssPart = new MimeBodyPart();
                        ssPart.attachFile(ss);
                        ssPart.setFileName(ss.getName());
                        multipart.addBodyPart(ssPart);
                        count++;
                    }
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println(">> EMAIL SENT to " + recipient + " | Subject: " + subject);
        } catch (Exception e) {
            System.out.println(">> EMAIL FAILED: " + e.getMessage());
        }
    }

    static class TestResult {
        String testName, suiteName, status, reason, screenshotPath;
        TestResult(String t, String s, String st, String r, String sp) {
            testName = t; suiteName = s; status = st; reason = r; screenshotPath = sp;
        }
    }
}
