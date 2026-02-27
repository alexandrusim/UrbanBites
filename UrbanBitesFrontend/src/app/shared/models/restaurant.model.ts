export interface Restaurant {
  restaurantId?: number;
  name: string;
  description?: string;
  address: string;
  city: string;
  postalCode?: string;
  country?: string;
  phoneNumber: string;
  email?: string;
  website?: string;
  logoUrl?: string;
  coverImageUrl?: string;
  svgLayout?: string;
  cuisineType?: string;
  priceRange?: string;
  openingHours?: string;
  averageRating?: number;
  totalReviews?: number;
  capacity?: number;
  hasParking?: boolean;
  hasWifi?: boolean;
  acceptsReservations?: boolean;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface RestaurantCreateRequest {
  name: string;
  description?: string;
  address: string;
  city: string;
  phoneNumber: string;
  email?: string;
  cuisineType?: string;
  priceRange?: string;
}
