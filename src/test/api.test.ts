import { describe, it, expect, beforeEach, vi } from 'vitest';

// Mock fetch
const mockFetch = vi.fn();
global.fetch = mockFetch;

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};
global.localStorage = localStorageMock;

// Import after mocks
import { recipeApi, favoriteApi, authApi, getCurrentUser, isLoggedIn, logout } from '../services/api';

describe('API Service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);
  });

  describe('Authentication', () => {
    it('getCurrentUser should return null when no user in storage', () => {
      localStorageMock.getItem.mockReturnValue(null);
      expect(getCurrentUser()).toBeNull();
    });

    it('getCurrentUser should return parsed user object', () => {
      const mockUser = { id: 1, username: 'test', nickname: 'Test User' };
      localStorageMock.getItem.mockReturnValue(JSON.stringify(mockUser));
      expect(getCurrentUser()).toEqual(mockUser);
    });

    it('isLoggedIn should return false when no token', () => {
      localStorageMock.getItem.mockReturnValue(null);
      expect(isLoggedIn()).toBe(false);
    });

    it('isLoggedIn should return true when token exists', () => {
      localStorageMock.getItem.mockReturnValue('mock-token');
      expect(isLoggedIn()).toBe(true);
    });

    it('logout should clear all auth data', () => {
      logout();
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('accessToken');
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('refreshToken');
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('user');
    });
  });

  describe('Recipe API', () => {
    it('getAll should call API with pagination params', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          list: [{ id: 1, title: 'Test Recipe' }],
          total: 10,
          page: 1,
          pageSize: 10,
          totalPages: 1,
        },
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      });

      const result = await recipeApi.getAll(1, 10);

      expect(mockFetch).toHaveBeenCalledWith('/api/recipes?page=1&pageSize=10', expect.any(Object));
      expect(result.list).toHaveLength(1);
      expect(result.total).toBe(10);
    });

    it('getById should return recipe detail', async () => {
      const mockDetail = {
        recipe: { id: 1, title: 'Test Recipe' },
        ingredients: [{ name: 'Ingredient 1', amount: '100g' }],
        steps: [{ stepNumber: 1, content: 'Step 1' }],
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ code: 200, message: 'success', data: mockDetail }),
      });

      const result = await recipeApi.getById(1);

      expect(result.recipe.title).toBe('Test Recipe');
      expect(result.ingredients).toHaveLength(1);
    });

    it('create should send recipe data', async () => {
      const mockRecipe = { title: 'New Recipe' };
      const mockResult = { id: 1, ...mockRecipe };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ code: 200, message: 'success', data: mockResult }),
      });

      const result = await recipeApi.create({
        recipe: mockRecipe,
        ingredients: [],
        steps: [],
      });

      expect(result.id).toBe(1);
      expect(mockFetch).toHaveBeenCalledWith(
        '/api/recipes',
        expect.objectContaining({ method: 'POST' })
      );
    });
  });

  describe('Favorite API', () => {
    it('getUserFavorites should return favorites list', async () => {
      const mockFavorites = [{ id: 1, title: 'Fav Recipe' }];

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ code: 200, message: 'success', data: mockFavorites }),
      });

      const result = await favoriteApi.getUserFavorites(1);

      expect(result).toHaveLength(1);
      expect(mockFetch).toHaveBeenCalledWith('/api/favorites/user/1', expect.any(Object));
    });

    it('add should POST favorite data', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ code: 200, message: 'success', data: null }),
      });

      await favoriteApi.add(1, 1);

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/favorites',
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify({ userId: 1, recipeId: 1 }),
        })
      );
    });

    it('remove should DELETE favorite', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ code: 200, message: 'success', data: null }),
      });

      await favoriteApi.remove(1, 1);

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/favorites?userId=1&recipeId=1',
        expect.objectContaining({ method: 'DELETE' })
      );
    });
  });

  describe('Auth API', () => {
    it('login should return auth result with tokens', async () => {
      const mockAuthResult = {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        user: { id: 1, username: 'test' },
        expiresIn: 86400,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ code: 200, message: 'success', data: mockAuthResult }),
      });

      const result = await authApi.login('testuser', 'password123');

      expect(result.accessToken).toBe('mock-access-token');
      expect(result.user.username).toBe('test');
    });

    it('register should send registration data', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({
          code: 200,
          message: 'success',
          data: { user: { id: 1, username: 'newuser' } },
        }),
      });

      await authApi.register('newuser', 'password123', 'New User');

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/auth/register',
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify({
            username: 'newuser',
            password: 'password123',
            nickname: 'New User',
          }),
        })
      );
    });
  });

  describe('Upload API', () => {
    it('uploadImage should send multipart form data', async () => {
      const mockUrl = 'http://localhost:8080/uploads/2024/01/01/test.jpg';

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({
          code: 200,
          message: 'success',
          data: { url: mockUrl },
        }),
      });

      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      const result = await uploadApi.uploadImage(file);

      expect(result).toBe(mockUrl);
    });
  });
});
