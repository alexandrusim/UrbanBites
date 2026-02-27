export interface DashboardStats {
  totalUsers: number;
  totalRestaurants: number;
  totalReservations: number;
  pendingReservations: number;
  confirmedReservations: number;
  todayReservations: number;
}

export interface UserDTO {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  role: string;
  restaurantId?: number;
  isActive?: boolean;
  createdAt?: string;
}

export interface ReservationDTO {
  reservationId: number;
  userId?: number;
  userName?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  restaurantId: number;
  restaurantName: string;
  tableId: number;
  reservationDate: string;
  reservationTime: string;
  numberOfGuests: number;
  status: string;
  specialRequests?: string;
}

export interface ReservationStatusUpdate {
  status: string;
  reason?: string;
}

export interface ActivityLog {
  logId: number;
  userId?: number | null;
  action: string;
  endpoint?: string;
  httpMethod?: string;
  ipAddress?: string;
  userAgent?: string;
  timestamp: string;
  statusCode?: number;
  responseTimeMs?: number;
}