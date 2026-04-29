import { Heart, Clock, Users } from 'lucide-react';
import { Recipe } from '../../services/api';
import styles from './RecipeCard.module.css';

interface RecipeCardProps {
  recipe: Recipe;
  onFavorite: (id: number) => void;
  onClick: () => void;
}

export function RecipeCard({ recipe, onFavorite, onClick }: RecipeCardProps) {
  const handleFavorite = (e: React.MouseEvent) => {
    e.stopPropagation();
    onFavorite(recipe.id);
  };

  const getDifficultyText = (difficulty: string) => {
    switch (difficulty) {
      case 'easy': return '简单';
      case 'medium': return '中等';
      case 'hard': return '困难';
      default: return difficulty;
    }
  };

  return (
    <article className={styles.card} onClick={onClick}>
      <div className={styles.imageWrapper}>
        <img src={recipe.imageUrl} alt={recipe.title} className={styles.image} />
        <button
          className={`${styles.favoriteButton} ${(recipe as any).isFavorited ? styles.favorited : ''}`}
          onClick={handleFavorite}
          aria-label={(recipe as any).isFavorited ? '取消收藏' : '收藏'}
        >
          <Heart
            size={20}
            fill={(recipe as any).isFavorited ? 'currentColor' : 'none'}
          />
        </button>
        <span className={`${styles.difficulty} ${styles[recipe.difficulty] || ''}`}>
          {getDifficultyText(recipe.difficulty)}
        </span>
      </div>
      <div className={styles.content}>
        <h3 className={styles.title}>{recipe.title}</h3>
        <p className={styles.description}>{recipe.description}</p>
        <div className={styles.meta}>
          <div className={styles.author}>
            <img src={recipe.authorAvatar} alt={recipe.authorName} className={styles.avatar} />
            <span className={styles.authorName}>{recipe.authorName}</span>
          </div>
          <div className={styles.stats}>
            <span className={styles.stat}>
              <Clock size={14} />
              {recipe.cookTime}分钟
            </span>
            <span className={styles.stat}>
              <Users size={14} />
              {recipe.servings}人份
            </span>
          </div>
        </div>
      </div>
    </article>
  );
}
