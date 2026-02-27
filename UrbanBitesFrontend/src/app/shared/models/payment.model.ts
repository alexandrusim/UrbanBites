export interface Payment {
  paymentId?: number;
  reservationId: number;
  userId: number;
  amount: number;
  currency: string;
  paymentMethod: 'CARD' | 'CASH' | 'ONLINE';
  paymentStatus: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  paymentProvider?: string;
  paymentDetails?: string;
  createdAt?: string;
  updatedAt?: string;
  paidAt?: string;
  processedAt?: string;
}

export interface PaymentCreateRequest {
  reservationId: number;
  userId: number;
  amount: number;
  currency: string;
  paymentMethod: string;
  paymentStatus: string;
  paymentProvider: string;
}