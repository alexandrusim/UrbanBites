export interface Reservation {
  reservationId?: number;
  userId?: number;
  restaurantId: number;
  tableId: number;
  reservationDate: string;
  reservationTime: string;
  durationMinutes?: number;
  numberOfGuests: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW';
  specialRequests?: string;
  confirmationCode?: string;
  cancelledAt?: string;
  cancellationReason?: string;
  checkedInAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ReservationCreateRequest {
  userId: number;
  restaurantId: number;
  tableId: number;
  reservationDate: string;
  reservationTime: string;
  numberOfGuests: number;
  durationMinutes?: number;
  specialRequests?: string;
}

export interface ReservationUpdateRequest {
  status?: string;
  numberOfGuests?: number;
  specialRequests?: string;
  cancellationReason?: string;
}

export interface GuestReservationRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  restaurantId: number;
  tableId?: number;
  reservationDate: string;
  reservationTime: string;
  numberOfGuests: number;
  specialRequests?: string;
  durationMinutes?: number;
}

export interface ReservationDetailDTO {
  reservationId?: number;
  userId?: number;
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  isGuest?: boolean;
  restaurantId?: number;
  restaurantName?: string;
  tableId?: number;
  tableNumber?: string;
  reservationDate?: string;
  reservationTime?: string;
  durationMinutes?: number;
  numberOfGuests?: number;
  status?: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW' | 'REJECTED';
  specialRequests?: string;
  confirmationCode?: string;
  createdAt?: string;
  updatedAt?: string;
  checkedInAt?: string;
  cancelledAt?: string;
  cancellationReason?: string;
}

export interface ReservationApprovalRequest {
  reservationId: number;
  status: string;
  tableId?: number;
  adminNote?: string;
  rejectionReason?: string;
}
