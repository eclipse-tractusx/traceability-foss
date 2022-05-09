/**
 * Types or status of notification messages
 * Success status - shows the notification with a green status bar, typically used to inform the user
 * warning status - shows the notification with a yellow status bar, typically used to alert the user
 * error status - shows the notification with a red status bar, typically used for error messages
 * informative status - shows the notification with a blue status bar, typically used for informative messages
 */
export const enum NotificationStatus {
  Success = 1,
  Warning = 2,
  Error = 3,
  Informative = 4,
}
