export interface Menu {
  menuId?: number;
  restaurantId: number;
  name: string;
  description?: string;
  menuType?: string;
  isActive?: boolean;
  validFrom?: string;
  validTo?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MenuItem {
  menuItemId?: number;
  restaurantId: number;
  itemId?: number;
  menuId?: number;
  name: string;
  description?: string;
  price: number;
  category?: string;
  imageUrl?: string;
  ingredients?: string;
  allergens?: string;
  calories?: number;
  preparationTime?: number;
  isVegetarian?: boolean;
  isVegan?: boolean;
  isGlutenFree?: boolean;
  isAvailable?: boolean;
  displayOrder?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface MenuItemCreateRequest {
  menuId: number;
  name: string;
  description?: string;
  price: number;
  category?: string;
  imageUrl?: string;
  ingredients?: string;
  allergens?: string;
  isVegetarian?: boolean;
  isVegan?: boolean;
  isGlutenFree?: boolean;
}
