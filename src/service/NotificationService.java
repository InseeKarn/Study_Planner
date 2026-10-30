
package service;

import dorkbox.notify.Notify;
import dorkbox.notify.Position;
import dorkbox.notify.Theme;

public class NotificationService {

    public static void showTestNotification() {

        Notify.Companion.create()
                .title("Study Planner")
                .text("Notification is working!")
                .theme(Theme.Companion.getDefaultDark())
                .position(Position.BOTTOM_RIGHT)
                .hideAfter(5000)
                .showWarning();
    }

    public static void main(String[] args) {
        showTestNotification();
    }
}
