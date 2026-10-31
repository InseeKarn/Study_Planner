package view.notification;

public class TaskSummary {
    private final int todayCount;
    private final int overdueCount;
    private final int inProgressCount;
    private final int totalPendingCount;

    public TaskSummary(int todayCount, int overdueCount, int inProgressCount, int totalPendingCount) {
        this.todayCount = todayCount;
        this.overdueCount = overdueCount;
        this.inProgressCount = inProgressCount;
        this.totalPendingCount = totalPendingCount;
    }

    public int getTodayCount() { return todayCount; }
    public int getOverdueCount() { return overdueCount; }
    public int getInProgressCount() { return inProgressCount; }
    public int getTotalPendingCount() { return totalPendingCount; }
}