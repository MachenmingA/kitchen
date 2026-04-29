import { useState, useEffect, useCallback } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Header } from './components/Header';
import { HomePage } from './pages/HomePage';
import { CategoriesPage } from './pages/CategoriesPage';
import { FavoritesPage } from './pages/FavoritesPage';
import { RecipePage } from './pages/RecipePage';
import { LoginPage } from './pages/LoginPage';
import { ProfilePage } from './pages/ProfilePage';
import { CreateRecipePage } from './pages/CreateRecipePage';
import { RegisterPage } from './pages/RegisterPage';
import { recipeApi, favoriteApi, getCurrentUser, isLoggedIn, Recipe, logout } from './services/api';
import './styles/globals.css';

function App() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [user, setUser] = useState(getCurrentUser());

  const fetchRecipes = useCallback(async () => {
    try {
      const data = await recipeApi.getAll();
      const currentUser = getCurrentUser();
      const userId = currentUser?.id || 1;

      const recipesWithFavorite = await Promise.all(
        data.map(async (recipe) => ({
          ...recipe,
          isFavorited: currentUser ? await favoriteApi.check(userId, recipe.id) : false,
        }))
      );
      setRecipes(recipesWithFavorite);
    } catch (error) {
      console.error('Failed to fetch recipes:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchRecipes();
    setUser(getCurrentUser());
  }, [fetchRecipes]);

  const handleFavorite = async (id: number) => {
    const recipe = recipes.find((r) => r.id === id);
    if (!recipe) return;

    if (!isLoggedIn()) {
      alert('请先登录');
      return;
    }

    const currentUser = getCurrentUser();
    if (!currentUser) return;

    try {
      if (recipe.isFavorited) {
        await favoriteApi.remove(currentUser.id, id);
      } else {
        await favoriteApi.add(currentUser.id, id);
      }
      setRecipes((prev) =>
        prev.map((r) =>
          r.id === id ? { ...r, isFavorited: !r.isFavorited } : r
        )
      );
    } catch (error) {
      console.error('Failed to toggle favorite:', error);
    }
  };

  const handleLogout = () => {
    logout();
    setUser(null);
    window.location.reload();
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Header
        onSearch={setSearchQuery}
        user={user}
        onLogout={handleLogout}
      />
      <main>
        <Routes>
          <Route
            path="/"
            element={
              <HomePage
                recipes={recipes}
                onFavorite={handleFavorite}
                searchQuery={searchQuery}
              />
            }
          />
          <Route
            path="/categories"
            element={
              <CategoriesPage recipes={recipes} onFavorite={handleFavorite} />
            }
          />
          <Route
            path="/favorites"
            element={
              isLoggedIn() ? (
                <FavoritesPage recipes={recipes} onFavorite={handleFavorite} />
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />
          <Route
            path="/recipe/:id"
            element={<RecipePage recipes={recipes} onFavorite={handleFavorite} />}
          />
          <Route
            path="/login"
            element={isLoggedIn() ? <Navigate to="/" replace /> : <LoginPage />}
          />
          <Route
            path="/register"
            element={isLoggedIn() ? <Navigate to="/" replace /> : <RegisterPage />}
          />
          <Route
            path="/profile"
            element={isLoggedIn() ? <ProfilePage /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/create-recipe"
            element={isLoggedIn() ? <CreateRecipePage /> : <Navigate to="/login" replace />}
          />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
