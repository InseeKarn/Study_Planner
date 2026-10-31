package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import model.Task;
import view.notification.NotificationPanel;
import view.notification.NotificationWindow;

public class NotificationService {

    private static final Path EXE =
            Path.of("tools", "WinToast.exe").toAbsolutePath();

    private static final Path HISTORY =
            Path.of("data", "notification-history.properties");

    private static final String APP_NAME = "Study Planner";
    private static final String APP_ID = "StudyPlanner.App";

    private static ScheduledExecutorService scheduler;

    private static final Properties history = new Properties();

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d-M-yyyy");

    private static boolean historyLoaded = false;

    // เริ่มระบบแจ้งเตือน
    public static synchronized void start(Data data) {

        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        loadHistory();

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "StudyPlanner-Notifications");
            thread.setDaemon(true);
            return thread;
        });

        // ตรวจสอบงานทันทีเมื่อเริ่มโปรแกรม และตรวจซ้ำทุก 1 นาที
        scheduler.scheduleAtFixedRate(
                () -> checkTasks(data),
                0,
                1,
                TimeUnit.MINUTES
        );
    }

    // หยุดระบบแจ้งเตือน
    public static synchronized void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    // เมธอดสำหรับสั่งเปิด Floating Notification Window มุมขวาล่างของ Desktop โดยตรง
    public static void showDesktopNotification(Data data) {
        SwingUtilities.invokeLater(() -> {
            NotificationPanel panel = new NotificationPanel(data);
            NotificationWindow.showNotification(panel);
        });
    }

    // ตรวจสอบงานทั้งหมดเพื่อส่งแจ้งเตือน
    private static void checkTasks(Data data) {

        try {
            LocalDate today = LocalDate.now();

            List<Task> tasks = data.getAllTasks();
            boolean hasNewNotification = false;

            for (Task task : tasks) {

                if (task.isCompleted()) {
                    continue;
                }

                LocalDate dueDate =
                        Task.parseDueDate(task.getDueDate());

                String taskKey = createTaskKey(task);

                // 1. งานติดดาว: แจ้งเตือนวันละครั้ง
                if (task.isImportant()) {
                    if (notifyOnce(
                            task,
                            taskKey,
                            "STARRED",
                            "Starred task",
                            "This task is marked as important."
                    )) {
                        hasNewNotification = true;
                    }
                }

                if (dueDate == null) {
                    continue;
                }

                // 2. งานที่เลยกำหนดส่ง: แจ้งเตือนวันละครั้ง
                if (dueDate.isBefore(today)) {
                    if (notifyOnce(
                            task,
                            taskKey,
                            "OVERDUE",
                            "Overdue task",
                            "This task is past its due date."
                    )) {
                        hasNewNotification = true;
                    }
                }

                // 3. งานที่เหลืออีก 1 วัน
                if (dueDate.equals(today.plusDays(1))) {
                    if (notifyOnce(
                            task,
                            taskKey,
                            "TOMORROW",
                            "Due tomorrow",
                            "This task is due in 1 day."
                    )) {
                        hasNewNotification = true;
                    }
                }
            }

            // หากมีรายการแจ้งเตือนใหม่ ให้เรียกแสดง Notification Window มุมขวาล่างของ Desktop
            if (hasNewNotification) {
                showDesktopNotification(data);
            }

        } catch (Exception e) {
            System.err.println("Notification check failed:");
            e.printStackTrace();
        }
    }

    // ป้องกันการแจ้งซ้ำในวันเดียวกัน
    private static boolean notifyOnce(
            Task task,
            String taskKey,
            String type,
            String notificationTitle,
            String message
    ) {

        String today = LocalDate.now().toString();

        String historyKey = today + "_" + type + "_" + taskKey;

        if (history.containsKey(historyKey)) {
            return false;
        }

        String due = task.getDueDate() == null
                ? "No due date"
                : task.getDueDate();

        String text = notificationTitle
                + ": " + task.getTitle()
                + " | Subject: " + task.getSubject()
                + " | Due: " + due
                + " | " + message;

        // ปิดการเรียกใช้ Windows Toast แบบดั้งเดิม เพื่อไม่ให้เด้งป๊อปอัปซ้อนกัน
        // showToastNotification(text);

        // บันทึกประวัติ
        history.setProperty(historyKey, "sent");
        saveHistory();
        return true;
    }

    // ส่ง Windows Toast ผ่าน WinToast.exe (คงไว้เป็น utility เผื่อเรียกใช้ในอนาคต)
    private static boolean showToastNotification(String message) {

        try {
            if (!Files.exists(EXE)) {
                System.err.println("WinToast.exe not found: " + EXE);
                return false;
            }

            ProcessBuilder pb = new ProcessBuilder(
                    EXE.toString(),
                    "--appname", APP_NAME,
                    "--appid", APP_ID,
                    "--text", message
            );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.err.println(
                        "WinToast failed. Exit code: " + exitCode
                );
                return false;
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // สร้างรหัสประจำงานจากข้อมูลที่มีอยู่
    private static String createTaskKey(Task task) {

        String raw = String.valueOf(task.getSubject())
                + "|" + String.valueOf(task.getTitle())
                + "|" + String.valueOf(task.getDueDate());

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(raw.getBytes("UTF-8"));

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // โหลดประวัติการแจ้งเตือน
    private static void loadHistory() {

        if (historyLoaded) {
            return;
        }

        try {
            if (Files.exists(HISTORY)) {
                try (var input = Files.newInputStream(HISTORY)) {
                    history.load(input);
                }
            }

            historyLoaded = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // บันทึกประวัติการแจ้งเตือน
    private static synchronized void saveHistory() {

        try {
            Files.createDirectories(HISTORY.getParent());

            try (var output = Files.newOutputStream(HISTORY)) {
                history.store(output, "Study Planner Notification History");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}