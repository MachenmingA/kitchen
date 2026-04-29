export interface Recipe {
  id: string;
  title: string;
  description: string;
  author: {
    id: string;
    name: string;
    avatar: string;
  };
  image: string;
  category: string;
  tags: string[];
  prepTime: number;
  cookTime: number;
  servings: number;
  difficulty: 'easy' | 'medium' | 'hard';
  ingredients: Ingredient[];
  steps: string[];
  likes: number;
  isFavorited: boolean;
}

export interface Ingredient {
  name: string;
  amount: string;
}

export interface Category {
  id: string;
  name: string;
  icon: string;
  image: string;
}

export interface Author {
  id: string;
  name: string;
  avatar: string;
  bio: string;
  recipesCount: number;
  followers: number;
}
