import { useNavigate } from 'react-router-dom';
import { Heart } from 'lucide-react';
import { RecipeCard } from '../../components/RecipeCard';
import { Recipe } from '../../services/api';
import styles from './FavoritesPage.module.css';

interface FavoritesPageProps {
  recipes: Recipe[];
  onFavorite: (id: number) => void;
}

export function FavoritesPage({ recipes, onFavorite }: FavoritesPageProps) {
  const navigate = useNavigate();
  const favoritedRecipes = recipes.filter((r) => (r as any).isFavorited);

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <div className={styles.header}>
          <h1 className={styles.title}>我的收藏</h1>
          <span className={styles.count}>{favoritedRecipes.length} 个收藏食谱</span>
        </div>

        {favoritedRecipes.length > 0 ? (
          <div className={styles.grid}>
            {favoritedRecipes.map((recipe, index) => (
              <div
                key={recipe.id}
                className={styles.cardWrapper}
                style={{ animationDelay: `${index * 80}ms` }}
              >
                <RecipeCard
                  recipe={recipe}
                  onFavorite={onFavorite}
                  onClick={() => navigate(`/recipe/${recipe.id}`)}
                />
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.emptyState}>
            <Heart size={64} className={styles.emptyIcon} />
            <h3>还没有收藏任何食谱</h3>
            <p>浏览食谱，点击心形图标添加收藏</p>
          </div>
        )}
      </div>
    </div>
  );
}
