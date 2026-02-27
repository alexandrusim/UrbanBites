export interface Table {
  tableId?: number;
  restaurantId: number;
  tableNumber: string;
  capacity: number;
  location?: string;
  isAvailable?: boolean;
  positionX?: number;
  positionY?: number;
  shape?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface TableCreateRequest {
  restaurantId: number;
  tableNumber: string;
  capacity: number;
  location?: string;
  positionX?: number;
  positionY?: number;
  shape?: string;
}
