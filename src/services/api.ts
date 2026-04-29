const API_BASE = '/api';

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem('accessToken');
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${url}`, {
    headers: getAuthHeaders(),
    ...options,
  });
  const result: ApiResponse<T> = await response.json();
  if (result.code !== 200) {
    throw new Error(result.message || '请求失败');
  }
  return result.data;
}

export function getCurrentUser(): User | null {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
}

export function isLoggedIn(): boolean {
  return !!localStorage.getItem('accessToken');
}

export function logout(): void {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
}

export interface AuthResult {
  accessToken: string;
  refreshToken: string;
  user: User;
  expiresIn: number;
}

export interface Recipe {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  category: string;
  difficulty: string;
  cookTime: number;
  servings: number;
  authorId: number;
  authorName: string;
  authorAvatar: string;
  favoritesCount: number;
  viewsCount: number;
  isFavorited?: boolean;
}

export interface Ingredient {
  id?: number;
  recipeId?: number;
  name: string;
  amount: string;
  sortOrder?: number;
}

export interface Step {
  id?: number;
  recipeId?: number;
  stepNumber: number;
  content: string;
  imageUrl?: string;
}

export interface Comment {
  id: number;
  recipeId: number;
  userId: number;
  userNickname: string;
  userAvatar: string;
  content: string;
  createTime: string;
}

export interface User {
  id: number;
  username: string;
  nickname: string;
  avatar: string;
  bio: string;
}

export interface RecipeDetail {
  recipe: Recipe;
  ingredients: Ingredient[];
  steps: Step[];
}

export const recipeApi = {
  getAll: () => request<Recipe[]>('/recipes'),

  getById: (id: number) => request<RecipeDetail>(`/recipes/${id}`),

  getByCategory: (category: string) => request<Recipe[]>(`/recipes/category/${category}`),

  search: (keyword: string) => request<Recipe[]>(`/recipes/search?keyword=${encodeURIComponent(keyword)}`),

  getPopular: (limit = 10) => request<Recipe[]>(`/recipes/popular?limit=${limit}`),

  getUserRecipes: (userId: number) => request<Recipe[]>(`/recipes/user/${userId}`),

  create: (data: { recipe: Partial<Recipe>; ingredients: any[]; steps: any[] }) =>
    request<Recipe>('/recipes', { method: 'POST', body: JSON.stringify(data) }),

  update: (id: number, data: { recipe: Partial<Recipe>; ingredients: any[]; steps: any[] }) =>
    request<Recipe>(`/recipes/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  delete: (id: number) => request<void>(`/recipes/${id}`, { method: 'DELETE' }),
};

export const favoriteApi = {
  getUserFavorites: (userId: number) => request<Recipe[]>(`/favorites/user/${userId}`),
  
  check: (userId: number, recipeId: number) => request<boolean>(`/favorites/check?userId=${userId}&recipeId=${recipeId}`),
  
  add: (userId: number, recipeId: number) => 
    request<void>('/favorites', { method: 'POST', body: JSON.stringify({ userId, recipeId }) }),
  
  remove: (userId: number, recipeId: number) => 
    request<void>(`/favorites?userId=${userId}&recipeId=${recipeId}`, { method: 'DELETE' }),
};

export const commentApi = {
  getByRecipe: (recipeId: number) => request<Comment[]>(`/comments/recipe/${recipeId}`),
  
  getRecent: (limit = 10) => request<Comment[]>(`/comments/recent?limit=${limit}`),
  
  add: (comment: Omit<Comment, 'id' | 'createTime'>) => 
    request<Comment>('/comments', { method: 'POST', body: JSON.stringify(comment) }),
  
  delete: (id: number, userId: number) => 
    request<void>(`/comments/${id}?userId=${userId}`, { method: 'DELETE' }),
};

export const userApi = {
  getById: (id: number) => request<User>(`/users/${id}`),

  getByUsername: (username: string) => request<User>(`/users/username/${username}`),

  update: (id: number, user: Partial<User>) =>
    request<User>(`/users/${id}`, { method: 'PUT', body: JSON.stringify(user) }),
};

export const authApi = {
  login: (username: string, password: string) =>
    request<AuthResult>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),

  register: (username: string, password: string, nickname?: string) =>
    request<{ user: User }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, nickname }),
    }),

  refresh: (refreshToken: string) =>
    request<AuthResult>('/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
    }),

  logout: () =>
    request<void>('/auth/logout', { method: 'POST' }),
};

// 标签 API
export const tagApi = {
  getAll: () => request<Tag[]>('/tags'),
  getPopular: (limit = 10) => request<Tag[]>(`/tags/popular?limit=${limit}`),
  getByRecipe: (recipeId: number) => request<Tag[]>(`/tags/recipe/${recipeId}`),
  create: (name: string) => request<Tag>('/tags', { method: 'POST', body: JSON.stringify({ name }) }),
};

// 评分 API
export const ratingApi = {
  getStats: (recipeId: number) => request<{ averageScore: number; totalCount: number }>(`/ratings/recipe/${recipeId}`),
  getUserRating: (recipeId: number, userId: number) => request<number | null>(`/ratings/recipe/${recipeId}/user/${userId}`),
  rate: (recipeId: number, userId: number, score: number) =>
    request<{ averageScore: number; totalCount: number }>('/ratings', {
      method: 'POST',
      body: JSON.stringify({ recipeId, userId, score }),
    }),
};

// 关注 API
export const followApi = {
  follow: (followerId: number, followingId: number) =>
    request<void>('/follow', { method: 'POST', body: JSON.stringify({ followerId, followingId }) }),
  unfollow: (followerId: number, followingId: number) =>
    request<void>(`/follow?followerId=${followerId}&followingId=${followingId}`, { method: 'DELETE' }),
  isFollowing: (followerId: number, followingId: number) =>
    request<boolean>(`/follow/check?followerId=${followerId}&followingId=${followingId}`),
  getStats: (userId: number) => request<{ followingCount: number; followersCount: number }>(`/follow/stats/${userId}`),
  getFollowing: (userId: number) => request<User[]>(`/follow/following/${userId}`),
  getFollowers: (userId: number) => request<User[]>(`/follow/followers/${userId}`),
};

// 动态 API
export const feedApi = {
  getRecent: (limit = 20) => request<Feed[]>(`/feeds?limit=${limit}`),
  getUserFeeds: (userId: number) => request<Feed[]>(`/feeds/user/${userId}`),
  create: (userId: number, content: string, imageUrl?: string) =>
    request<Feed>('/feeds', { method: 'POST', body: JSON.stringify({ userId, content, imageUrl }) }),
  delete: (id: number, userId: number) => request<void>(`/feeds/${id}?userId=${userId}`, { method: 'DELETE' }),
  like: (id: number, userId: number) => request<void>(`/feeds/${id}/like?userId=${userId}`, { method: 'POST' }),
  unlike: (id: number, userId: number) => request<void>(`/feeds/${id}/like?userId=${userId}`, { method: 'DELETE' }),
};

// 通知 API
export const notificationApi = {
  getList: (userId: number, limit = 20) => request<Notification[]>(`/notifications?userId=${userId}&limit=${limit}`),
  getUnreadCount: (userId: number) => request<number>(`/notifications/unread-count?userId=${userId}`),
  markAsRead: (id: number) => request<void>(`/notifications/${id}/read`, { method: 'POST' }),
  markAllAsRead: (userId: number) => request<void>(`/notifications/read-all?userId=${userId}`, { method: 'POST' }),
  delete: (id: number) => request<void>(`/notifications/${id}`, { method: 'DELETE' }),
};

// 扩展类型
export interface Tag {
  id: number;
  name: string;
  recipeCount: number;
}

export interface Feed {
  id: number;
  userId: number;
  content: string;
  imageUrl?: string;
  createTime: string;
  authorNickname?: string;
  authorAvatar?: string;
  likesCount?: number;
}

export interface Notification {
  id: number;
  userId: number;
  type: string;
  title: string;
  content: string;
  isRead: number;
  createTime: string;
}
