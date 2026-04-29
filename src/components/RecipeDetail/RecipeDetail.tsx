import { Heart, Clock, Users, ChefHat } from 'lucide-react';
import { Recipe } from '../../services/api';
import styles from './RecipeDetail.module.css';

interface RecipeDetailProps {
  recipe: Recipe & { isFavorited?: boolean; ingredients?: any[]; steps?: any[] };
  onFavorite: () => void;
  onBack: () => void;
}

export function RecipeDetail({ recipe, onFavorite, onBack }: RecipeDetailProps) {
  return (
    <div className={styles.container}>
      <button className={styles.backButton} onClick={onBack}>
        返回
      </button>

      <div className={styles.hero}>
        <img src={recipe.imageUrl} alt={recipe.title} className={styles.heroImage} />
        <div className={styles.heroOverlay}>
          <h1 className={styles.title}>{recipe.title}</h1>
          <p className={styles.description}>{recipe.description}</p>
        </div>
      </div>

      <div className={styles.content}>
        <div className={styles.meta}>
          <div className={styles.author}>
            <img
              src={recipe.authorAvatar}
              alt={recipe.authorName}
              className={styles.avatar}
            />
            <div className={styles.authorInfo}>
              <span className={styles.authorName}>{recipe.authorName}</span>
              <span className={styles.authorLabel}>作者</span>
            </div>
          </div>

          <div className={styles.stats}>
            <div className={styles.stat}>
              <Clock size={18} />
              <span>烹饪 {recipe.cookTime} 分钟</span>
            </div>
            <div className={styles.stat}>
              <Users size={18} />
              <span>{recipe.servings} 人份</span>
            </div>
          </div>

          <button
            className={`${styles.favoriteButton} ${recipe.isFavorited ? styles.favorited : ''}`}
            onClick={onFavorite}
          >
            <Heart size={20} fill={recipe.isFavorited ? 'currentColor' : 'none'} />
            <span>{recipe.favoritesCount} 收藏</span>
          </button>
        </div>

        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>
            <ChefHat size={22} />
            食材清单
          </h2>
          <ul className={styles.ingredientList}>
            {recipe.ingredients?.map((ingredient, index) => (
              <li key={index} className={styles.ingredient}>
                <span className={styles.ingredientName}>{ingredient.name}</span>
                <span className={styles.ingredientAmount}>{ingredient.amount}</span>
              </li>
            ))}
          </ul>
        </section>

        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>烹饪步骤</h2>
          <ol className={styles.stepList}>
            {recipe.steps?.map((step, index) => (
              <li key={index} className={styles.step}>
                <span className={styles.stepNumber}>{step.stepNumber || index + 1}</span>
                <p className={styles.stepText}>{step.content}</p>
              </li>
            ))}
          </ol>
        </section>
      </div>
    </div>
  );
}
