export interface Notification {
  notificationId: number; 
  userId: number;
  message: string;
  isRead: boolean;
  sentAt: string; 
  type?: string; 
}

export interface NotificationCreateRequest {
  userId: number;
  message: string;
  type?: string;
  isRead?: boolean;
}