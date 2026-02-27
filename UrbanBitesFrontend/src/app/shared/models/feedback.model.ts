export interface Feedback {
  feedbackId?: number;
  userId: number;
  restaurantId: number;
  reservationId?: number;
  rating: number;
  comment?: string;
  foodRating?: number;
  serviceRating?: number;
  ambianceRating?: number;
  valueRating?: number;
  isVisible?: boolean;
  adminResponse?: string;
  respondedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface FeedbackCreateRequest {
  userId: number;
  restaurantId: number;
  reservationId?: number;
  rating: number;
  comment?: string;
  foodRating?: number;
  serviceRating?: number;
  ambianceRating?: number;
  valueRating?: number;
}
